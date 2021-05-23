package tw.tim.mvvm_greedy_snake.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.tim.mvvm_greedy_snake.data.Position
import tw.tim.mvvm_greedy_snake.enums.Direction
import tw.tim.mvvm_greedy_snake.enums.GameState
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val body = mutableListOf<Position>()
    private val size = 20
    private var score = 0
    private var bonus : Position? = null
    private var direction = Direction.LEFT
    private var flag = true
    var total_score = 0

    // LiveData & MutableLiveData 區別
    // https://www.cnblogs.com/guanxinjing/p/11544273.html
    var snake = MutableLiveData<List<Position>>()
    var bonusPosition = MutableLiveData<Position>()
    var gameState = MutableLiveData<GameState>()
    var scoreData = MutableLiveData<Int>()

    /**
     *  移動
     */
    fun move(dir: Direction){
        direction = dir
    }

    /**
     *  遊戲開始
     */
    fun start(){
        // 防止連點機制  Button連點造成多個的錯誤
        if(flag){

            initialization()

            // kotlin run, let, with, also, apply 用法
            // https://louis383.medium.com/%E7%B0%A1%E4%BB%8B-kotlin-run-let-with-also-%E5%92%8C-apply-f83860207a0c
            // 當第一次吃到紅點 bonusPosition的位置 = 之後隨機產生出來的紅點位置
            bonus = nextBonus().also{
                bonusPosition.value = it
            }

            // TODO 影片後面故意不給看 也可以用Timer().schedule(object : TimerTask() 這種方式做
            fixedRateTimer("timer",true,1,200){
                val pos = body.first().copy().apply {
                    // 按下上下左右鍵做動作
                    when (direction){
                        Direction.LEFT -> x--
                        Direction.RIGHT -> x++
                        Direction.UP -> y--
                        Direction.DOWN -> y++
                    }
                    Log.e("x",x.toString() )
                    Log.e("y",y.toString() )
                    // 左上座標為(0,0)
                    // 判斷吃到自己 & 超出範圍 遊戲結束
                    // x,y 大於等於20, x,y 小於0
                    if(body.contains(this) || x < 0 || x >= size || y < 0 || y >= size){
                        gameState.postValue(GameState.GAME_OVER)
                        flag = true
                        cancel()
                    }
                }
                // 每移動一次 就新增一次 然後判斷是否吃到紅點 吃到就+1 沒有就移除最後一個點
                body.add(0, pos)
                if(pos != bonus){
                    body.removeLast()
                }else{
                    // 吃到紅點 更新紅點位置
                    bonus = nextBonus().also {
                        bonusPosition.postValue(it)
                    }
                    score ++
                    Log.e("score",score.toString())
                    scoreData.postValue(score)
                    total_score = score
                }
                snake.postValue(body)
            }
        }

    }

    /**
     *  初始化
     */
    private fun initialization(){
        flag = false
        // score 當重新開始時 , Score歸零且要通知他更新
        score = 0
        scoreData.postValue(score)

        gameState.postValue(GameState.ONGOING)

        // 當沒有初始化為向左的話 再你往右邊撞牆後 再按REPLAY 將會無限循環
        direction = Direction.LEFT

        body.clear()
        // 一開始給4個body 中心點 固定位置
        body.add(Position(10,10))
        body.add(Position(11,10))
        body.add(Position(12,10))
        body.add(Position(13,10))
    }

    /**
     *  隨機給下個紅點
     */
    private fun nextBonus() : Position {
        return Position(Random.nextInt(size), Random.nextInt(size))
    }

}
