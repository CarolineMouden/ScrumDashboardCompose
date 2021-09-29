package options

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import common.*
import fr.gmo.scrumboard.model.Options
import services.OptionsAction

@Composable
fun OptionsScreen(options:Options, action: OptionsAction) {
    var state by mutableStateOf(options)
    Column(modifier = Modifier.padding(padding_m)) {
        Title("Paramètres")

        ActionButton({action.disconnect()}) {
            Text("Fermer le projet")
        }

        Header("Mes status")

        Column {
            state.sprintItemStatusList.forEach { itemStatus ->
                Row(modifier = Modifier.padding(bottom = padding_m)) {
                    Surface(modifier = Modifier.height(25.dp).width(20.dp).padding(end = padding_m), color = Color(itemStatus.colorComp)){}
                    Text(itemStatus.name, modifier = Modifier.width(100.dp))

                    CheckboxInputComponent("Ne pas prendre en compte", itemStatus.isDefault) {b, s ->  }
                    CheckboxInputComponent("En cours", itemStatus.isStarted) {b, s ->  }
                    CheckboxInputComponent("Compter les tâches", itemStatus.withTask) {b, s ->  }
                    CheckboxInputComponent("Terminé", itemStatus.isDone) {b, s ->  }
                }

            }
        }
    }
}

