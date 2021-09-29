package issue

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import common.*
import fr.gmo.scrumboard.model.IssueStep
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayAt

@Composable
fun IssueHistoryListComponent(history: List<IssueStep>, onAdd : (IssueStep) -> Unit) {

    Column {
        Text("Historique:", modifier = Modifier.padding(bottom = padding_m))
        Surface(color = Color.LightGray) {
            Column {
                history.forEach {
                    IssueHistoryItemListComponent(it)
                    Divider(color = Color.DarkGray, thickness = 1.dp)
                }

                IssueHistoryNewItemComponent { onAdd(it) }
            }
        }
    }

}

@Composable
fun IssueHistoryItemListComponent(step: IssueStep){
    Row(modifier = Modifier.fillMaxWidth().padding(padding_m)) {
        Column {
            Text(step.date)
            Text(step.actor)
        }
        Text(step.action, modifier = Modifier.fillMaxWidth().padding(start = padding_m))
    }
}

@Composable
fun IssueHistoryNewItemComponent(onAdd: (IssueStep) -> Unit) {
    val now = Clock.System.todayAt(TimeZone.currentSystemDefault())
    var state by mutableStateOf(IssueStep(now.toString()))
    Surface(color = colorSecondary) {
        Column(modifier = Modifier.fillMaxWidth().padding(padding_m)) {
            Row {
                DateInputComponent("", state.date, Modifier.align(Alignment.CenterVertically)) {
                    state = state.copy(date = it)
                }
                TextInputComponent(
                    "Action *",
                    state.action,
                    modifier = Modifier.weight(1F, true).padding(start = padding_m)
                ) { state = state.copy(action = it) }
            }
            Row(modifier = Modifier.padding(top = padding_m)) {
                TextInputComponent(
                    "Porteur *", state.actor,
                    modifier = Modifier.weight(1F, true).padding(end = padding_m)
                ) { state = state.copy(actor = it) }
                IconButton(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = padding_m), onClick = {
                    if (isValid(state)) {
                        onAdd(state)
                        state = state.copy(date = now.toString(), actor = "", action = "")
                    }
                }) {
                    Icon(Icons.Default.Done, contentDescription = "Ajouter", tint = colorAction)
                }
                IconButton(modifier = Modifier.align(Alignment.CenterVertically), onClick = { state = state.copy(date = now.toString(), actor = "", action = "") }) {
                    Icon(Icons.Default.Close, contentDescription = "Annuler", tint = colorAction)
                }
            }
        }
    }
}

fun isValid(step:IssueStep) : Boolean {
    return step.action.isNotEmpty() && step.actor.isNotEmpty()
}