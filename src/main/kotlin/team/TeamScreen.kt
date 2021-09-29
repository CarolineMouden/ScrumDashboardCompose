package team

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import common.*
import fr.gmo.scrumboard.model.Team
import services.TeamAction


@Composable
fun TeamScreen(team:List<Team>, action: TeamAction) {
    System.out.println("UPDATE TEAM SCREEN")
    Column {
        ActionButton(modifier = Modifier.padding(padding_m),
            onClick = { action.update(Team()) }) {
            Text("Ajouter")
        }
        SimpleFlowRow(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            team.forEach {
                TeamItemComponent(it, action)
            }
        }
    }
}

@Composable
fun TeamItemComponent (member: Team, action: TeamAction) {
    var state by mutableStateOf(member)

    Card(modifier = Modifier.width(700.dp).padding(padding_m)) {
        Column(modifier = Modifier.padding(padding_m)) {
            Row {
                TextInputComponent("Nom", state.name, modifier = Modifier.weight(1F, true)) { state = state.copy(name= it) }
                TextInputComponent("Rôles", state.roles, modifier = Modifier.weight(1F, true)) {  state = state.copy(roles=it) }
            }
            Row {
                TextInputComponent("Email", state.email, modifier = Modifier.weight(2F, true)) { state = state.copy(email=it) }
                TextInputComponent("Tel (fixe)", state.deskPhone, modifier = Modifier.weight(1F, true)) { state = state.copy(deskPhone = it) }
                TextInputComponent("Tel (portable)", state.cellPhone, modifier = Modifier.weight(1F, true)) { state = state.copy(cellPhone = it) }
            }
            TextInputComponent("Autre", state.other, modifier = Modifier.fillMaxWidth()) {  state = state.copy(other=it) }
            Row(modifier = Modifier.padding(top = padding_m)) {
                CheckboxInputComponent("Compte actif", state.isActive,
                    modifier = Modifier.weight(1F, true).align(Alignment.CenterVertically))
                {actif,str ->  state = state.copy(isActive=actif) }

                ActionButton(modifier = Modifier.padding(end = padding_m),
                    onClick = { if (isValid(state)) action.update(state) }) {
                    Text("Mettre à jour")
                }
                IconButton(
                    onClick = { action.delete(state) }){
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = colorAction)
                }
            }
        }
    }
}

fun isValid(state : Team) : Boolean {
    return state.name.isNotEmpty()
}