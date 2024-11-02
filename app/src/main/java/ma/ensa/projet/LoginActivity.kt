
package ma.ensa.projet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper


import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity



import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var usernameInput: EditText
    private lateinit var loginButton: Button
    private lateinit var ppName: TextView
    private val url = "http://10.0.2.2/test_memo/userScore.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usernameInput = findViewById(R.id.usernameInput)
        loginButton = findViewById(R.id.loginButton)
        requestQueue = Volley.newRequestQueue(this)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            if (username.isNotEmpty()) {
                checkUsername(username)
            } else {
                Toast.makeText(this, "Veuillez entrer un nom d'utilisateur", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkUsername(username: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                try {



                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("existe")) {

                        val existingScore = jsonResponse.getInt("score")
                        showExistingUserPopup(username, existingScore)
                    } else {

                        addUserToDatabase(username)
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Erreur lors de l'analyse de la réponse: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error: VolleyError ->
                Toast.makeText(this, "Erreur réseau: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["score"] = "0"
                return params
            }
        }
        requestQueue.add(stringRequest)
    }



    private fun showExistingUserPopup(username: String, score: Int) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Utilisateur existant")
        builder.setMessage("Bienvenu $username, votre dernier score était : $score")

        val dialog = builder.create()

        dialog.show()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            dialog.dismiss()

            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("USERNAME", username)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun addUserToDatabase(username: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "Utilisateur ajouté avec succès", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, QuizActivity::class.java).apply {
                    putExtra("USERNAME", username)
                })
                finish()
            },
            Response.ErrorListener { error: VolleyError ->
                Toast.makeText(this, "Erreur lors de l'ajout de l'utilisateur: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["score"] = "0"
                return params
            }
        }
        requestQueue.add(stringRequest)
    }
}



