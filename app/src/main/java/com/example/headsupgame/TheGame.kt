package com.example.headsupgame

import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.seismic.ShakeDetector

import android.content.Intent
import android.os.CountDownTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.properties.Delegates


abstract class TheGame : AppCompatActivity(), ShakeDetector.Listener {

    // declare variables
    private lateinit var clGame: ConstraintLayout
    private lateinit var tv_portrait_rotate: TextView

    private lateinit var tv_landscape_header: TextView
    private lateinit var cv_landscape_cardView: CardView
    private lateinit var tv_landscape_name: TextView
    private lateinit var tv_landscape_taboo1: TextView
    private lateinit var tv_landscape_taboo2: TextView
    private lateinit var tv_landscape_taboo3: TextView

    private lateinit var sensorManager: SensorManager
    private lateinit var sd: ShakeDetector

    private var listOfCelebrities = ArrayList<CelebrityDetails>()

    private var gameStatus = false
    private var chances: Int = 0
    private var cardsSize: Int = 0
    private var gameScore: Int = 0




    // ----------------------------------------------------------


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_the_game)

        // Assign variables
        initVars()
        if (savedInstanceState != null) {
            chances = savedInstanceState.getInt("chances")
            gameScore = savedInstanceState.getInt("gameScore")
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
        when(DEVICE_ORIENTATION) {
            1 -> handlePortrait()
            2 -> handleLandscape()
            else -> handlePortrait()
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
        gameStatus = false
        // Landscape
        tv_landscape_header.visibility = View.INVISIBLE
        cv_landscape_cardView.visibility = View.INVISIBLE
        tv_landscape_name.visibility = View.INVISIBLE
        tv_landscape_taboo1.visibility = View.INVISIBLE
        tv_landscape_taboo2.visibility = View.INVISIBLE
        tv_landscape_taboo3.visibility = View.INVISIBLE

    }

    private fun handleLandscapeUI() {
        // Portrait
        tv_portrait_rotate.visibility = View.INVISIBLE
        sd.start(sensorManager)
        gameStatus = true
        // Landscape
        tv_landscape_header.visibility = View.VISIBLE
        cv_landscape_cardView.visibility = View.VISIBLE
        tv_landscape_name.visibility = View.VISIBLE
        tv_landscape_taboo1.visibility = View.VISIBLE
        tv_landscape_taboo2.visibility = View.VISIBLE
        tv_landscape_taboo3.visibility = View.VISIBLE
        startGame()

    }
    // ----------------------------------------------------------



    private fun startGame() {
        tv_landscape_name.setText(listOfCelebrities[chances].name)
        tv_landscape_taboo1.setText(listOfCelebrities[chances].taboo1)
        tv_landscape_taboo2.setText(listOfCelebrities[chances].taboo2)
        tv_landscape_taboo3.setText(listOfCelebrities[chances].taboo3)
        if (chances < cardsSize - 1) {
            chances++
        } else {
            Log.d("LoggerTheGame", "Finished all cards")
        }
        if (chances <= 1) {
//            timer.start()
        } else {

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

        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        listOfCelebrities = args!!.getSerializable("listOfCelebrities") as ArrayList<CelebrityDetails>

        cardsSize = listOfCelebrities.size
    }
    // ----------------------------------------------------------


    // save variable on rotation
    override fun onSaveInstanceState(savedInstanceState: Bundle) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("chances", chances)
        savedInstanceState.putInt("gameScore", gameScore)

        // etc.
        super.onSaveInstanceState(savedInstanceState)
    }
    // ----------------------------------------------------------

}