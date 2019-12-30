package com.google.engedu.wordstack.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import java.util.*

class StackedLayout(context: Context?) : LinearLayout(context) {
    private val tiles: Stack<View?> = Stack()
    fun push(tile: View?) {
        if (!empty())
            removeView(peek())
        tiles.push(tile)
        addView(tile)
    }

    fun pop(): View? {
        // Dropped, reassign View to ViewGroup
        val poppedTile = tiles.pop()
        removeView(poppedTile)
        if (!empty())
            addView(peek())
        return poppedTile
    }

    fun peek(): View? {
        return tiles.peek()
    }

    fun empty(): Boolean {
        return tiles.empty()
    }

    fun clear() {
        tiles.clear()
    }
}