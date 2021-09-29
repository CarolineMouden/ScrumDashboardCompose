package sprint

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import common.ActionButton
import common.WelcomeButton
import common.padding_m
import fr.gmo.scrumboard.model.Sprint

@Composable
fun SprintListComponent(props: SprintListProps, onNewClick: () -> Unit, onClick: (Sprint) -> Unit) {
    Column(modifier = Modifier.width(200.dp).padding(padding_m).verticalScroll(rememberScrollState())) {
        ActionButton(modifier = Modifier.fillMaxWidth(), onClick = onNewClick) {
            Text("Ajouter", textAlign = TextAlign.Center)
        }

        Column {
            props.sprints?.forEach { item ->
                SprintItemComponent(item, modifier = Modifier.padding(top = padding_m).fillMaxWidth()) { onClick(it) }
            }
            Spacer(modifier = Modifier.padding(top = padding_m))
            props.closedSprints?.forEach { item ->
                SprintItemComponent(item, modifier = Modifier.padding(top = padding_m).fillMaxWidth()) { onClick(it) }
            }
        }
    }
}

@Composable
fun SprintItemComponent(sprint: Sprint, modifier: Modifier = Modifier, onClick: (Sprint) -> Unit) {
    WelcomeButton(modifier = modifier, onClick = { onClick(sprint) }) {
        Column {
            Text(sprint.name)
            if (sprint.isDone) {
                Text("Fermé")
            }
            sprint.details?.let {details->
                Text("Points : ${details.donePoints} / ${details.totalComplexity}")
                Text("Capacité : ${details.realCapacity} h/j")

            }
        }
    }
}