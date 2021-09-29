package common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate

@Composable
fun Title(text:String) {
    Text(text,
        modifier = Modifier.fillMaxWidth().padding(padding_m),
        fontSize = 20.sp
    )
}

@Composable
fun Header(text: String, modifier: Modifier=Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(text,
        modifier = modifier.fillMaxWidth().padding(padding_m),
        fontSize = 16.sp,
        textAlign = textAlign,
        fontWeight = FontWeight.Bold
    )
}

fun formatDate(date: LocalDate) :String {
    return "${date.dayOfMonth}/${date.monthNumber}"
}