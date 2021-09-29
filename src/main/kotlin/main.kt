import androidx.compose.desktop.Window
import androidx.compose.desktop.WindowEvents
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import bug.BugScreen
import common.*
import fr.gmo.scrumboard.agenda.AgendaScreen
import fr.gmo.scrumboard.model.*
import fr.gmo.scrumboard.services.SprintAction
import fr.gmo.scrumboard.services.SprintService
import issue.IssueScreen
import options.OptionsScreen
import org.kodein.db.DB
import org.kodein.db.impl.open
import retro.RetroScreen
import services.*
import sprint.SprintScreen
import team.TeamScreen

val viewModel: ViewModel = ViewModel()
fun main() = Window(title = "Dashboard ScrumMaster", size = IntSize(1000,600), events = WindowEvents(onClose =  {viewModel.close()})) {
    var screen by remember { mutableStateOf(Screen.SPRINT) }
    var projectSelected by mutableStateOf(false)
    var options by mutableStateOf(Options())
    var listTeam by mutableStateOf(mutableListOf<Team>())
    var listRetro by mutableStateOf(mutableListOf<ActionRetro>())
    var listIssue by mutableStateOf(mutableListOf<Issue>())
    var listSprint by mutableStateOf(mutableListOf<Sprint>())
    var listSprintNames by mutableStateOf(mutableListOf<String>())
    var screenId by mutableStateOf("WELCOME")
    var project by mutableStateOf("Scrumboard")

    ScrumTheme {
        if (!projectSelected) {
            Column (modifier = Modifier.padding(padding_m)) {
                Title("Bienvenue!")
                TextInputComponent("Nom du projet", project) {project = it}
                ActionButton(modifier = Modifier.padding(vertical = padding_m),
                    onClick = {
                    viewModel.setProject(project,
                        onUpdateTeam = { listTeam = it.toMutableList() },
                        onUpdateRetro = { listRetro = it.toMutableList() },
                        onUpdateIssue = { listIssue = it.toMutableList() },
                        onUpdateSprint = {
                            listSprint = it.toMutableList()
                            listSprintNames = it.map { it.name!! }.toMutableList()
                        },
                        onUpdateOptions = {options = it}
                    )
                    projectSelected = true
                }){ Text("Ouvrir")}
            }
        } else {
            Column {
                Menu(screen) { s ->
                    screen = s
                    screenId = "WELCOME"
                }
                when (screen) {
                    Screen.AGENDA -> AgendaScreen()
                    Screen.BUG -> BugScreen()
                    Screen.TEAM ->
                        TeamScreen(listTeam, object : TeamAction {
                            override fun update(team: Team) {
                                viewModel.teamService.updateItem(team) {}
                            }

                            override fun delete(team: Team) {
                                viewModel.teamService.deleteItem(team) {}
                            }
                        })

                    Screen.SPRINT ->
                        SprintScreen(listSprint, listSprintNames, listTeam, options, screenId,
                            object : SprintAction {
                                override fun changeScreen(screen: String) {
                                    screenId = screen
                                }

                                override fun get(screen: String): Sprint {
                                    return viewModel.sprintService.getSprint(screen)
                                }

                                override fun getId(name: String): String {
                                    return viewModel.sprintService.getSprintIdFromName(name)
                                }

                                override fun update(sprint: Sprint) {
                                    viewModel.sprintService.updateItem(sprint) { screenId = "WELCOME" }
                                }

                                override fun delete(sprint: Sprint) {
                                    viewModel.sprintService.deleteItem(sprint) { screenId = "WELCOME" }
                                }

                                override fun lastxSprint(name: String, total: Int): ArrayList<Sprint> {
                                    return viewModel.sprintService.lastxSprint(name, total)
                                }
                            }
                        )
                    Screen.RETRO ->
                        RetroScreen(listRetro, listSprintNames, screenId,
                            object : RetroAction {
                                override fun changeScreen(screen: String) {
                                    screenId = screen
                                }

                                override fun get(screen: String): ActionRetro {
                                    return viewModel.retroService.getAction(screen)
                                }

                                override fun update(retro: ActionRetro) {
                                    viewModel.retroService.updateItem(retro) { screenId = "WELCOME" }
                                }

                                override fun delete(retro: ActionRetro) {
                                    viewModel.retroService.deleteItem(retro) { screenId = "WELCOME" }
                                }
                            }
                        )
                    Screen.ISSUE ->
                        IssueScreen(listIssue, screenId,
                            object : IssueAction {
                                override fun changeScreen(screen: String) {
                                    screenId = screen
                                }

                                override fun get(screen: String): Issue {
                                    return viewModel.issueService.getIssue(screen)
                                }

                                override fun update(issue: Issue) {
                                    viewModel.issueService.updateItem(issue) { screenId = "WELCOME" }
                                }

                                override fun delete(issue: Issue) {
                                    viewModel.issueService.deleteItem(issue) { screenId = "WELCOME" }
                                }
                            }
                        )
                    Screen.OPTIONS ->
                        OptionsScreen(options, object : OptionsAction {
                            override fun disconnect() {
                                viewModel.close()
                                projectSelected = false
                            }

                            override fun update(options: Options) {
                                viewModel.optionsService.updateItem(options) { System.out.println("Yay") }
                            }
                        })
                }
            }
        }
    }
}

class ViewModel {
    lateinit var teamService:TeamService
    lateinit var retroService:RetroService
    lateinit var issueService:IssueService
    lateinit var sprintService:SprintService
    lateinit var optionsService: OptionsService

    var db:DB? = null

    fun setProject(project:String, onUpdateTeam:(List<Team>)->Unit, onUpdateRetro:(List<ActionRetro>) -> Unit,
             onUpdateIssue:(List<Issue>)->Unit, onUpdateSprint:(List<Sprint>) -> Unit, onUpdateOptions:(Options)-> Unit) {
        close()
        db = DB.open("./$project.db")
        teamService = TeamService(db!!, onUpdateTeam)
        retroService = RetroService(db!!, onUpdateRetro)
        issueService = IssueService(db!!, onUpdateIssue)
        sprintService = SprintService(db!!, onUpdateSprint)
        optionsService = OptionsService(db!!, onUpdateOptions)
    }

    fun close() {
        db?.close()
    }

}