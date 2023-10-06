package gb.com.cartrajectoryanimation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import gb.com.cartrajectoryanimation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var drawingView: DrawingView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        drawingView = binding.drawingView
        binding.btnStart.setOnClickListener {
            drawingView.post {
                drawingView.drawRandomPath()
                drawingView.animateCarAlongPath()
            }
        }
    }
}