package sprint.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.*
import fr.gmo.scrumboard.model.Options
import fr.gmo.scrumboard.model.SprintItem

@Composable
fun StoryListComponent (items: List<SprintItem>, options:Options, onUpdate: (SprintItem, String) -> Unit, onDelete : (SprintItem) -> Unit) {
    Column {
        ActionButton(modifier = Modifier.padding(padding_m),
            onClick = {
                var id = calculateId(items)
                onUpdate(SprintItem(id, "", options.sprintTypeList.get(0)), id)
            }) {
            Text("Ajouter")
        }
        Column {
            items.forEach {
                StoryItemListComponent(it, options, onUpdate, onDelete)
            }
        }
    }
}

fun calculateId(items: List<SprintItem>) : String {
    if (items.isEmpty()) return "0001"
    var lastItem = items.map { it.id }.sorted().last()
    return lastItem+"2" //TOdo
}

@Composable
fun StoryItemListComponent (sprintItem: SprintItem, options:Options, onUpdate: (SprintItem, String) -> Unit, onDelete : (SprintItem) -> Unit) {
    var state by mutableStateOf(sprintItem)
    var statePoint by mutableStateOf(sprintItem.point.toString())
    var stateCharge by mutableStateOf(sprintItem.charge.toString())
    var stateTask by mutableStateOf(sprintItem.tasks.toString())

    Surface {
        Column(modifier = Modifier.fillMaxWidth().padding(padding_m)) {
            Row {
                SelectInputComponent(
                    "",
                    options.sprintTypeList,
                    state.type.toString(),
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = padding_m)
                ) { state = state.copy(type = it) }
                TextInputComponent("ID *", state.id, modifier = Modifier.weight(1F, true)) { state = state.copy(id = it) }
                TextInputComponent("Point", statePoint, modifier = Modifier.width(120.dp)) {
                    statePoint = it
                    it.toDoubleOrNull()?.let { point ->
                        state = state.copy(point = point)
                    }
                }
                TextInputComponent("Charge (jour)", stateCharge, modifier = Modifier.width(120.dp)) {
                    stateCharge = it
                    it.toDoubleOrNull()?.let { point ->
                        state = state.copy(charge = point)
                    }
                }
                TextInputComponent("Nb sous-tâches", stateTask, modifier = Modifier.width(120.dp)) {
                    stateTask = it
                    it.toIntOrNull()?.let { point ->
                        state = state.copy(tasks = point)
                    }
                }
            }
            Row {
                TextInputComponent("Titre *", state.title,
                    modifier = Modifier.weight(1F, true).padding(end = padding_m)) {
                    state = state.copy(title = it)
                }

                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = padding_m),
                    onClick = { if (isValid(state)) onUpdate(state, sprintItem.id) }) {
                    Icon(Icons.Default.Done, contentDescription = "Mettre à jour", tint = colorAction)
                }
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = { onDelete(state) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = colorAction)
                }
            }
        }
    }
}

fun isValid(state : SprintItem) : Boolean {
    return state.id.isNotEmpty() && state.title.isNotEmpty()
}