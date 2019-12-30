package com.google.engedu.wordstack

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.engedu.wordstack.databinding.ActivityMainBinding
import com.google.engedu.wordstack.viewModel.StackViewModel
import com.google.engedu.wordstack.views.LetterTile
import com.google.engedu.wordstack.views.StackedLayout
import java.util.*

class MainActivity : AppCompatActivity() {
    private var stackedLayout: StackedLayout? = null
    private val placedTiles: Stack<LetterTile> = Stack()

    private lateinit var binding: ActivityMainBinding
    private val stackViewModel: StackViewModel by lazy {
        ViewModelProvider(this).get(StackViewModel::class.java)
    }
    private val touchListener = object : OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_DOWN && !stackedLayout!!.empty()) {
                val tile = stackedLayout!!.peek() as LetterTile
                placedTiles.push(tile)
                tile.moveToViewGroup((v as ViewGroup))
                isGameEnded()
                return true
            }
            return false
        }

    }

    private fun View.setDragListener() {
        // Creates a new drag event listener
        this.setOnDragListener { v, event ->

            // Handles each of the expected events
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.setBackgroundColor(LIGHT_BLUE)
                        v.invalidate()
                        true
                    } else {
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // Applies a green tint to the View. Return true; the return value is ignored.
                    v.setBackgroundColor(LIGHT_GREEN)
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION ->
                    // Ignore the event
                    true
                DragEvent.ACTION_DRAG_EXITED -> {
                    // Re-sets the color tint to light  blue. Returns true; the return value is ignored.
                    v.setBackgroundColor(LIGHT_BLUE)
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    // Dropped, reassign Tile to the target Layout
                    val tile = event.localState as LetterTile
                    placedTiles.push(tile)
                    tile.moveToViewGroup((v as ViewGroup))
                    isGameEnded()
                    // Invalidates the view to force a redraw
                    v.invalidate()
                    // Returns true. DragEvent.getResult() will return true.
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    // Turns off any color tinting
                    v.setBackgroundColor(Color.WHITE)
                    // Invalidates the view to force a redraw
                    v.invalidate()
                    // returns true; the value is ignored.
                    true
                }
                else -> {
                    // An unknown action type was received.
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    false
                }
            }
        }

    }

    private fun isGameEnded() {
        if (stackedLayout!!.empty()) {
            stackViewModel.gameEnded()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = stackViewModel
        binding.lifecycleOwner = this
        stackedLayout = StackedLayout(this)
        binding.stackContainer.addView(stackedLayout)
        binding.word1.setOnTouchListener(touchListener)
        binding.word1.setDragListener()
        binding.word2.setOnTouchListener(touchListener)
        binding.word2.setDragListener()
        setUpObservers()

    }

    private fun setUpObservers() {
        stackViewModel.gameState.observe(this, Observer {
            it?.let {
                if (it) {
                    clear()
                    binding.messageBox.text = getString(R.string.game_started)
                    onStartGame()
                } else {
                    binding.answerWord1.text = getString(R.string.word1_answer,stackViewModel.word1)
                    binding.answerWord2.text = getString(R.string.word2_answer,stackViewModel.word2)

                }
                stackViewModel.gameStartedDone()
            }
        })
        stackViewModel.gameUndo.observe(this, Observer {

            it?.let {
                //we pop the previous placed view and push it again into stacked layout
                if (!placedTiles.empty()) {
                    placedTiles.pop().moveToViewGroup(stackedLayout as ViewGroup)
                }

                stackViewModel.gameUndoDone()

            }
        })
    }


    private fun clear() {
        binding.word1.removeAllViews()
        binding.word2.removeAllViews()
        stackedLayout?.removeAllViews()

    }


    private fun onStartGame() {
        run loop@{
            while (true) {
                when (val char = stackViewModel.pickRandomLetterFromWord()) {
                    null -> return@loop
                    else -> {
                        stackedLayout?.push(LetterTile(this, char).apply { setDragListener() })
                    }
                }
            }
        }
    }


    companion object {
        val LIGHT_BLUE = Color.rgb(176, 200, 255)
        val LIGHT_GREEN = Color.rgb(200, 255, 200)
    }
}