package ma.ensa.projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.ensa.projet.bean.Question
import ma.ensa.projet.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private var questions: List<Question> = listOf()
    private var score = 0
    private var currentQuestionIndex = 0
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = intent.getStringExtra("USERNAME")
        // Chargez les questions
        loadQuestions()
    }

    private fun loadQuestions() {
        // Démarre une coroutine dans le contexte IO pour charger les questions sans bloquer le thread principal
        CoroutineScope(Dispatchers.IO).launch {
            questions = fetchQuestions()
            // Utilise withContext pour revenir au thread principal afin d'afficher les questions
            withContext(Dispatchers.Main) {
                displayQuestion()
            }
        }
    }

    private fun fetchQuestions(): List<Question> {
        return listOf(
            Question(
                1, "Quel pays est le plus grand consommateur de café ?",
                "https://www.un-amour-de-cafe.com/files/Blog/shutterstock_135404954.jpg",
                listOf("Norvège", "Finlande", "Suède", "Danemark"),
                "Finlande"
            ),
            Question(
                7, "Identifie ce fruit.", // Litchi
                "https://st4.depositphotos.com/37010776/40711/i/450/depositphotos_407117606-stock-photo-rambutan-sweet-fruit-isolated-white.jpg",
                listOf("Ramboutan", "Litchi", "Mangoustan", "Longane"),
                "Ramboutan"
            ),
            Question(
                2, "Comment est appelée cette tour ?", // Tour de Pise
                "https://static.nationalgeographic.fr/files/styles/image_3200/public/iStock-929861782.jpg?w=1600",
                listOf("tour de Rome", "tour de Naples", "tour de Pise", "tour de Florence"),
                "tour de Pise"
            ),
            Question(
                3, "Comment est appelé cet animal ?", // Raton laveur
                "https://www.fdc43.chasseauvergnerhonealpes.com/wp-content/uploads/sites/4/2018/07/raton-1-1200x675.jpg",
                listOf("Blairot", "Mangouste", "Raton laveur", "Opossum"),
                "Raton laveur"
            ),
            Question(
                4, "Ce drapeau représente quel pays ?", // Chili
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Flag_of_Chile.svg/2560px-Flag_of_Chile.svg.png",
                listOf("Philippines", "Costa Rica", "Chili", "Panama"),
                "Chili"
            ),
            Question(
                5, "Quel est ce monument ?", // Le Colisée
                "https://www.merveilles-monde.com/image/large/colisee-rome2.jpg",
                listOf("Théâtre antique d'Orange-France ", "Arènes de Nîmes-France", "Le Colisée-Italie", "Temple de Baalbek-Liban"),
                "Le Colisée-Italie"
            ),



            Question(
                8, "Identifie ce fruit.", // Fruit du dragon
                "https://www.academiedugout.fr/images/5340/1200-auto/fotolia_36244908_subscription_xl-copy.jpg?poix=50&poiy=50",
                listOf("Papaye", "Carambole", "Fruit du dragon", "Grenade"),
                "Carambole"
            ),
            Question(
                9, "Quel pays est le plus grand consommateur de céréales ?", // Montre un pays ou des céréales
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTH18kJiXKPQnKo1ncyBY29V1XPADjmoAemPQ&s",
                listOf("Inde", "États-Unis", "Chine", "Indonésie"),
                "Chine"
            ),
            Question(
                6, "Quel est ce monument ?", // Mont-Saint-Michel
                "https://thumbs.dreamstime.com/b/mont-saint-michel-au-cr%C3%A9puscule-au-cr%C3%A9puscule-normandie-france-59634362.jpg",
                listOf("Abbaye de Cluny", "Cathédrale de Chartres", "Mont-Saint-Michel", "Notre-Dame de Paris"),
                "Mont-Saint-Michel"
            ),
            Question(
                10, "Ce drapeau représente quel pays ?",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPIAAADQCAMAAAAK0syrAAAAllBMVEXgUgYMsCv////gRAD11snM7NEAqw3r6+v8/////v/gUQDdRADfTAD9//3fQwDeRgDnxrveUwnv7O673MH66+T33NT10sr6+PHxvKjmflHbTADxv6/38OftlXjtm37wsZfwt53iekv+//jiXh/lYyvfZifneU/phFntpov0yLjnjGv45d7rs5blQADrvKTgXxHqfVzqmXMnEYH5AAACq0lEQVR4nO3a21LbMBhFYVVOixzLNoYEQ3ChpJT0QNL2/V+uKjAtQ2OXqz8z2uu7gbtojeRDpDgHAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADwTCHHHc/EHLsjL+aIZAEkKyBZAckKSFZAsgKSFRwmua7r4H1Ifw/w4YdJDn6xPDtbLtI/9uyTQ+jPL1ZNVRRVs7q47IN1tn3y8L4p4vxx320e2/ZqMB6AefJ1EV33fLsxth+8t1zhxsn9TTV/ucU6bza1X9s1myaH4WPct68cb/tMZzms9xen5k+GjyvTWb4bKXau3diNwi45hOtq9Mika3beap7tkssh/nPnenYPi2aXs+HCvmrHi9PS/pxfcj++rB+nOb+FfVl0U8ld8cVoIFbJpd9MXcpJ/GoyELvkOsRuMnnedSYDMUsuw2Ly5pUWdmyH2uQOZjbLy2Y62bnmW11aDMXs9jX7f/IyZJUcXpNs85gyS37NwvZ5zfKinX5Gpfevweb9yyg53Yvj5JtIeko5n9cs+/QqMv32lV5F8rqWvd8Vk5PcbXN74UxfK6bfRXL8WhGuivEbWJfll0c/rKYu5q63Goddcu3vt6PN3TbHjaC0tG8mtvvy3Meu++9jm7q36zw3dUv/e+t+zz1sdWt2IXv7A5q7PY+q6sfacgzWx3Dhvn0RHYud7THzAQ5bf1ZPh63d02Gr8QGzeXKoQ39+E5sqaVYXu96Xxr+eOMAPJx4KF8vZbPlwmF7abP/8xS+CDFlP7h/MsgKSFZCsgGQFJCsgWYFk8smhh2DtxJ2+FXPq3slxb+SQrIBkBSQrIFkByQpIVkCyApIVkKyAZAUkKyBZAckKSFZAsgKSFZCsgGQFJCsgWQHJCkhWQLICkhWQrIBkBSQrIFkByQpIVkCyApIVkKxAMPkXHgDhUc5ES4wAAAAASUVORK5CYII=",
                listOf("Niger",  "Mali", "Zambie","Nigeria"),
                "Niger"
            )
        )
    }

    private fun displayQuestion() {
        // Réinitialiser les couleurs de fond avant d'afficher une nouvelle question
        binding.optionsLayout.removeAllViews() // Effacez les anciennes options

        val currentQuestion = questions[currentQuestionIndex]
        binding.questionText.text = currentQuestion.questionText

        // Chargez l'image ici avec Glide
        Glide.with(this)
            .load(currentQuestion.imageUrl)
            .placeholder(R.drawable.placeholder_image) // Optionnel : une image de remplacement pendant le chargement
            .into(binding.questionImage)

        for (option in currentQuestion.options) {
            val button = Button(this)
            button.text = option
            button.setBackgroundColor(getColor(android.R.color.transparent)) // Réinitialiser la couleur de fond
            button.setOnClickListener {
                handleOptionClick(button, option, currentQuestion.correctAnswer)
            }
            binding.optionsLayout.addView(button)
        }

        binding.nextButton.visibility = View.GONE // Cacher le bouton "Suivant" au début
    }

    private fun handleOptionClick(button: Button, selectedOption: String, correctAnswer: String) {
        // Vérifiez si l'option sélectionnée est correcte
        if (selectedOption == correctAnswer) {
            button.setBackgroundColor(getColor(android.R.color.holo_green_light)) // Correct
            score++
        } else {
            button.setBackgroundColor(getColor(android.R.color.holo_red_light)) // Incorrect
            highlightCorrectAnswer(correctAnswer) // Mettre en surbrillance la bonne réponse
        }

        // Désactiver tous les boutons après une sélection
        disableAllOptions()

        // Montrer le bouton "Suivant" après avoir sélectionné une option
        binding.nextButton.visibility = View.VISIBLE
        binding.nextButton.setOnClickListener {
            // Passez à la question suivante ou montrez les résultats
            currentQuestionIndex++
            if (currentQuestionIndex < questions.size) {
                displayQuestion()
            } else {
                showResults()
            }
        }
    }

    private fun disableAllOptions() {
        for (i in 0 until binding.optionsLayout.childCount) {
            val button = binding.optionsLayout.getChildAt(i) as Button
            button.isEnabled = false // Désactiver chaque bouton
        }
    }

    private fun highlightCorrectAnswer(correctAnswer: String) {
        for (i in 0 until binding.optionsLayout.childCount) {
            val button = binding.optionsLayout.getChildAt(i) as Button
            if (button.text == correctAnswer) {
                button.setBackgroundColor(getColor(android.R.color.holo_green_light)) // Bonne réponse
            }
        }
    }

    private fun showResults() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("SCORE", score)
        intent.putExtra("USERNAME", username)
        intent.putExtra("TOTAL_QUESTIONS", questions.size)
        startActivity(intent)
        finish() // Terminez l'activité actuelle
    }
}

