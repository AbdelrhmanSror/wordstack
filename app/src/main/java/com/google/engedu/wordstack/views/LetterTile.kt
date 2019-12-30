package com.google.engedu.wordstack.views

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

@SuppressLint("ViewConstructor")
class LetterTile(context: Context?, letter: Char) : TextView(context) {
    private var frozen = false
    fun moveToViewGroup(targetView: ViewGroup) {
        when (val parent = parent) {
            is StackedLayout -> {
                parent.pop()
                targetView.addView(this)
                freeze()
                visibility = View.VISIBLE
            }
            else -> {
                val owner = parent as ViewGroup
                owner.removeView(this)
                (targetView as StackedLayout).push(this)
                unfreeze()

            }
        }
    }

    fun freeze() {
        frozen = true
    }

    fun unfreeze() {
        frozen = false
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_DOWN && !frozen) {
            // Create a new ClipData.
            // This is done in two steps to provide clarity. The convenience method
            // ClipData.newPlainText() can create a plain text ClipData in one step.

            // Create a new ClipData.Item from the ImageView object's tag
            val item = ClipData.Item(this.tag as? CharSequence)
            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This will create a new ClipDescription object within the
            // ClipData, and set its MIME type entry to "text/plain"
            val dragData = ClipData(
                    this.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item)

            // Instantiates the drag shadow builder.
            val myShadow = DragShadowBuilder(this)

            // Starts the drag
            this.startDrag(
                    dragData,   // the data to be dragged
                    myShadow,   // the drag shadow builder
                    this,       // no need to use local data
                    0           // flags (not currently used, set to 0)
            )
            return true
        }
        return false
    }

    companion object {
        const val TILE_SIZE = 150
    }

    init {
        text = letter.toString()
        textAlignment = View.TEXT_ALIGNMENT_CENTER
        height = TILE_SIZE
        width = TILE_SIZE
        textSize = 30f
        setBackgroundColor(Color.rgb(255, 255, 200))
    }


}
