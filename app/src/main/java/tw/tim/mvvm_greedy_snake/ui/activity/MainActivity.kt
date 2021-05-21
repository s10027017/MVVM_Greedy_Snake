package tw.tim.mvvm_greedy_snake.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import tw.tim.mvvm_greedy_snake.ui.viewmodel.MainViewModel
import tw.tim.mvvm_greedy_snake.R
import tw.tim.mvvm_greedy_snake.enums.Direction
import tw.tim.mvvm_greedy_snake.enums.GameState

/**
 *  實作MVVM 貪吃蛇  綠豆湯學院
 *  https://www.youtube.com/watch?v=LMpJ35tndUw&t=25s
 */
// 抓取 viewModel
// lambda
// GameView
// BUG 1 Score 無即時更新
// BUG 2 當沒有初始化為向左的話 再你往右邊撞牆後 再按REPLAY 將會無限循環
// BUG 3 連點按鈕會造成多個錯誤
// BUG ?

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // gradle 要+ implementation 'androidx.appcompat:appcompat:1.1.0'
        // implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0' 不然找不到ViewModelProvider(this).get 這個fun
        // https://stackoverflow.com/questions/49405616/cannot-resolve-symbol-viewmodelproviders-on-appcompatactivity
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initUniObserve()
        initButtons()
    }

    /**
     *  建立唯一觀察者
     */
    private fun initUniObserve() {
        // gradle plugins 要擴展kotlin-android-extensions 才能省略findViewByID
        // https://blog.csdn.net/feiyukill/article/details/72781965
        // 在這之前還有Butter Knife奶油刀也可以做到這件事, 有興趣可以看看
        // DataBinding 也可以, 而且更多功能
        viewModel.snake.observe(this,{
            game_view.snakeBody = it
            // 強制重繪View，調用invalidate()
            // http://androidbiancheng.blogspot.com/2010/05/viewinvalidate.html
            game_view.invalidate()
        })

        // 更新紅點
        viewModel.bonusPosition.observe(this,{
            game_view.updateBonus(it)
        })

        // 更新遊戲分數
        viewModel.scoreData.observe(this, {
            score.text = it.toString()
            Log.e("it.toString()",it.toString())
        })

        // 依照遊戲狀態更新
        viewModel.gameState.observe(this, {
            Log.e("it",it.toString() )
            if (it == GameState.GAME_OVER) {
                AlertDialog.Builder(this)
                    .setTitle("Game State")
                    .setMessage("GAME OVER")
                    // dialog, which -> Lambda寫法 省略很多
                    .setPositiveButton("REPLAY") { dialog, which ->
                        viewModel.start()
                    }
                    .setNeutralButton("Cancel") { dialog, which ->
                        dialog.cancel()
                    }
                    .show()
            }
        })

        // 遊戲開始
        viewModel.start()

    }

    /**
     *  Button 按鈕事件
     */
    private fun initButtons() {
        left.setOnClickListener { viewModel.move(Direction.LEFT) }
        right.setOnClickListener { viewModel.move(Direction.RIGHT) }
        top.setOnClickListener { viewModel.move(Direction.TOP) }
        down.setOnClickListener { viewModel.move(Direction.DOWN) }
        restart.setOnClickListener { viewModel.start() }
    }

}