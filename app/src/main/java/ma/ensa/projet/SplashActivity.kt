package ma.ensa.projet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.ensa.projet.databinding.ActivitySplashBinding
import java.util.concurrent.Executors

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val executor = Executors.newSingleThreadExecutor() // Utilisation de l'Executor pour exécuter les tâches en arrière-plan
    private val job = Job() // Création d'un Job pour les Coroutines
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job) // Utilisation de la Coroutine sur le Main Dispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Utilisation de l'Executor pour exécuter l'animation dans un thread séparé
        executor.execute {
            // Animation 1
            runOnUiThread {
                binding.splashImage.animate().alpha(1f).setDuration(1000).withEndAction {
                    // Animation 2 après la première
                    binding.splashImage.animate().rotation(360f).setDuration(1000).withEndAction {
                        // Animation 3 après la deuxième
                        binding.splashImage.animate().scaleX(1.5f).scaleY(1.5f).setDuration(1000).withEndAction {
                            // Lancer une Coroutine pour passer à l'écran de connexion
                            coroutineScope.launch {
                                // Passer à l'écran de connexion sur le thread principal
                                withContext(Dispatchers.Main) {
                                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown() // Fermer l'exécuteur
        job.cancel() // Annuler les coroutines en cours
    }
}

