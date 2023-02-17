package com.example.workouttracker.framework.presentation.weighthistorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.workouttracker.R
import com.example.workouttracker.domain.model.weight_history.WeightHistoryFactory
import com.example.workouttracker.domain.util.DateUtil
import com.example.workouttracker.framework.presentation.ui.WorkoutTrackerTheme
import com.example.workouttracker.framework.presentation.weighthistorylist.state.WeightHistoryStateEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WeightHistoryFragment : Fragment() {

    @Inject
    lateinit var weightHistoryFactory: WeightHistoryFactory

    @Inject
    lateinit var dateUtil: DateUtil

    private val viewModel: WeightHistoryViewModel by viewModels()

    val args: WeightHistoryFragmentArgs by navArgs()

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
                WorkoutTrackerTheme(args.themeState) {
                    Scaffold(topBar = {
                        AppBar(title = "${args.muscleEquipName} History",
                            muscleName = "${args.muscleName} Muscle",
                            onIconClicked = {
                                findNavController().popBackStack()
                            })
                    }) { innerPadding ->
                        Column {
//                            Text(text = "Welcome to the weight history screen")
//                            Row(
//                                horizontalArrangement = Arrangement.Start,
//                                modifier = Modifier
////                                    .background(color = Color.Cyan)
//                            ) {
//
//                                WeightInputField(viewModel)
//
//                            }
                            WeightHistoryFragmentScreen(viewModel = viewModel)

                        }

                    }

                }

            }
        }
    }

    @Composable
    fun WeightHistoryFragmentScreen(viewModel: WeightHistoryViewModel) {
        viewModel.setMuscleEquipId(args.muscleEquipId)

        val weightHistoryList = viewModel.weightHistory.observeAsState(initial = listOf())
        val maxWeightHistory = viewModel.maxWeightHistory.observeAsState()
        WeightHistoryScreen(
            items = weightHistoryList.value,
            onItemComplete = { weight, unit, reps, sides ->
                val newHistory = weightHistoryFactory.createSingleWeightHistory(
                    muscleEquipmentId = args.muscleEquipId,
                    weight = weight,
                    unit = unit,
                    reps = reps,
                    sides = sides
                )
                val stateEvent = WeightHistoryStateEvent.InsertWeightHistoryEvent(newHistory)

                viewModel.setStateEvent(stateEvent)
            },
            maxValueWeight = maxWeightHistory.value,
            currentlyEditing = viewModel.currentEditItem,
            onEditDone = {
                viewModel.onEditDone()
            },
            onStartEdit = {
                viewModel.onEditItemSelected(it)
            },
            onRemoveItem = {
                val stateEvent = WeightHistoryStateEvent.DeleteWeightHistoryEvent(it)
                viewModel.setStateEvent(stateEvent = stateEvent)
            },
            dateUtil = dateUtil
        )
    }

    @Composable
    private fun AppBar(title: String, muscleName: String, onIconClicked: () -> Unit) {
        TopAppBar(
            navigationIcon = {
                Icon(
                    Icons.Rounded.ArrowBack,
                    "",
                    Modifier
                        .clickable(onClick = onIconClicked)
                        .padding(horizontal = 12.dp)

                )
            },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = title)
                    Text(
                        text = muscleName,
                        style = MaterialTheme.typography.subtitle2
                    )
                }

            },
            backgroundColor = MaterialTheme.colors.primarySurface
        )
    }


}