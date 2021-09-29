package retro

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.*
import common.Title
import fr.gmo.scrumboard.model.ActionRetro
import fr.gmo.scrumboard.model.ActionRetroType
import services.RetroAction
import kotlin.collections.ArrayList

data class RetroListProps(
    var actions: List<ActionRetro>,
    var passiveActions: List<ActionRetro>,
    var closedActions: List<ActionRetro>
)

@Composable
fun RetroScreen(retros: List<ActionRetro>, listSprint: List<String>, screen: String, action:RetroAction) {
    var listProps = filterActions(retros)
    Column {
        if (screen.equals("WELCOME")) {
            RetroWelcomeComponent(listProps, { action.changeScreen("NEW") }, { act -> action.changeScreen(act.id!!) })
        } else {
            Row {
                RetroListComponent(listProps, { action.changeScreen("NEW") }, { act -> action.changeScreen(act.id!!) })
                RetroComponent(if (screen.equals("NEW")) ActionRetro() else action.get(screen), listSprint,action)
            }
        }
    }
}

fun filterActions(allActions: List<ActionRetro>): RetroListProps {
    val current = allActions.filter { !it.isDone }
    val actif = current.filter { ActionRetroType.PONCTUEL.equals(it.type) }
    val passif = current.filter { ActionRetroType.PASSIF.equals(it.type) }
    val closed = allActions.filter { it.isDone }
    return RetroListProps(actif, passif, closed)
}



