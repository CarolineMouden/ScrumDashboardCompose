package sprint.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import common.ActionButton
import common.padding_m
import fr.gmo.scrumboard.model.ItemStatus
import fr.gmo.scrumboard.model.Options
import fr.gmo.scrumboard.model.Sprint
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class CumulChartDraw(
    val options: Options,
    val datas: HashMap<ItemStatus, ArrayList<Int>> = hashMapOf()
) : ChartBaseData() {
    fun draw(scope: DrawScope? = null, graphics: Graphics2D? = null) {
        drawBase(scope, graphics)
        drawLegend(scope, graphics)
        options.sprintItemStatusList.forEach {
            if (!it.isDefault && datas.containsKey(it)) {
                drawArea(scope, graphics, datas.get(it)!!, Color(it.colorComp), java.awt.Color.decode(it.colorGrap))
            }
        }
    }

    fun calculateDatas(sprint: Sprint) {
        days = sprint.days
        maxAbs = sprint.items?.size
        ratioAbs = (lineYEnd - lineYStart - 15.0F) / maxAbs
        sprint.days.let { days ->
            ratioDay = (lineXEnd - lineXStart - 15F) / days.size-1
        }

        options.sprintItemStatusList.forEach{datas.put(it, arrayListOf())}

        sprint.days.forEach { day ->
            var hasValueForThisDay = false
            var mapForThisDay = hashMapOf<ItemStatus, Int>()
            sprint.items.forEach { item ->
                val status = item.history.get(day)
                status?.let { st ->
                    val statusItemList = options.sprintItemStatusList.filter { it.name.equals(st.status) }
                    if (statusItemList.isNotEmpty()) {
                        val statusItem = statusItemList[0]
                        if (!statusItem.isDefault) {
                            hasValueForThisDay = true
                        }
                        if (!mapForThisDay.containsKey(st.status)) {
                            mapForThisDay.put(statusItem, 1)
                        } else {
                            mapForThisDay.put(statusItem, mapForThisDay.getValue(statusItem) + 1)
                        }
                    }
                }
            }
            var olderForThisDay = 0
            options.sprintItemStatusList.asReversed().forEach {
                if (hasValueForThisDay) {
                    if (mapForThisDay.containsKey(it)) {
                        olderForThisDay = mapForThisDay.getValue(it) + olderForThisDay
                    }
                    datas.getValue(it).add(olderForThisDay)
                } else {
                    datas.getValue(it)?.add(-1)
                }
            }
        }
    }

    fun drawLegend(scope:DrawScope?=null, graphics:Graphics2D?=null) {
        var posY = totalHeight - 10.0F
        var posX = lineXStart
        val space = 100.0F
        var rectSize = 15.0F

        options.sprintItemStatusList.forEach {
            if (!it.isDefault) {
                drawRect(scope, graphics, posX, posY - rectSize, rectSize, rectSize, Color(it.colorComp), java.awt.Color.decode(it.colorGrap))
                drawText(scope, graphics, it.name.toLowerCase().capitalize(), posX + rectSize + 5.0F, posY - 5.0F)
                posX = posX + space
            }
        }
    }

    fun drawArea(scope:DrawScope?=null, graphics: Graphics2D?=null, itemList: ArrayList<Int>, color: Color, colorG:java.awt.Color) {
        val path = CustomPath()
        path.moveTo(lineXStart, lineYEnd)
        var i = 0
        var lastX = 0.0F
        itemList.forEach { item ->
            if (item >= 0) {
                lastX = lineXStart + (i * ratioDay)
                path.lineTo(lastX, lineYEnd - (item * ratioAbs))
            }
            i++
        }
        path.lineTo(lastX, lineYEnd)
        path.lineTo(lineXStart, lineYEnd)
        drawPath(scope, graphics, path, color, colorG, isFilled=true)
    }

    fun createImage(name: String, onFinished:(Boolean) -> Unit) {
        try {
            val img = BufferedImage(totalWidth.toInt(), totalHeight.toInt(), BufferedImage.TYPE_INT_RGB)
            img.createGraphics().let {
                it.color = java.awt.Color.WHITE
                it.fillRect(0, 0, totalWidth.toInt(), totalHeight.toInt())
                draw(graphics = it)
            }
            val file = File("${name}_CUMUL.png")
            if (file.exists()) file.delete()
            ImageIO.write(img, "png", file)
            onFinished(true)
        } catch (e:Exception) {
            onFinished(false)
        }
    }
}



@Composable
fun SprintCumulativeChartComponent(sprint: Sprint, options: Options) {
    val saveStatus = remember { mutableStateOf(SaveStatus.NONE) }
    val cumulChartdraw = CumulChartDraw(options)
    cumulChartdraw.calculateDatas(sprint)
    Column {
        Row {
            ActionButton(onClick = {
                saveStatus.value = SaveStatus.SAVING
                cumulChartdraw.createImage(sprint.name) {
                    saveStatus.value = if (it) SaveStatus.SUCCESS else SaveStatus.ERROR
                }
            }, enabled = saveStatus.value != SaveStatus.SAVING) {
                Text("Enregistrer")
            }
            if (saveStatus.value == SaveStatus.SUCCESS) {
                Text("Enregistrement réussi.", modifier = Modifier.padding(padding_m))
            } else if (saveStatus.value == SaveStatus.ERROR) {
                Text("Enregistrement échoué.", modifier = Modifier.padding(padding_m))
            }
        }

        Canvas(modifier = Modifier.width((cumulChartdraw.totalWidth / 1.2).dp).height((cumulChartdraw.totalHeight / 1.2).dp)) {
            cumulChartdraw.draw(scope = this)
        }
    }
}