package com.example.headsupgame

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable





class MainActivity : AppCompatActivity() {
    // declare vars
    private lateinit var clMain: ConstraintLayout
    private lateinit var tv_title: TextView
    private lateinit var bt_start: Button
    private lateinit var tv_score: TextView
    private var won: Boolean = false
    private var returned: Boolean = false
    private var score: Int = 0


    val listOfCelebrities = ArrayList<CelebrityDetails>()
    var listIsLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // init vars
        initVars()

        CoroutineScope(Dispatchers.IO).launch {
            getAllCelebrities()
            withContext(Dispatchers.Main) {}
        }
    }

    // init vars
    private fun initVars() {
        clMain = findViewById(R.id.clMain)
        tv_title = findViewById(R.id.tv_title)
        bt_start = findViewById(R.id.bt_start)
        tv_score = findViewById(R.id.tv_score)


        bt_start.setOnClickListener { startTheGame() }

        listIsLoaded = false

        val intent = intent
        val args = intent.getBundleExtra("RETURN")

        if (args != null) {
            won = args!!.getBoolean("won")
            returned = args!!.getBoolean("returned")
            score = args!!.getInt("score")

            tv_score.setText("Latest score: $score")
            customAlert(won)
        }
    }

    // start the game
    private fun startTheGame() {

        if (listIsLoaded) {
            val intent = Intent(this, Game::class.java)
            val args: Bundle = Bundle()
            args.putSerializable("listOfCelebrities", listOfCelebrities as Serializable?)
            intent.putExtra("BUNDLE",args);
            startActivity(intent)
        } else {
            Log.d("LoggerMainActivity", "Data isn't loaded yet..")
            Toast.makeText(clMain.context, "Data isn't loaded yet..", Toast.LENGTH_SHORT).show()
        }
    }

    // Get all celebrities from API
    private fun getAllCelebrities() {
        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        val getAll: Call<ArrayList<CelebrityDetails>> = apiInterface?.getAll()!!
        getAll.enqueue(object: Callback<ArrayList<CelebrityDetails>> {
            override fun onResponse(
                call: Call<ArrayList<CelebrityDetails>>,
                response: Response<ArrayList<CelebrityDetails>>
            ) {
                Log.d("LoggerMainActivity", "Fetched successfully..")
                for (celebrity in response.body()!!) {
                    val tempCelebrity = CelebrityDetails(

                        celebrity.name,
                        celebrity.taboo1,
                        celebrity.taboo2,
                        celebrity.taboo3,
                        celebrity.pk
                    )
                    listOfCelebrities.add(tempCelebrity)
                }
                listIsLoaded = true
            }

            override fun onFailure(call: Call<ArrayList<CelebrityDetails>>, t: Throwable) {
                Log.d("LoggerMainActivity", "Connection failed.. $t")
                Toast.makeText(clMain.context, "Connection failed..", Toast.LENGTH_SHORT).show()
            }

        })
    }
    // ----------------------------------------------------------

    private fun customAlert(wonTheGame: Boolean){
        // first we create a variable to hold an AlertDialog builder
        val dialogBuilder = AlertDialog.Builder(this)

        var alertTitle: String
        if (wonTheGame) {
            alertTitle = "Congrats, you won!"
        } else {
            alertTitle = "You lost!"
        }

        // positive button text and action
        dialogBuilder.setMessage("Score: $score/10").setPositiveButton("Play Again!", DialogInterface.OnClickListener {
                dialog, id ->
        })
        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle(alertTitle)

        // show alert dialog
        alert.show()
    }

}