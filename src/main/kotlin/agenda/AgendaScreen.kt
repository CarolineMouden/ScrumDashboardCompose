package fr.gmo.scrumboard.agenda

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import common.CalendarLayout
import common.CalendarVM
import kotlinx.datetime.toLocalDate
import java.time.format.TextStyle

@Composable
fun AgendaScreen() {
    Column(modifier = Modifier.fillMaxWidth()) {
        CalendarLayout(CalendarVM("2021-02-05".toLocalDate(), TextStyle.FULL), modifier = Modifier.fillMaxWidth()) { date ->
            val txtDay = date.dayOfMonth.toString()
            Text(txtDay, modifier = Modifier.weight(1F, true), textAlign = TextAlign.Center)
        }
    }
}

