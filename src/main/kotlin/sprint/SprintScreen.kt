package sprint

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import fr.gmo.scrumboard.model.Options
import fr.gmo.scrumboard.model.Sprint
import fr.gmo.scrumboard.model.Team
import fr.gmo.scrumboard.services.SprintAction

data class SprintListProps(
    var sprints: List<Sprint>,
    var closedSprints: List<Sprint>
)

@Composable
fun SprintScreen(sprints: List<Sprint>, listSprint: List<String>, listTeam: List<Team>, options: Options, screen: String, action:SprintAction) {
    var listProps = filterSprints(sprints)
    Column {
        if (screen.equals("WELCOME")) {
           SprintWelcomeComponent(listProps, { action.changeScreen("NEW") }, { act -> action.changeScreen(act.id!!) })
        } else {
            Row {
                SprintListComponent(listProps, { action.changeScreen("NEW") }, { act -> action.changeScreen(act.id!!) })
                SprintComponent(if (screen.equals("NEW")) Sprint() else action.get(screen), listSprint, listTeam, options, action)
            }
        }
    }
}

fun filterSprints(allActions: List<Sprint>): SprintListProps {
    val current = allActions.filter { !it.isDone }
    val closed = allActions.filter { it.isDone }
    return SprintListProps(current, closed)
}