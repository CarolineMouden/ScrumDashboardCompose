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
import fr.gmo.scrumboard.model.Options
import fr.gmo.scrumboard.model.Sprint
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.max

class BDChartDraw(
    val options: Options,
    val idealTasks: ArrayList<Float> = ArrayList(),
    val idealPoints: ArrayList<Float> = ArrayList(),
    val openStories: ArrayList<Int> = ArrayList(),
    val openTasks: ArrayList<Int> = ArrayList(),
    val openPoints: ArrayList<Float> = ArrayList(),
    val colorTask: Color = Color(0xFF4CAF50),
    val colorTaskG: java.awt.Color = java.awt.Color.decode("0x4CAF50"),
    val colorPoint: Color = Color(0xFFF44336),
    val colorPointG: java.awt.Color = java.awt.Color.decode("0xF44336"),
    val colorOpenStoriesFill: Color = Color(0xFFFFEBEE),
    val colorOpenStoriesFillG: java.awt.Color = java.awt.Color.decode("0xFFEBEE"),
    val colorOpenStoriesStroke: Color = Color(0xFFE57373),
    val colorOpenStoriesStrokeG: java.awt.Color = java.awt.Color.decode("0xE57373")
) : ChartBaseData() {

    fun draw(scope: DrawScope? = null, graphics: Graphics2D? = null) {
        drawBase(scope, graphics)
        drawLegend(scope, graphics)
        drawOpenStories(scope, graphics)
        drawIdealTasks(scope, graphics)
        drawIdealPoints(scope, graphics)
        drawTasks(scope, graphics)
        drawPoints(scope, graphics)
    }

    fun calculateDatas(sprint:Sprint) {
        days = sprint.days
        var totalPoints = 0.0F
        var totalTasks = 0.0F

        sprint.items.forEach { item ->
            totalPoints = totalPoints + item.point.toFloat()
            totalTasks = totalTasks + item.tasks
        }
        maxAbs = max(totalPoints, totalTasks).toInt()
        ratioAbs = (lineYEnd - lineYStart - 15.0F) / maxAbs.toFloat()

        sprint.days.let { days ->
           ratioDay = (lineXEnd - lineXStart - 15F) / days.size
            var i = 0
            days.forEach {
                if (i == 0) {
                   idealTasks.add(totalTasks)
                   idealPoints.add(totalPoints)
                } else if (i == days.size - 1) {
                   idealTasks.add(0.0F)
                   idealPoints.add(0.0F)
                } else {
                   idealTasks.add(totalTasks - (i * totalTasks / (days.size - 1)))
                   idealPoints.add(totalPoints - (i * totalPoints / (days.size - 1)))
                }
                i++
            }
        }
        sprint.days.forEach { day ->
            var hasValueForThisDay = false
            var storyForThisDay = 0
            var taskForThisDay = 0
            var pointsForThisDay = 0.0F

            sprint.items.forEach { item ->
                val status = item.history.get(day)
                status?.let { st ->
                    val statusItemList = options.sprintItemStatusList.filter { it.name.equals(st.status) }
                    if (statusItemList.isNotEmpty()) {
                        val statusItem = statusItemList[0]
                        if (!statusItem.isDefault) {
                            hasValueForThisDay = true
                            if (!statusItem.isDone) {
                                pointsForThisDay = pointsForThisDay + item.point.toFloat()
                            }
                            if (statusItem.isStarted) {
                                storyForThisDay++
                            }
                            if (statusItem.withTask) {
                                taskForThisDay += status.tasks
                            }
                            if (!statusItem.isDone && !statusItem.isStarted) { // cas backlog
                                taskForThisDay = taskForThisDay + item.tasks
                            }
                        }
                    }
                }
            }
           openStories.add(storyForThisDay)
           openTasks.add(if (hasValueForThisDay) taskForThisDay else -1)
           openPoints.add(if (hasValueForThisDay) pointsForThisDay else -1.0F)
        }
    }

    fun drawOpenStories(scope:DrawScope?=null, graphics:Graphics2D?=null) {
        openStories.let {
            if (it.isNotEmpty()) {
                var i = 0
                it.forEach { task ->
                    if (task > 0.0) {
                        val x = lineXStart + (i * ratioDay)
                        val y = lineYEnd - (task * ratioAbs)
                        val w = ratioDay
                        val h = ratioAbs*task
                        drawRect(scope, graphics, x,y,w,h,colorOpenStoriesFill,colorOpenStoriesFillG)
                        drawRect(scope, graphics, x,y,w,h,colorOpenStoriesStroke,colorOpenStoriesStrokeG, false)
                    }
                    i++
                }
            }
        }

    }

    fun drawLegend(scope:DrawScope?=null, graphics: Graphics2D?=null) {
        var posY = totalHeight - 10.0F
        var posX = lineXStart
        val space = 100.0F
        var rectSize = 15.0F

        // open stories
        drawRect(scope, graphics, posX, posY - rectSize, rectSize, rectSize, colorOpenStoriesFill, colorOpenStoriesFillG)
        drawRect(scope, graphics, posX, posY - rectSize, rectSize, rectSize, colorOpenStoriesStroke, colorOpenStoriesStrokeG, false)

        drawRect(scope, graphics, posX, posY - rectSize, rectSize, rectSize, colorOpenStoriesFill, colorOpenStoriesFillG)
        drawRect(scope, graphics, posX, posY - rectSize, rectSize, rectSize, colorOpenStoriesStroke, colorOpenStoriesStrokeG, false)
        drawText(scope, graphics,"US ouvertes", (posX + rectSize + 5.0).toFloat(), posY - 5.0F)

        // tasks
        posX = posX + space
        drawLine(scope, graphics, posX, posY - (rectSize/2), posX + rectSize, posY - (rectSize/2), colorTask, colorTaskG,true)
        drawText(scope, graphics,"Tâches (idéal)", (posX + rectSize + 5.0).toFloat(), posY - 5.0F)

        posX = posX + space + 20.0F
        drawLine(scope, graphics, posX, posY - (rectSize/2), posX + rectSize, posY - (rectSize/2), colorTask, colorTaskG)
        drawText(scope, graphics,"Tâches", (posX + rectSize + 5.0).toFloat(), posY - 5.0F)

        //points
        posX = posX + space - 10.0F
        drawLine(scope, graphics, posX, posY - (rectSize/2), posX + rectSize, posY - (rectSize/2), colorPoint, colorPointG,true)
        drawText(scope, graphics,"Points (idéal)", (posX + rectSize + 5.0).toFloat(), posY - 5.0F)

        posX = posX + space + 10.0F
        drawLine(scope, graphics, posX, posY - (rectSize/2), posX + rectSize, posY - (rectSize/2), colorPoint, colorPointG)

        drawText(scope, graphics, "Points", (posX + rectSize + 5.0).toFloat(), posY - 5.0F)
    }

    fun drawTasks(scope:DrawScope?=null, graphics:Graphics2D?=null) {
        val path=CustomPath()
        openTasks.let {
            if (it.isNotEmpty()) {
                path.moveTo(lineXStart, lineYEnd - (it[0] * ratioAbs))
                var i = 0
                it.forEach { task ->
                    if (task >= 0) {
                        path.lineTo(lineXStart + (i * ratioDay), lineYEnd - (task * ratioAbs))
                    }
                    i++
                }
            }
        }
        drawPath(scope, graphics, path, colorTask, colorTaskG)
    }

    fun drawPoints(scope:DrawScope?=null, graphics:Graphics2D?=null) {
        val path=CustomPath()
        openPoints.let {
            if (it.isNotEmpty()) {
                path.moveTo(lineXStart, lineYEnd - (it[0] * ratioAbs))
                var i = 0
                it.forEach { task ->
                    if (task >= 0.0) {
                        path.lineTo(lineXStart + (i * ratioDay), lineYEnd - (task * ratioAbs)
                        )
                    }
                    i++
                }
            }
        }
        drawPath(scope, graphics, path, colorPoint, colorPointG)
    }

    fun drawIdealTasks(scope:DrawScope?=null, graphics:Graphics2D?=null) {
        val path=CustomPath()
        idealTasks.let {
            if (it.isNotEmpty()) {
                path.moveTo(lineXStart, lineYEnd - (it[0] * ratioAbs))
                var i = 0
                it.forEach { task ->
                    path.lineTo(lineXStart + (i * ratioDay), lineYEnd - (task * ratioAbs))
                    i++
                }
            }
        }
        drawPath(scope, graphics, path, colorTask, colorTaskG, true)
    }

    fun drawIdealPoints(scope:DrawScope?=null, graphics: Graphics2D?=null) {
        val path = CustomPath()
        idealPoints.let {
            if (it.isNotEmpty()) {
                path.moveTo(lineXStart, lineYEnd - (it[0] * ratioAbs))
                var i = 0
                it.forEach { task ->
                    path.lineTo(lineXStart + (i * ratioDay), lineYEnd - (task * ratioAbs))
                    i++
                }
            }
        }
        drawPath(scope, graphics, path, colorPoint, colorPointG, true)
    }

    fun createImage(name: String, onFinished:(Boolean) -> Unit) {
        val img = BufferedImage(totalWidth.toInt(), totalHeight.toInt(), BufferedImage.TYPE_INT_RGB)
        img.createGraphics().let {
            it.color = java.awt.Color.WHITE
            it.fillRect(0, 0, totalWidth.toInt(), totalHeight.toInt())
            draw(graphics = it)
        }
        val file = File("${name}_BD.png")
        if (file.exists()) file.delete()
        ImageIO.write(img, "png", file)
        onFinished(true)
    }
}


@Composable
fun SprintBurnDownChartComponent(sprint:Sprint, options: Options) {
    val saveStatus = remember { mutableStateOf(SaveStatus.NONE) }
    val bdChartdraw = BDChartDraw(options)
    bdChartdraw.calculateDatas(sprint)

    Column {
        Row {
            ActionButton(onClick = {
                saveStatus.value = SaveStatus.SAVING
                bdChartdraw.createImage(sprint.name) {
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

        Canvas(modifier = Modifier.width((bdChartdraw.totalWidth / 1.2).dp).height((bdChartdraw.totalHeight / 1.2).dp)) {
            bdChartdraw.draw(scope = this)
        }
    }
}