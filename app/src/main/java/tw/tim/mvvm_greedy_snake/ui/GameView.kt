package tw.tim.mvvm_greedy_snake.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import tw.tim.mvvm_greedy_snake.data.Position

/**
 *  畫遊戲畫面
 */
class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paintBonus = Paint().apply {
        color = Color.RED
    }
    private val paint: Paint = Paint().apply {
        color = Color.BLUE
    }
    var snakeBody : List<Position>? = null
    var bonus : Position? = null
    var dim = 0
    var gap = 5

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 畫布
        // https://ithelp.ithome.com.tw/articles/10204311
        canvas?.run {
            // 紅點
            bonus?.apply {
                drawRect((x*dim+gap).toFloat(),
                    (y*dim+gap).toFloat(), ((x+1)*dim-gap).toFloat(), ((y+1)*dim-gap).toFloat(), paintBonus)
            }
            // 身體
            snakeBody?.forEach {
                drawRect((it.x*dim+gap).toFloat(), (it.y*dim+gap).toFloat(),
                    ((it.x+1)*dim-gap).toFloat(), ((it.y+1)*dim-gap).toFloat(), paint)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        dim = width / 20
    }

    fun updateBonus(pos: Position){
        bonus = pos
    }

}