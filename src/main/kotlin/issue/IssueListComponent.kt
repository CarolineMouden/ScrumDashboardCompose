package issue

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
import fr.gmo.scrumboard.model.Issue

@Composable
fun IssueListComponent(props: IssueListProps, onNewClick: () -> Unit, onClick: (Issue) -> Unit) {
    Column(modifier = Modifier.width(200.dp).padding(padding_m).verticalScroll(rememberScrollState())) {
        ActionButton(modifier = Modifier.fillMaxWidth(), onClick = onNewClick) {
            Text("Ajouter", textAlign = TextAlign.Center)
        }

        Column {
            props.issues?.forEach { item ->
                IssueItemComponent(item, modifier = Modifier.padding(top = padding_m).fillMaxWidth()) { onClick(it) }
            }
            Spacer(modifier = Modifier.padding(top = padding_m))
            props.closedIssues?.forEach { item ->
                IssueItemComponent(item, modifier = Modifier.padding(top = padding_m).fillMaxWidth()) { onClick(it) }
            }
        }
    }
}

@Composable
fun IssueItemComponent(issue: Issue, modifier: Modifier = Modifier, onClick: (Issue) -> Unit) {
    WelcomeButton(modifier = modifier, onClick = { onClick(issue) }) {
        Text(issue.issue, textAlign = TextAlign.Center)
    }
}