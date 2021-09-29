import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.vectorXmlResource
import common.iconSize


enum class Screen {AGENDA, TEAM, SPRINT, RETRO, ISSUE, BUG, OPTIONS}

@Composable
fun Menu(selectedScreen:Screen, onSelected : (Screen) -> Unit) {
    Row (modifier = Modifier.fillMaxWidth()) {
        MenuItem("calendar_month", "Calendrier", selectedScreen==Screen.AGENDA) {onSelected(Screen.AGENDA)}
        MenuItem("account_group", "Equipe", selectedScreen==Screen.TEAM) {onSelected(Screen.TEAM)}
        MenuItem("chart_sankey", "Sprints", selectedScreen==Screen.SPRINT) {onSelected(Screen.SPRINT)}
        MenuItem("clipboard_arrow_up", "Actions", selectedScreen==Screen.RETRO) {onSelected(Screen.RETRO)}
        MenuItem("alert_decagram", "Problèmes", selectedScreen==Screen.ISSUE) {onSelected(Screen.ISSUE)}
        MenuItem("bug", "Anomalies", selectedScreen==Screen.BUG) {onSelected(Screen.BUG)}
        MenuItem("cog", "Paramètres", selectedScreen==Screen.OPTIONS) {onSelected(Screen.OPTIONS)}
    }
}

@Composable
fun MenuItem(iconSrc:String, title:String, isSelected:Boolean, onClick: () -> Unit) {
    Button(colors = ButtonDefaults.buttonColors(backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary)
        , onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = vectorXmlResource("assets/${iconSrc}.xml"),
                contentDescription = title,
                modifier = Modifier.size(iconSize),
                colorFilter = ColorFilter.tint(if(isSelected) Color.White else Color.Black)
            )
            Text(title)
        }
    }
}