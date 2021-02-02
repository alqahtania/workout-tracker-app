package com.example.workouttracker.framework.presentation.musclelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.workouttracker.R
import com.example.workouttracker.domain.model.muscle.MuscleFactory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.datasource.preferences.PreferencesValues
import com.example.workouttracker.framework.datasource.preferences.PreferencesValues.DARK_MODE
import com.example.workouttracker.framework.datasource.preferences.PreferencesValues.LIGHT_MODE
import com.example.workouttracker.framework.presentation.musclelist.state.MuscleListStateEvent
import com.example.workouttracker.framework.presentation.ui.WorkoutTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MuscleFragment : Fragment() {

    @Inject
    lateinit var muscleFactory: MuscleFactory

    @Inject
    lateinit var dateUtil: DateUtil

    private val viewModel: MuscleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val themeState = viewModel.themeState.observeAsState()
                val isDarkThemeMode =
                    if (themeState.value == PreferencesValues.SYSTEM_MODE || themeState.value == null) isSystemInDarkTheme()
                    else themeState.value == DARK_MODE

                WorkoutTrackerTheme(isDarkThemeMode) {

                    Scaffold(
                        topBar = {
                            AppBar(onClickChangeTheme = {
                                val currentMode = if(isDarkThemeMode) LIGHT_MODE else DARK_MODE
                                viewModel.saveThemeFilterOptions(currentMode)
                            })
                        }
                    ) { innerPadding ->
                        Column {
//                    val muscle = muscleFactory.createSingleMuscle(name = "Bicep")
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text(
//                            text = "Muscle is ${muscle.name} created at ${
//                                dateUtil.removeTimeFromDateString(
//                                    muscle.createdAt
//                                )
//                            } id: ${muscle.id} "
//                        )
//                        Spacer(modifier = Modifier.padding(16.dp))
//                        Button(onClick = {
//                            findNavController()
//                                .navigate(R.id.action_muscleFragment_to_muscleEquipmentFragment)
//                        }) {
//                            Text(text = "Go To Muscle Equipment")
//
//                        }
//                    }
                            MuscleFragmentScreen(viewModel = viewModel, themeState = isDarkThemeMode)
                        }
                    }

                }

            }
        }
    }


    @Composable
    fun MuscleFragmentScreen(viewModel: MuscleViewModel, themeState: Boolean) {
        val muscleListState = viewModel.muscleList.observeAsState(initial = listOf())



        MuscleScreen(
            items = muscleListState.value,
            currentlyEditing = viewModel.currentEditItem,
            onAddItem = {
                val stateEvent = MuscleListStateEvent.InsertNewMuscleEvent(it)
                viewModel.setStateEvent(stateEvent = stateEvent)
            },
            onRemoveItem = {
                val stateEvent = MuscleListStateEvent.DeleteMuscleEvent(it)
                viewModel.setStateEvent(stateEvent = stateEvent)
            },
            onStartEdit = {
                viewModel.onEditItemSelected(it)
            },
            onItemClickedNavigate = {
                // check to prevent crash when user clicks multiple times fast
                if (findNavController().currentDestination?.id == R.id.muscleFragment) {
                    val action = MuscleFragmentDirections
                        .actionMuscleFragmentToMuscleEquipmentFragment(
                            muscleId = it.id,
                            muscleName = it.name,
                            themeState = themeState
                        )
                    findNavController()
                        .navigate(action)
                }

            },
            onEditItemChanged = {
                viewModel.onEditItemChange(it)
            },
            onEditDone = {
                viewModel.onEditDone()
            },
            dateUtil = dateUtil,
            muscleFactory = muscleFactory
        )
    }

    @Composable
    private fun AppBar(onClickChangeTheme: () -> Unit) {
        TopAppBar(
            navigationIcon = {
                Icon(
                    bitmap = imageResource(id = R.drawable.ic_launcher_foreground),
                    Modifier
                        .clickable(onClick = { onClickChangeTheme() })
                        .padding(horizontal = 12.dp)
                        .size(30.dp)
                )
            },
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(text = "Workout Tracker")
                }

            }
        )
    }
}