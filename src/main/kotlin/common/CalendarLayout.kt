package common

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarLayout(calendarVM: CalendarVM, modifier: Modifier = Modifier,  content: @Composable (date:LocalDate) -> Unit) {
    var state by remember { mutableStateOf(calendarVM.date) }
    var isFull = calendarVM.style == TextStyle.FULL
    Column(modifier = modifier) {
        Row(modifier = Modifier.weight(1F, true).fillMaxWidth().padding(padding_m), horizontalArrangement = Arrangement.Center) {
            Row {
                InputButton(onClick = {
                    state = state.minus(DateTimeUnit.MONTH)
                }) { Text("<") }
                Text(
                    "${state.month.getDisplayName(TextStyle.FULL, Locale.FRENCH)} ${state.year}",
                    modifier = Modifier.padding(horizontal = padding_m).align(Alignment.CenterVertically)
                )
                InputButton(onClick = {
                    state = state.plus(DateTimeUnit.MONTH)
                }) { Text(">") }
            }
        }

        Row(modifier = Modifier.weight(1F, true)) {
            Text(if (isFull) "lundi" else "lun", modifier = Modifier.weight(1F, true), textAlign = TextAlign.Center)
            Text(if (isFull) "mardi" else "mar", modifier = Modifier.weight(1F, true), textAlign = TextAlign.Center)
            Text(if (isFull) "mercredi" else "mer", modifier = Modifier.weight(1F, true), textAlign = TextAlign.Center)
            Text(if (isFull) "jeudi" else "jeu", modifier = Modifier.weight(1F, true), textAlign = TextAlign.Center)
            Text(if (isFull) "vendredi" else "ven", modifier = Modifier.weight(1F, true), textAlign = TextAlign.Center)
            Text(if (isFull) "samedi" else "sam", modifier = Modifier.weight(1F, true), textAlign = TextAlign.Center)
            Text(if (isFull) "dimanche" else "dim", modifier = Modifier.weight(1F, true), textAlign = TextAlign.Center)
        }
        var day = LocalDate(state.year, state.monthNumber, 1)
        System.out.println("Day : ${day.dayOfWeek.value}")
        var sameMonth = true
        while (sameMonth) {
            Row(modifier = Modifier.weight(1F, true)) {
                for (i in 0..6) {
                    if (sameMonth) {
                        if ((day.dayOfWeek.value == (i+1)) ||
                            (day.dayOfWeek.value == 0 && i == 6)) {
                            content(day)
                            day = day.plus(DateTimeUnit.DAY)
                        } else {
                            Spacer(modifier = Modifier.weight(1F, true))
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1F, true))
                    }
                    sameMonth = day.monthNumber == state.monthNumber
                }
            }
        }

    }
}

data class CalendarVM(var date: LocalDate, var style: TextStyle)