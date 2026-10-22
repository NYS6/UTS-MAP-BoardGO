package id.boardgo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import id.boardgo.app.ui.navigation.BoardGoNavHost
import id.boardgo.app.ui.theme.BoardGoTheme
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * Entry point aplikasi BoardGO.
 *
 * MainActivity MEWARISI ComponentActivity (bukan AppCompatActivity) dan
 * memakai setContent { } untuk merender Jetpack Compose — ini inti dari
 * migrasi ke Compose (tidak ada lagi findViewById / inflate XML).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            // Theme Material 3 CUSTOM BoardGO (bukan bawaan Android Studio)
            BoardGoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ViewModel di-share ke seluruh graf navigasi (MVVM)
                    val viewModel: BoardGOViewModel = viewModel()
                    BoardGoNavHost(viewModel = viewModel)
                }
            }
        }
    }
}
