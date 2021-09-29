package issue

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
import fr.gmo.scrumboard.model.*
import services.IssueAction

@Composable
fun IssueComponent(
    issue: Issue,
    action: IssueAction
) {
    var state by mutableStateOf(issue)
    var refresh by mutableStateOf(false)

    Surface(modifier = Modifier.fillMaxWidth().padding(padding_m)) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            if (state.id == null) {
                Title("Nouveau problème")
                TextInputComponent("Problématique *", state.issue,
                    modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) { state = state.copy(issue = it) }
            } else {
                Title(state.issue)
            }

            SelectInputComponent(
                "Priorité",
                IssuePriority.values().map { it.toString() }.toCollection(ArrayList()),
                state.priority.toString(),
                modifier = Modifier.padding(bottom = padding_m)
            ) { state = state.copy(priority = IssuePriority.valueOf(it)) }

            TextInputComponent("Commentaire", state.comment, modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) { state = state.copy(comment = it) }

            refresh.let {
                IssueHistoryListComponent(state.history) {
                    state = state.copy(history = addStep(state, it))
                }
                refresh = !refresh
            }

            Row( modifier = Modifier.padding(top=padding_m)) {
                CheckboxInputComponent("Problème résolu", state.isClosed,
                    modifier = Modifier.weight(1F, true).align(Alignment.CenterVertically))
                {closed,str -> state = state.copy(isClosed = closed) }

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

fun addStep(issue: Issue, it: IssueStep): ArrayList<IssueStep> {
    issue.history.add(it) // TODO ordonnancer par date
    return issue.history
}

fun isValid(action: Issue): Boolean {
    return action.issue.isNotEmpty()
}


@Composable
fun IssueWelcomeComponent(props: IssueListProps, onNewClick: () -> Unit, onClick: (Issue) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        ActionButton(modifier = Modifier.padding(padding_m), onClick = onNewClick) {
            Text("Ajouter")
        }

        Title("Actions en cours")
        if (props.issues.isEmpty()) {
            Text("Pas d'actions en cours.", modifier = Modifier.padding(start=30.dp))
        } else {
            WelcomeFlowRow {
                props.issues.forEach {
                    IssueItemComponent(it) { onClick(it) }
                }
            }
        }

        Title("Actions terminées")
        if (props.closedIssues.isEmpty()) {
            Text("Pas d'actions terminées.", modifier = Modifier.padding(start=30.dp))
        } else {
            WelcomeFlowRow {
                props.closedIssues.forEach {
                    IssueItemComponent(it) { onClick(it) }
                }
            }
        }
    }
}