package retro

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
import fr.gmo.scrumboard.model.ActionRetro

@Composable
fun RetroListComponent(props: RetroListProps, onNewClick: () -> Unit, onClick: (ActionRetro) -> Unit) {
    Column(modifier = Modifier.width(200.dp).padding(padding_m).verticalScroll(rememberScrollState())) {
        ActionButton(modifier = Modifier.fillMaxWidth(), onClick = onNewClick) {
            Text("Ajouter", textAlign = TextAlign.Center)
        }

        Column {
            props.actions?.forEach { item ->
                RetroItemComponent(item, modifier = Modifier.padding(top = padding_m).fillMaxWidth()) { onClick(it) }
            }
            Spacer(modifier = Modifier.padding(top = padding_m))
            props.passiveActions?.forEach { item ->
                RetroItemComponent(item, modifier = Modifier.padding(top = padding_m).fillMaxWidth()) { onClick(it) }
            }
            Spacer(modifier = Modifier.padding(top = padding_m))
            props.closedActions?.forEach { item ->
                RetroItemComponent(item, modifier = Modifier.padding(top = padding_m).fillMaxWidth()) { onClick(it) }
            }
        }
    }
}

@Composable
fun RetroItemComponent(action: ActionRetro, modifier: Modifier = Modifier, onClick: (ActionRetro) -> Unit) {
    WelcomeButton(modifier = modifier, onClick = { onClick(action) }) {
        Text(action.issue, textAlign = TextAlign.Center)
    }
}