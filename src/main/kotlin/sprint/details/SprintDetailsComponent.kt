package sprint.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import common.TextInputComponent
import common.padding_m
import fr.gmo.scrumboard.model.SprintDetail

data class SprintDetailViewModel(var details: SprintDetail,
                                 var totalCapacity:String=details.totalCapacity.toString(),
                                 var absence:String=details.absence.toString(),
                                 var ratio:String = details.ratio.toString(),
                                 var totalVelocity:String = details.totalComplexity.toString(),
                                 var donePoint:String = details.donePoints.toString(),
                                 var charge:Double = details.totalCharge) {
    fun toDetails(): SprintDetail {
        totalCapacity.toDoubleOrNull()?.let { details.totalCapacity = it }
        absence.toDoubleOrNull()?.let { details.absence = it }
        ratio.toDoubleOrNull()?.let { details.ratio = it }
        totalVelocity.toDoubleOrNull()?.let { details.totalComplexity = it }
        donePoint.toDoubleOrNull()?.let { details.donePoints = it }
        details.totalCharge = charge
        return details
    }
}

@Composable
fun SprintDetailsComponent(details: SprintDetailViewModel, onUpdate: (SprintDetailViewModel) -> Boolean) {
    var viewModel by mutableStateOf(details)

    Row {
        Column(modifier = Modifier.padding(padding_m).weight(1F, true)) {
            TextInputComponent("Capacité totale", viewModel.totalCapacity) {
                onUpdate(viewModel.copy(totalCapacity = it))
            }

            Divider(color = Color.DarkGray, thickness = 2.dp, modifier = Modifier.padding(padding_m))
            TextInputComponent("Abs non prévues", viewModel.absence) {
                onUpdate(viewModel.copy(absence = it))
            }
            Text("Dont charge (spike, ano, action retro...) : ${viewModel.charge}")
            Divider(color = Color.DarkGray, thickness = 2.dp, modifier = Modifier.padding(padding_m))
            Text(
                "Capacité réelle : ${
                    getCapacity(viewModel.totalCapacity, viewModel.absence, viewModel.charge)
                }"
            )
        }

        Column(modifier = Modifier.padding(padding_m).weight(1F, true)) {
            TextInputComponent("Ratio", viewModel.ratio) {
                onUpdate(viewModel.copy(ratio = it))
            }

            Text(
                "Vélocité estimée : ${
                    getVelocity(viewModel.totalCapacity, viewModel.ratio, viewModel.charge)
                }"
            )
            Text("Points mis dans le sprint : ${viewModel.totalVelocity}")
            TextInputComponent("Points effectués", viewModel.donePoint) {
                onUpdate(viewModel.copy(donePoint = it))
            }
        }
    }
}

fun getCapacity(totalCapacity:String, absence:String, totalCharge:Double): String {
    val totalCapacityD = totalCapacity.toDoubleOrNull()
    val absenceD = absence.toDoubleOrNull()
    if (totalCapacityD != null && absenceD != null) {
        return (totalCapacityD - totalCharge - absenceD).toString()
    }
    return "-"
}
fun getVelocity(totalCapacity: String, ratio:String, totalCharge: Double): String {
    val totalCapacityD = totalCapacity.toDoubleOrNull()
    val ratioD = ratio.toDoubleOrNull()
    if (totalCapacityD != null && ratioD != null) {
        return ((totalCapacityD - totalCharge) * ratioD).toString()
    }
    return "-"
}