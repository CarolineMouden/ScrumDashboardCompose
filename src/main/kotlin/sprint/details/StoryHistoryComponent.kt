package sprint.details

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import common.*
import fr.gmo.scrumboard.model.Options
import fr.gmo.scrumboard.model.SprintItem
import fr.gmo.scrumboard.model.SprintItemHistory
import kotlinx.datetime.toLocalDate

@Composable
fun StoryHistoryComponent(items: List<SprintItem>, days:List<String>,  options: Options, onUpdate: (SprintItem, String, SprintItemHistory) -> Unit) {
    val nameWidth = 200.dp
    val itemWidth = 125.dp
    val horizontalScrollState = rememberScrollState(1)
    Box{
        Column(modifier = Modifier.horizontalScroll(horizontalScrollState).padding(bottom = padding_m)) {
            Row {
                Header("Ticket", Modifier.width(nameWidth))
                days.forEach { day ->
                    Header(formatDate(day.toLocalDate()), modifier = Modifier.width(itemWidth), textAlign = TextAlign.Center)
                }
            }
            items.forEach {
                Row {
                    Column(modifier = Modifier.width(nameWidth)) {
                        Text("${it.id} ${it.type}")
                        Text(it.title,  maxLines = 1, overflow = TextOverflow.Ellipsis)

                    }
                    days.forEach { day ->
                        StoryHistoryComponentItem(it, day, options, Modifier.width(itemWidth)) { item, day, h ->
                            onUpdate(item, day, h)
                        }
                    }
                }
            }
        }
        HorizontalScrollbar(adapter = rememberScrollbarAdapter(horizontalScrollState),
            modifier = Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
fun StoryHistoryComponentItem(item:SprintItem, day:String, options: Options, modifier: Modifier, onUpdate: (SprintItem, String, SprintItemHistory) -> Unit) {
    var history = item.history.get(day)
    if (history == null) {
        history = SprintItemHistory(options.sprintItemStatusList.get(0).name, 0)
    }
    var state by mutableStateOf(history)
    var stateTask by mutableStateOf(history.tasks.toString())

    val taskStatusList = options.sprintItemStatusList.filter { stat -> stat.withTask }.map { it.name }
    var stateShowTask by mutableStateOf(taskStatusList.contains(state.status))

    Column(modifier=modifier) {
        SelectInputComponent(
            "",
            options.sprintItemStatusListStr,
            state.status,
            modifier= Modifier.fillMaxWidth()
        ) {
            state.status = it
            stateShowTask = taskStatusList.contains(state.status)
            onUpdate(item, day, state)
        }

        if (stateShowTask) {
            TextInputComponent("Tâches", stateTask, modifier= Modifier.fillMaxWidth()) {
                stateTask = it
                it.toIntOrNull()?.let { task ->
                    state.tasks = task
                    onUpdate(item, day, state)
                }
            }
        }
    }
}