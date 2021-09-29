package issue

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import common.Title
import fr.gmo.scrumboard.model.Issue
import services.IssueAction
import kotlin.collections.ArrayList

data class IssueListProps(
    var issues: List<Issue>,
    var closedIssues: List<Issue>
)

@Composable
fun IssueScreen(issues: List<Issue>, screen: String, action: IssueAction) {
    var listProps = filterIssues(issues)
    Column {
        if (screen.equals("WELCOME")) {
            IssueWelcomeComponent(listProps, { action.changeScreen("NEW") }, { act -> action.changeScreen(act.id!!) })
        } else {
            Row {
                IssueListComponent(listProps, { action.changeScreen("NEW") }, { act -> action.changeScreen(act.id!!) })
                IssueComponent(if (screen.equals("NEW")) Issue() else action.get(screen), action)
            }
        }
    }
}

fun filterIssues(allActions: List<Issue>): IssueListProps {
    val current = allActions.filter { !it.isClosed }
    val closed = allActions.filter { it.isClosed }
    return IssueListProps(current, closed)
}
