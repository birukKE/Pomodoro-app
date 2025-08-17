package se.umu.bide0023.pomodoroapp
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
 * This is the main activity that contains all the fragments for the app.
 */


class MainActivity : AppCompatActivity() {

    /** Overriden function to initialize mainactivity*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

    }
}