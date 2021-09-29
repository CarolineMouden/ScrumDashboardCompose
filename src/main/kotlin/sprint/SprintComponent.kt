package sprint

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.*
import fr.gmo.scrumboard.model.*
import fr.gmo.scrumboard.services.SprintAction
import kotlinx.datetime.*
import sprint.details.*
import java.time.format.TextStyle

enum class SprintScreen {ORGA, DETAILS, TICKETS, HISTORY, CHARTS}

@Composable
fun SprintComponent(
    sprint: Sprint,
    listSprint: List<String>,
    listTeam: List<Team>,
    options: Options,
    action: SprintAction) {

    var sprintListNames = if (sprint.id==null) listSprint else listSprint.filterNot { it.equals(sprint.name) }
    var state by mutableStateOf(sprint)
    var stateScreen by mutableStateOf(if (state.id==null) SprintScreen.ORGA else SprintScreen.HISTORY)
    var stateName by mutableStateOf(sprint.name)
    var stateDone by mutableStateOf(sprint.isDone)
    var statePrecedent by mutableStateOf(sprint.precedentSprintName)
    var stateDetails by mutableStateOf(SprintDetailViewModel(sprint.details))
    var refreshTeam by mutableStateOf(false)
    var refreshDate by mutableStateOf(false)
    var refreshDetails by mutableStateOf(false)
    var refreshTickets by mutableStateOf(false)
    var refreshHistory by mutableStateOf(false)

    val now = Clock.System.todayAt(TimeZone.currentSystemDefault())

    Surface(modifier = Modifier.fillMaxWidth().padding(padding_m)) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            if (state.id == null) {
                Title("Nouveau sprint")
                TextInputComponent("Nom du sprint *", stateName,
                    modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) {
                    stateName = it
                    state.name = it
                }
            } else {
                Title(state.name)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) {
                SelectInputComponent("Sprint précédent", sprintListNames, statePrecedent,
                    modifier = Modifier.weight(1F).padding(end = padding_m)) {
                    statePrecedent = it
                    state.precedentSprintName = it

                    val ratio = updateRatio(action.lastxSprint(it, TOTAL), state.details.ratio)
                    state.details.ratio = ratio
                    stateDetails.ratio = ratio.toString()
                }

                statePrecedent?.let {
                    if (it.isNotEmpty()) {
                        ActionButton(modifier = Modifier.weight(1F),
                            onClick = { action.changeScreen(action.getId(it)) }) {
                            Text("Afficher le sprint précédent")
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) {
                CheckboxInputComponent("Sprint clôturé", stateDone,
                    modifier = Modifier.weight(1F, true).align(Alignment.CenterVertically)) { done, lab ->
                        stateDone = done
                        state.isDone = done
                }

                ActionButton(modifier = Modifier.padding(end = padding_m), onClick = { if (isValid(state)) {
                    state.details = stateDetails.toDetails()
                    action.update(state)
                } }) {
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

            SprintDetailsSelection(stateScreen) {stateScreen = it }

            when (stateScreen) {
                SprintScreen.ORGA -> {
                    Header("Durée du sprint")
                    refreshDate.let {
                        CalendarLayout(CalendarVM(if (state.days.isEmpty()) now else state.days[0].toLocalDate(), TextStyle.SHORT),
                            modifier = Modifier.width(400.dp).height(400.dp)) { date ->
                            val txtDay = date.dayOfMonth.toString()
                            CheckboxInputComponent(txtDay, state.days.contains(date.toString()), modifier = Modifier.weight(1F, true)
                            ) { checked, value ->
                                if (checked) state.days.add(date.toString()) else state.days.remove(date.toString())
                                state.days.sort()
                                refreshDate = !refreshDate
                            }
                        }
                    }

                    if (listTeam.isNotEmpty()) {
                        Header("Equipe")
                        refreshTeam.let {
                            listTeam.forEach {
                                CheckboxInputComponent(it.name, state.members.contains(it.id)) { checked, value ->
                                    if (checked) state.members.add(it.id!!) else state.days.remove(it.id)
                                    refreshTeam = !refreshTeam
                                }
                            }
                        }
                    }
                }
                SprintScreen.DETAILS -> {
                    refreshDetails.let {
                        SprintDetailsComponent(stateDetails) {
                            stateDetails = it
                            refreshDetails != refreshDetails
                        }
                    }

                }
                SprintScreen.TICKETS -> {
                    refreshTickets.let {
                        StoryListComponent(state.items, options, { it, oldId ->
                            state.updateTicket(it, oldId)
                            state.updateDetails(options)
                            stateDetails = SprintDetailViewModel(state.details)
                            refreshTickets = !refreshTickets
                            refreshHistory = !refreshHistory
                        }, {
                            state.deleteTicket(it)
                            state.updateDetails(options)
                            stateDetails = SprintDetailViewModel(state.details)
                            refreshTickets = !refreshTickets
                            refreshHistory = !refreshHistory
                        })
                    }
                }
                SprintScreen.HISTORY -> {
                    refreshHistory.let {
                        StoryHistoryComponent(state.items, state.days, options) { i, d, h ->
                            state = state.copy(items = updateSprintHistoryItem(state.items, i, d, h))
                            state.updateDetails(options)
                            stateDetails = SprintDetailViewModel(state.details)
                            refreshHistory = !refreshHistory
                        }
                    }
                }

                SprintScreen.CHARTS -> {
                    Column {
                        SprintBurnDownChartComponent(state, options)
                        SprintCumulativeChartComponent(state, options)
                    }
                }
            }
        }
    }
}

val TOTAL = 3

fun isValid(sprint: Sprint): Boolean {
    return sprint.name.isNotEmpty()
}
fun updateRatio(sprints: ArrayList<Sprint>, currentRatio:Double) : Double{
    if (sprints.size < TOTAL) {
        return currentRatio
    }
    val details:List<SprintDetail> = sprints.mapNotNull { it.details }
    val ponderedCap:Double = details.map({it.realCapacity}).sum()
    val ponderedComp:Double = details.map({it.donePoints}).sum()
    return (ponderedComp / ponderedCap)
}



fun updateSprintHistoryItem(items: ArrayList<SprintItem>, item: SprintItem, day:String, history: SprintItemHistory) : ArrayList<SprintItem>{
    val idsItem = items.filter { it.id.equals(item.id) }
    item.history.put(day, history)
    idsItem.forEach { items.set(items.indexOf(it), item) }
    return items
}


@Composable
fun SprintDetailsSelection(selectedScreen: SprintScreen, onSelected: (SprintScreen) -> Unit) {
    Row (modifier = Modifier.fillMaxWidth().padding(bottom = padding_m)) {
        SprintMenuItem("Organisation", selectedScreen==SprintScreen.ORGA) {onSelected(SprintScreen.ORGA)}
        SprintMenuItem("Détails", selectedScreen==SprintScreen.DETAILS) {onSelected(SprintScreen.DETAILS)}
        SprintMenuItem("Tickets", selectedScreen==SprintScreen.TICKETS) {onSelected(SprintScreen.TICKETS)}
        SprintMenuItem("Historique", selectedScreen==SprintScreen.HISTORY) {onSelected(SprintScreen.HISTORY)}
        SprintMenuItem("Charts", selectedScreen==SprintScreen.CHARTS) {onSelected(SprintScreen.CHARTS)}
    }
}

@Composable
fun SprintMenuItem(title:String, isSelected:Boolean, onClick: () -> Unit) {
    Button(colors = ButtonDefaults.buttonColors(backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary),
        onClick = onClick) {
            Text(title)
    }
}

@Composable
fun SprintWelcomeComponent(props: SprintListProps, onNewClick: () -> Unit, onClick: (Sprint) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        ActionButton(modifier = Modifier.padding(padding_m), onClick = onNewClick) {
            Text("Ajouter")
        }

        Title("Sprints en cours")
        if (props.sprints.isEmpty()) {
            Text("Pas de sprint en cours.", modifier = Modifier.padding(start=30.dp))
        } else {
            WelcomeFlowRow {
                props.sprints.forEach {
                    SprintItemComponent(it) { onClick(it) }
                }
            }
        }

        Title("Anciens sprints")
        if (props.closedSprints.isEmpty()) {
            Text("Pas de sprint terminé.", modifier = Modifier.padding(start=30.dp))
        } else {
            WelcomeFlowRow {
                props.closedSprints.forEach {
                    SprintItemComponent(it) { onClick(it) }
                }
            }
        }
    }
}