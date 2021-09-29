package retro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.*
import fr.gmo.scrumboard.model.ActionRetro
import fr.gmo.scrumboard.model.ActionRetroType
import services.RetroAction

@Composable
fun RetroComponent(
    retro: ActionRetro,
    listSprint: List<String>,
    action:RetroAction
) {
    var state by mutableStateOf(retro)
    Surface(modifier = Modifier.fillMaxWidth().padding(padding_m)) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            if (state.id == null) {
                Title("Nouvelle action de retrospective")
                TextInputComponent("Problématique *", state.issue,
                    modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) { state = state.copy(issue = it) }
            } else {
                Title(state.issue)
            }

            TextInputComponent("Action *", state.solution,
                modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) { state = state.copy(solution = it) }
            TextInputComponent("Porteur", state.actor,
                modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) { state = state.copy(actor = it) }

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) {
                SelectInputComponent(
                    "Type",
                    ActionRetroType.values().map { it.toString() }.toCollection(ArrayList()),
                    state.type.toString(),
                    modifier = Modifier.weight(1F, true)
                ) { state = state.copy(type = ActionRetroType.valueOf(it)) }
                SelectInputComponent("Sprint associé", listSprint, state.sprintName,
                    modifier = Modifier.weight(1F, true)
                ) { state = state.copy(sprintName = it) }
            }

            TextInputComponent("Commentaire", state.comment,
                modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) { state = state.copy(comment = it) }

            Row {
                CheckboxInputComponent("Action terminée", state.isDone,
                    modifier = Modifier.weight(1F, true).align(Alignment.CenterVertically))
                { done, str -> state = state.copy(isDone = done) }

                ActionButton(modifier = Modifier.padding(end = padding_m), onClick = { if (isValid(state)) action.update(state) }) {
                    if (state.id == null) {
                        Text("Ajouter")
                    } else {
                        Text("Mettre à jour")
                    }
                }

                ActionButton(onClick = { action.delete(state) }) {
                    if (state.id == null) {
                        Text("Annuler")
                    } else {
                        Text("Supprimer")
                    }
                }
            }
        }
    }
}

fun isValid(action: ActionRetro): Boolean {
    return action.issue.isNotEmpty() && action.solution.isNotEmpty()
}


@Composable
fun RetroWelcomeComponent(props: RetroListProps, onNewClick: () -> Unit, onClick: (ActionRetro) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        ActionButton(modifier = Modifier.padding(padding_m), onClick = onNewClick) {
            Text("Ajouter")
        }

        Title("Actions en cours")
        if (props.actions.isEmpty()) {
            Text("Pas d'actions en cours.", modifier = Modifier.padding(start=30.dp))
        } else {
            WelcomeFlowRow {
                props.actions.forEach {
                    RetroItemComponent(it) { onClick(it) }
                }
            }
        }

        Title("Actions passives")
        if (props.passiveActions.isEmpty()) {
            Text("Pas d'actions passives.", modifier = Modifier.padding(start=30.dp))
        } else {
            WelcomeFlowRow {
                props.passiveActions.forEach {
                    RetroItemComponent(it) { onClick(it) }
                }
            }
        }

        Title("Actions terminées")
        if (props.closedActions.isEmpty()) {
            Text("Pas d'actions terminées.", modifier = Modifier.padding(start=30.dp))
        } else {
            WelcomeFlowRow {
                props.closedActions.forEach {
                    RetroItemComponent(it) { onClick(it) }
                }
            }
        }
    }
}