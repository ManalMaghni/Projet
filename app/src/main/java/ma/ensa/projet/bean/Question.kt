package ma.ensa.projet.bean



data class Question(
    val id: Int,
    val questionText: String,
    val imageUrl: String,
    val options: List<String>,
    val correctAnswer: String
)

data class Option(
    val optionText: String,
    val isCorrect: Boolean
)
