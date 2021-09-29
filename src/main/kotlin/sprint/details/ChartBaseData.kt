package sprint.details

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import common.formatDate
import kotlinx.datetime.toLocalDate
import org.jetbrains.skija.*
import java.awt.BasicStroke
import java.awt.Graphics
import java.awt.Graphics2D

open class ChartBaseData(val totalWidth: Float = 1000F,
                         val totalHeight: Float = 500F,
                         val lineXStart: Float = 50.0F,
                         val lineXEnd: Float = 950.0F,
                         val lineYStart: Float = 25.0F,
                         val lineYEnd: Float = 450.0F,
                         val dashLine: PathEffect = PathEffect.dashPathEffect(floatArrayOf(6.0F, 4.0F)),
                         val dashLineG:BasicStroke = BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0F, floatArrayOf(6.0F, 4.0F), 0F),
                         val font: Font = Font(FontMgr.getDefault().matchFamilyStyle("Roboto", FontStyle.NORMAL), 14F),
                         val textPaint: Paint = Paint(),
                         var ratioDay: Float = 1.0F,
                         var ratioAbs: Float = 1.0F,
                         var maxAbs: Int =0,
                         var days:ArrayList<String> = ArrayList()
) {
    fun drawBase(scope:DrawScope?=null, graphics:Graphics2D?=null) {
        if (scope == null && graphics == null) {
            return
        }
        val path = CustomPath()
        // main line
        path.moveTo(lineXStart, lineYStart)
        path.lineTo(lineXStart, lineYEnd)
        path.lineTo(lineXEnd, lineYEnd)
        //arrow up
        path.moveTo(lineXStart, lineYStart)
        path.lineTo(lineXStart - 5.0F, lineYStart + 10.0F)
        path.moveTo(lineXStart, lineYStart)
        path.lineTo(lineXStart + 5.0F, lineYStart + 10.0F)
        // arrow down
        path.moveTo(lineXEnd, lineYEnd)
        path.lineTo(lineXEnd - 10.0F, lineYEnd - 5.0F)
        path.moveTo(lineXEnd, lineYEnd)
        path.lineTo(lineXEnd - 10.0F, lineYEnd + 5.0F)

        // days
        days.let { days ->
            var i = 0
            days.forEach {day ->
                val posX = lineXStart + (ratioDay * i)
                drawText(scope, graphics, formatDate(day.toLocalDate()), posX, lineYEnd+12.0F)
                path.moveTo(posX, lineYEnd - 5.0F)
                path.lineTo(posX, lineYEnd + 5.0F)
                i++
            }
        }
        // tasks
        (0..maxAbs).forEach { j ->
            val posY = lineYEnd - (ratioAbs * j)
            if (j%5==0) {
                drawText(scope, graphics, j.toString(), lineXStart - 20, posY + 5F)
            }
            path.moveTo(lineXStart - 5.0F, posY)
            path.lineTo(lineXStart + 5.0F, posY)
        }
        drawPath(scope, graphics, path)
    }

    fun drawText(scope: DrawScope?, graphics:Graphics?, text:String, posX: Float, posY: Float) {
        scope?.drawIntoCanvas { it.nativeCanvas.drawTextLine(TextLine.make(text, font), posX, posY, textPaint) }
        graphics?.color = java.awt.Color.BLACK
        graphics?.drawString(text, posX.toInt(), posY.toInt())
    }

    fun drawRect(scope: DrawScope?, graphics:Graphics2D?, x:Float, y: Float, width:Float, height:Float, colorS:Color, colorG:java.awt.Color, fill: Boolean = true) {
        if (fill) {
            scope?.drawRect(colorS, topLeft = Offset(x, y), size = Size(width, height))
        } else {
            scope?.drawRect(colorS, topLeft = Offset(x, y), size = Size(width, height), style = Stroke())
        }

        graphics?.color = colorG
        graphics?.stroke = BasicStroke(1F)
        if (fill) {
            graphics?.fillRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
        } else {
            graphics?.drawRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
        }
    }

    fun drawLine(scope: DrawScope?, graphics:Graphics2D?, x1:Float, y1: Float, x2:Float, y2:Float, colorS:Color= Color.Black, colorG:java.awt.Color= java.awt.Color.BLACK, isDash:Boolean = false) {
        if (isDash) {
            scope?.drawLine(color = colorS, Offset(x1, y1), Offset(x2, y2), pathEffect = dashLine)
        } else {
            scope?.drawLine(color = colorS, Offset(x1, y1), Offset(x2, y2))
        }

        graphics?.color = colorG
        graphics?.stroke = if (isDash) dashLineG else BasicStroke(1F)
        graphics?.drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
    }

    fun drawPath(
        scope: DrawScope?,
        graphics: Graphics2D?,
        path: CustomPath,
        colorS: Color = Color.Black,
        colorG: java.awt.Color = java.awt.Color.BLACK,
        isDash: Boolean = false,
        isFilled: Boolean = false
    ) {
        if (isDash) {
            scope?.drawPath(path.path, colorS, style = Stroke(pathEffect= dashLine))
        } else if (isFilled){
            scope?.drawPath(path.path, colorS, style = Fill)
        } else {
            scope?.drawPath(path.path, colorS, style = Stroke())
        }

        graphics?.color = colorG
        graphics?.stroke = if (isDash) dashLineG else BasicStroke(1F)
        path.polylines.forEach {
            if (isFilled) {
                graphics?.fillPolygon(it.xList.toIntArray(), it.yList.toIntArray(), it.xList.size)
            } else {
                graphics?.drawPolyline(it.xList.toIntArray(), it.yList.toIntArray(), it.xList.size)
            }
        }
    }
}

class CustomPath(val path: Path = Path(), val polylines: ArrayList<Polyline> = arrayListOf()) {
    var lineIndex = -1

    fun moveTo(x:Float, y:Float) {
        path.moveTo(x, y)
        lineIndex ++;
        polylines.add(Polyline())
        polylines[lineIndex].xList.add(x.toInt())
        polylines[lineIndex].yList.add(y.toInt())
    }

    fun lineTo(x: Float, y: Float) {
        path.lineTo(x,y)
        polylines[lineIndex].xList.add(x.toInt())
        polylines[lineIndex].yList.add(y.toInt())
    }
}
class Polyline(val xList:ArrayList<Int> = arrayListOf(), val yList:ArrayList<Int> = arrayListOf())

enum class SaveStatus {NONE, SAVING, SUCCESS, ERROR}
