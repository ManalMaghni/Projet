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
    private val executor = Executors.newSingleThreadExecutor()
    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executor.execute {
            runOnUiThread {
                binding.splashImage.animate().alpha(1f).setDuration(1000).withEndAction {
                    binding.splashImage.animate().rotation(360f).setDuration(1000).withEndAction {
                        binding.splashImage.animate().scaleX(1.5f).scaleY(1.5f).setDuration(1000).withEndAction {
                            coroutineScope.launch {
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
        executor.shutdown()
        job.cancel()
    }
}
