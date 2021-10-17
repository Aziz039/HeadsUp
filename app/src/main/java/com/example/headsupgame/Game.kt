package com.example.headsupgame

import android.content.DialogInterface
import android.content.Intent
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.seismic.ShakeDetector
import java.io.Serializable
import java.util.ArrayList

class Game : AppCompatActivity(), ShakeDetector.Listener {
    // declare variables
    private lateinit var clGame: ConstraintLayout
    private lateinit var tv_portrait_rotate: TextView

    private lateinit var tv_landscape_header: TextView
    private lateinit var cv_landscape_cardView: CardView
    private lateinit var tv_landscape_name: TextView
    private lateinit var tv_landscape_taboo1: TextView
    private lateinit var tv_landscape_taboo2: TextView
    private lateinit var tv_landscape_taboo3: TextView
    private lateinit var tv_landscape_score: TextView

    private lateinit var sensorManager: SensorManager
    private lateinit var sd: ShakeDetector

    private var listOfCelebrities = ArrayList<CelebrityDetails>()

    private var gameEnded = false
    private var chances: Int = 0
    private var cardsSize: Int = 0
    private var gameScore: Int = 0
    private var leftTime: Long = 20000
    private var won = false
    private var startCounting = false

    private lateinit var timer: CountDownTimer

    // ----------------------------------------------------------


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Assign variables
        initVars()
        if (savedInstanceState != null) {
            chances = savedInstanceState.getInt("chances")
            gameScore = savedInstanceState.getInt("gameScore")
            leftTime = savedInstanceState.getLong("leftTime")
            won = savedInstanceState.getBoolean("won")
            gameEnded = savedInstanceState.getBoolean("gameEnded")
            startCounting = savedInstanceState.getBoolean("startCounting")
        }

        if (!gameEnded) {
            timerCount(leftTime)
            tv_landscape_score.setText("Score: ${gameScore }/10")
        } else {
            endGame(won)
        }

        // shake detection
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sd = ShakeDetector(this)

        // two ways to handle device orientation
        // {0 = portrait, 1 = left landscape, 3 = right landscape}
        //val DEVICE_ORIENTATION = getWindowManager().getDefaultDisplay().getRotation();
        // {1 = portrait, 2 = landscape}
        val DEVICE_ORIENTATION = this.resources.configuration.orientation

        // detect device position
        handleGame(DEVICE_ORIENTATION)
    }

    // handle The game
    private fun handleGame(DEVICE_ORIENTATION: Int) {
        if (gameEnded) {
            endGame(won)
        } else {
            when (DEVICE_ORIENTATION) {
                1 -> handlePortrait()
                2 -> handleLandscape()
                else -> handlePortrait()
            }
        }
    }
    // ----------------------------------------------------------


    // handle device orientation
    private fun handlePortrait() {
        handlePortraitUI()
    }

    private fun handleLandscape() {
        handleLandscapeUI()
    }
    // ----------------------------------------------------------


    // handle shake detection
    override fun hearShake() {
        Log.d("LoggerTheGame", "Don't shake me!")
        Toast.makeText(this, "Skip question..", Toast.LENGTH_SHORT).show()
    }
    // ----------------------------------------------------------


    // handle showing and hiding UI elements
    private fun handlePortraitUI() {
        // Portrait
        tv_portrait_rotate.visibility = View.VISIBLE
        sd.stop()
        // Landscape
        tv_landscape_header.visibility = View.VISIBLE
        cv_landscape_cardView.visibility = View.INVISIBLE
        tv_landscape_name.visibility = View.INVISIBLE
        tv_landscape_taboo1.visibility = View.INVISIBLE
        tv_landscape_taboo2.visibility = View.INVISIBLE
        tv_landscape_taboo3.visibility = View.INVISIBLE
        tv_landscape_score.visibility = View.INVISIBLE

    }

    private fun handleLandscapeUI() {
        // Portrait
        tv_portrait_rotate.visibility = View.INVISIBLE
        sd.start(sensorManager)
        // Landscape
        tv_landscape_header.visibility = View.VISIBLE
        cv_landscape_cardView.visibility = View.VISIBLE
        tv_landscape_name.visibility = View.VISIBLE
        tv_landscape_taboo1.visibility = View.VISIBLE
        tv_landscape_taboo2.visibility = View.VISIBLE
        tv_landscape_taboo3.visibility = View.VISIBLE
        tv_landscape_score.visibility = View.VISIBLE
        startGame()
    }
    // ----------------------------------------------------------



    private fun startGame() {
        tv_landscape_name.setText(listOfCelebrities[chances].name)
        tv_landscape_taboo1.setText(listOfCelebrities[chances].taboo1)
        tv_landscape_taboo2.setText(listOfCelebrities[chances].taboo2)
        tv_landscape_taboo3.setText(listOfCelebrities[chances].taboo3)
        if (chances < cardsSize - 1) {
            if (!gameEnded) {
                chances++
//            if(startCounting) {
                gameScore++
//            }
            startCounting = true
            }
        } else {
            Log.d("LoggerTheGame", "Finished all cards")
        }
    }

    private fun timerCount(timeTillFinish: Long) {
        if (timeTillFinish / 1000 <= 1L) {
            tv_landscape_header.setText("Game over!")
        } else {
            timer = object : CountDownTimer(timeTillFinish, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    tv_landscape_header.setText("seconds remaining: " + millisUntilFinished / 1000)
                    leftTime = millisUntilFinished
                    if (gameScore >= 5) {
                        won = true
                        gameEnded = true
                        cancel()
                    }
                }

                override fun onFinish() {
                    if (!won) {
                        tv_landscape_header.setText("Game over!")
                        gameEnded = true
                        won = false
                        cancel()
                    }
                    cancel()
                }
            }
            timer.start()
        }
    }

    // Init variables
    private fun initVars() {
        clGame = findViewById(R.id.clGame)
        tv_portrait_rotate = findViewById(R.id.tv_portrait_rotate)

        tv_landscape_header = findViewById(R.id.tv_landscape_header)
        cv_landscape_cardView = findViewById(R.id.cv_landscape_cardView)
        tv_landscape_name = findViewById(R.id.tv_landscape_name)
        tv_landscape_taboo1 = findViewById(R.id.tv_landscape_taboo1)
        tv_landscape_taboo2 = findViewById(R.id.tv_landscape_taboo2)
        tv_landscape_taboo3 = findViewById(R.id.tv_landscape_taboo3)
        tv_landscape_score = findViewById(R.id.tv_landscape_score)

        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        listOfCelebrities = args!!.getSerializable("listOfCelebrities") as ArrayList<CelebrityDetails>

        cardsSize = listOfCelebrities.size


    }
    // ----------------------------------------------------------


    // end game
    private fun endGame(winner: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        val args: Bundle = Bundle()
        args.putBoolean("won", winner)
        args.putBoolean("returned", true)
        args.putInt("score", gameScore)
        intent.putExtra("RETURN",args);
        startActivity(intent)
    }
    // ----------------------------------------------------------



    // save variable on rotation
    override fun onSaveInstanceState(savedInstanceState: Bundle) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("chances", chances)
        savedInstanceState.putInt("gameScore", gameScore)
        savedInstanceState.putLong("leftTime", leftTime)
        savedInstanceState.putBoolean("won", won)
        savedInstanceState.putBoolean("gameEnded", gameEnded)
        savedInstanceState.putBoolean("startCounting", startCounting)

        // etc.
        super.onSaveInstanceState(savedInstanceState)
    }
    // ----------------------------------------------------------
}