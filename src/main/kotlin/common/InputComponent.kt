package common

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.toLocalDate
import java.time.format.TextStyle

@Composable
fun TextInputComponent(label:String, initVal:String?, modifier: Modifier = Modifier, onUpdate: (String) -> Unit) {
    TextField(
        label = { Text(label) },
        value = initVal?:"",
        modifier = modifier,
        singleLine = true,
        onValueChange = {
            onUpdate(it)
        }
    )
}

@Composable
fun CheckboxInputComponent(label:String, initVal:Boolean?, modifier: Modifier = Modifier, onUpdate: (Boolean, String) -> Unit) {
    Row( modifier = modifier) {
        Checkbox(
            checked = initVal?: false,
            onCheckedChange = {onUpdate(it, label)}
        )
        Text(label, modifier = Modifier.padding(start = padding_m))
    }
}

@Composable
fun SelectInputComponent(label:String, choices: List<String>, initVal:String?, modifier: Modifier = Modifier, onUpdate: (String) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    Row(modifier=modifier) {
        if (label.isNotEmpty())
            Text(label, modifier = Modifier.padding(padding_m))
        Box {
            InputButton(modifier = modifier,
                onClick = { expanded.value = true }) {
                Text(initVal ?: "")
            }
            DropdownMenu(expanded = expanded.value,
                onDismissRequest = { expanded.value = false }) {
                choices.forEach { choice ->
                    DropdownMenuItem(onClick = {
                        onUpdate(choice)
                        expanded.value = false
                    }) {
                        Text(choice)
                    }
                }
            }
        }
    }
}

@Composable
fun DateInputComponent(label:String, initVal: String, modifier: Modifier = Modifier, onUpdate: (String) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    Row(modifier=modifier) {
        if (label.isNotEmpty())
            Text(label, modifier = Modifier.padding(padding_m))
        Box {
            InputButton(modifier = modifier,
                onClick = { expanded.value = true }) {
                Text(initVal ?: "")
            }
            DropdownMenu(expanded = expanded.value,
                onDismissRequest = { expanded.value = false }) {
                CalendarLayout(CalendarVM(initVal.toLocalDate(), TextStyle.SHORT), modifier = Modifier.width(400.dp).height(400.dp)) { date ->
                    val txtDay = date.dayOfMonth.toString()
                    Button(modifier = Modifier.weight(1F, true).padding(horizontal = 3.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorInput
                        ),
                        onClick = {
                            onUpdate(date.toString())
                            expanded.value = false
                        }
                    ) {
                        Text(
                            txtDay,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}