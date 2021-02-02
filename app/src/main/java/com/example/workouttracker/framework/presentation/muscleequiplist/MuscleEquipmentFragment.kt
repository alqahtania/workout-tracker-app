package com.example.workouttracker.framework.presentation.muscleequiplist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.workouttracker.R
import com.example.workouttracker.domain.model.muscle_equipment.MuscleEquipmentFactory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.presentation.muscleequiplist.state.MuscleEquipmentStateEvent
import com.example.workouttracker.framework.presentation.ui.WorkoutTrackerTheme
import com.example.workouttracker.framework.presentation.util.Constants.AUTHORITIES
import dagger.hilt.android.AndroidEntryPoint
import java.lang.RuntimeException
import javax.inject.Inject


@AndroidEntryPoint
class MuscleEquipmentFragment : Fragment() {

    @Inject
    lateinit var muscleEquipmentFactory: MuscleEquipmentFactory

    @Inject
    lateinit var dateUtil: DateUtil

    private val viewModel: MuscleEquipmentViewModel by viewModels(
        ownerProducer = {requireActivity()}
    )

    val args: MuscleEquipmentFragmentArgs by navArgs()


    var cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            // delete the old file to assure one image per item
            viewModel.viewState.value?.newPhotoDetail?.let { photoDetail ->
                photoDetail.currentMuscleEquipId?.let { currentMuscleEquipId ->
                    photoDetail.currentPhotoFile?.let { currentPhotoFile ->
                        photoDetail.oldPhotoFile?.let { oldFilePath ->
                            viewModel.deletePhotoFile(oldFilePath)
                            viewModel.deleteOldPhotoFilesForAnEquipExcept(
                                exceptFile = currentPhotoFile,
                                muscleEquipId = currentMuscleEquipId
                            )

                        }

                        viewModel.addPhotoPathAndMuscleEquipId(
                            muscleEquipId = currentMuscleEquipId,
                            photoFile = currentPhotoFile
                        )
                    }
                }

            }


        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // when opening the camera the app will create a temporary file even if photo is cancelled
            // we need to delete the new one which is empty and keep the old one

            viewModel.viewState.value?.newPhotoDetail?.let {
                it.currentPhotoFile?.let {
                    viewModel.deletePhotoFile(it)
                }
            }

        }
        viewModel.setPhotoDetails(null)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
        viewModel.setCurrentMuscleId(args.muscleId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {

                WorkoutTrackerTheme(args.themeState) {
                    Scaffold(topBar = {
                        AppBar(title = "${args.muscleName} Equipments",
                            onIconClicked = {
                                findNavController()
                                    .popBackStack()
                            })
                    }) { innerPadding ->
                        Column {
                            MuscleEquipmentFragmentScreen(viewModel = viewModel)
                        }

                    }
                }

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
    }


    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
            if (viewState != null) {
                viewState.newPhotoForMuscleEquip?.let { muscleEquip ->
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    // we don't want to create the new file if there is no camera app
                    if (intent.resolveActivity(requireContext().packageManager) != null) {
                        viewModel.createNewPhotoFileName(muscleEquip.id)
                    }
                }

                viewState.newPhotoDetail?.let { photoDetail ->
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                    // So as long as the result is not null, it's safe to use the intent.
                    if (intent.resolveActivity(requireContext().packageManager) != null) {
                        photoDetail.imageUri?.let {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, it)
                            cameraLauncher.launch(intent)
                        }
                    }
                }

            }


        })

    }

    @Composable
    fun MuscleEquipmentFragmentScreen(viewModel: MuscleEquipmentViewModel) {
        val muscleEquipListState = viewModel.muscleEquipments.observeAsState(initial = listOf())
        val photoPathsAsState = viewModel.photoPathsMap

        val memoryCache = viewModel.memoryCache
        Log.d(TAG, "MuscleEquipmentFragmentScreen: cache size: ${memoryCache.size()}")
        Log.d(TAG, "MuscleEquipmentFragmentScreen: max cache size: ${memoryCache.maxSize()}")


        MuscleEquipmentScreen(
            items = muscleEquipListState.value,
            photoPaths = photoPathsAsState,
            memoryCachedImages = memoryCache,
            onAddItem = {
                val newMuscleEquip = muscleEquipmentFactory.createSingleMuscleEquipment(
                    muscleId = args.muscleId,
                    name = it
                )
                val stateEvent =
                    MuscleEquipmentStateEvent.InsertNewMuscleEquipmentEvent(newMuscleEquip)
                viewModel.setStateEvent(stateEvent = stateEvent)
            },
            currentlyEditing = viewModel.currentEditItem,
            onRemoveItem = {
                val stateEvent = MuscleEquipmentStateEvent.DeleteAMuscleEquipmentEvent(it)
                viewModel.setStateEvent(stateEvent = stateEvent)
            },
            onStartEdit = {
                viewModel.onEditItemSelected(it)
            },
            onItemClickedNavigate = {
                // check to prevent crash when user clicks multiple times fast
                viewModel.setPhotoDetails(null)
                if (findNavController().currentDestination?.id == R.id.muscleEquipmentFragment) {
                    val action = MuscleEquipmentFragmentDirections
                        .actionMuscleEquipmentFragmentToWeightHistoryFragment(
                            muscleEquipId = it.id,
                            muscleEquipName = it.name,
                            muscleName = args.muscleName,
                            themeState = args.themeState
                        )
                    findNavController().navigate(action)
                }

            },
            onEditItemChanged = {
                viewModel.onEditItemChange(it)
            },
            onEditDone = {
                viewModel.onEditDone()
            },
            dateUtil = dateUtil,
            isImageLoading = viewModel.isImageLoading,
            onImageIconClick = { muscleEquip ->
                viewModel.setNewPhotoForMuscleEquip(muscleEquip)
            },
            onImageClicked = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(
                    if (VERSION.SDK_INT >= VERSION_CODES.N)
                        FileProvider.getUriForFile(requireContext(), AUTHORITIES, it.file)
                    else
                        Uri.fromFile(it.file), "image/*"
                ).addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                startActivity(intent)
            }
        )
    }

    @Composable
    private fun AppBar(title: String, onIconClicked: () -> Unit) {
        TopAppBar(
            navigationIcon = {
                Icon(
                    Icons.Rounded.ArrowBack,
                    Modifier
                        .clickable(onClick = onIconClicked)
                        .padding(horizontal = 12.dp)

                )
            },
            title = {
                Text(text = title)
            },
            backgroundColor = MaterialTheme.colors.primarySurface
        )
    }


    companion object {
        private const val TAG = "MuscleEquipmentFragment"
        const val CAMERA_REQUEST_CODE = 101
        const val CURRENT_PHOTO_PATH = "CURRENT_PHOTO_PATH"
        const val CURRENT_MUSCLE_EQUIP_ID = "CURRENT_MUSCLE_EQUIP_ID"
    }

}