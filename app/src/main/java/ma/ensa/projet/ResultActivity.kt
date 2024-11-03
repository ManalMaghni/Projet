package ma.ensa.projet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Receive the username from the intent and set it as an empty string if null
        val username = intent.getStringExtra("USERNAME") ?: "" // Ensuring it’s an empty string, not "Joueur"
        val score = intent.getIntExtra("SCORE", 0)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)

        CoroutineScope(Dispatchers.IO).launch {
            // Update the user's score in the database
            updateScoreInDatabase(username, score)

            // Prepare the result message based on the score
            val resultMessage = when {
                score == totalQuestions -> "Félicitations $username ! Vous avez réussi à obtenir un score parfait de $score sur $totalQuestions ! 🎉"
                score >= (totalQuestions * 0.8).toInt() -> "Excellent travail, $username ! Avec un score de $score sur $totalQuestions, vous démontrez une grande maîtrise ! 🌟"
                score >= (totalQuestions * 0.5).toInt() -> "Bien joué, $username ! Votre score de $score sur $totalQuestions montre que vous avez des bases solides. Continuez à apprendre ! 👍"
                else -> "Pas de souci, $username. Avec un score de $score sur $totalQuestions, vous avez encore des opportunités d'apprentissage. Essayez encore ! 💪"
            }

            // Update UI with result message on the main thread
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.resultText).text = resultMessage

                // Set up the button to play again
                findViewById<Button>(R.id.playAgain).setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        resetScoreInDatabase(username)
                    }
                }
            }
        }
    }

    // Function to update score in the database
    private suspend fun updateScoreInDatabase(username: String, score: Int) {
        val url = "http://10.0.2.2/test_memo/userScore.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { /* Réponse gérée dans la méthode appelée */ },
            { error -> error.printStackTrace() }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf("username" to username, "score" to score.toString())
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }

    // Function to reset score in the database, and navigate back to QuizActivity with the same username
    private suspend fun resetScoreInDatabase(username: String) {
        val url = "http://10.0.2.2/test_memo/userScore.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                val jsonResponse = JSONObject(response)
                if (jsonResponse.optString("message") == "Score updated successfully.") {
                    // Redirecting to QuizActivity with the same username
                    val intent = Intent(this@ResultActivity, QuizActivity::class.java)
                    intent.putExtra("USERNAME", username) // Pass the same username
                    startActivity(intent)
                    finish()
                }
            },
            { error -> error.printStackTrace() }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf("username" to username, "score" to "0")
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}
