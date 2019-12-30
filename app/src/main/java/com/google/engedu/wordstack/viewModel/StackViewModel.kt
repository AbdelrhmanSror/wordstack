package com.google.engedu.wordstack.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class StackViewModel(application: Application) : AndroidViewModel(application) {
    private val mApplication = application
    //game started of or ended
    private val _gameState = MutableLiveData<Boolean?>()
    val gameState: LiveData<Boolean?>
        get() = _gameState

    private val _gameUndo = MutableLiveData<Boolean?>()
    val gameUndo: LiveData<Boolean?>
        get() = _gameUndo

    companion object {
        private const val WORD_LENGTH = 5

    }

    private val words = ArrayList<String>()
    private val random = Random()
    var word1: String? = null
        private set
    var word2: String? = null
        private set

    private var counter1 = 0
    private var counter2 = 0

    fun gameStarted() {
        reset()
        setRandomWord()
        _gameState.value = true
    }

    private fun reset() {
        counter1 = 0
        counter2 = 0
        word1 = null
        word2 = null
    }

    fun gameEnded() {
        _gameState.value = false

    }

    fun gameUndo() {
        _gameUndo.value = true
    }

    fun gameUndoDone() {
        _gameUndo.value = null

    }

    fun gameStartedDone() {
        _gameState.value = null

    }

    init {
        readWord()


    }
    private fun setRandomWord(){
        word1 = words[random.nextInt(words.size)]
        word2 = words[random.nextInt(words.size)]
    }

    private fun readWord() {
        val assetManager = mApplication.assets
        try {
            val inputStream = assetManager.open("words.txt")
            val `in` = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (`in`.readLine().also { line = it } != null) {
                val word = line?.trim { it <= ' ' }
                //make sure that word is at least 5 char
                if (word!!.length == WORD_LENGTH)
                    words.add(word)
            }
        } catch (e: IOException) {
            val toast = Toast.makeText(mApplication, "Could not load dictionary", Toast.LENGTH_LONG)
            toast.show()
        }
    }

    fun pickRandomLetterFromWord(): Char? {
        var char: Char? = null
        val randomWord = random.nextInt(2)
        //if counter reached the the end of specific word then we move to the other word and continue with that word
        if (counter1 >= word1!!.length && counter2 < word2!!.length || (counter2 < word2!!.length && randomWord == 1)) {
            char = word2!![counter2]
            counter2++
        } else if (counter2 >= word2!!.length && counter1 < word1!!.length || (counter1 < word1!!.length && randomWord == 0)) {
            char = word1!![counter1]
            counter1++
        }
        return char

    }


}