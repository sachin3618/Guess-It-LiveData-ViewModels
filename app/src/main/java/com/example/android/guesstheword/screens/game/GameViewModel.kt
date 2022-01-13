package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel: ViewModel(){



    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    companion object{
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = 60000L

        private const val COUNTDOWN_PANIC_SECONDS = 10L
    }


   private val timer: CountDownTimer

   private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz : LiveData<BuzzType>
    get() = _eventBuzz

   private val _time = MutableLiveData<Long>()
    val time : LiveData<Long>
    get() = _time

    val timeString = Transformations.map(time) { Stringtime ->
        DateUtils.formatElapsedTime(Stringtime)
    }

    private var _word = MutableLiveData<String>()
    val word: LiveData<String>
    get() = _word



    // The current score
    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
    get() = _score

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish : LiveData<Boolean>
    get() = _eventGameFinish

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }



    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            // gameFinished()
            resetList()
        }
        _word.value = wordList.removeAt(0)


    }


    /** Methods for buttons presses **/

     fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {

        _score.value = (score.value)?.plus(1)
        _eventBuzz.value = BuzzType.CORRECT
        nextWord()
    }

    fun onGameFinishComplete(){
        _eventGameFinish.value = false
    }

    fun onBuzzComplete(){
        _eventBuzz.value = BuzzType.NO_BUZZ
    }


    init {
        _eventGameFinish.value = false
        Log.i("GameViewModel", "GameViewModel Created")
        resetList()
        nextWord()
        _score.value = 0

        timer = object: CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){
            override fun onTick(millisUntilFinished: Long) {
                _time.value = (millisUntilFinished/ ONE_SECOND)
                if(millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS){
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _eventGameFinish.value = true
                _eventBuzz.value = BuzzType.GAME_OVER
            }

        }
        timer.start()
    }



    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Log.i("GameViewModel", "GameViewModel Destroyed")

    }
}