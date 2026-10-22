package id.boardgo.app.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.boardgo.app.R
import id.boardgo.app.ui.components.BoardGoGhostButton
import id.boardgo.app.ui.components.BoardGoPrimaryButton
import id.boardgo.app.ui.components.SkyBackdrop
import id.boardgo.app.ui.theme.BoardGoColors

/**
 * =================== LAYAR WELCOME / LANDING ==============================
 * Meniru landing screen web app:
 * "FIND PEOPLE. FIND GAMES. PLAY TOGETHER." + foto papan Catan +
 * tombol Create account / I already have an account / skip.
 * Semua aksi menuju layar List (autentikasi dummy).
 * ========================================================================
 */
@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Latar sky penuh bagian atas
        SkyBackdrop(modifier = Modifier.fillMaxWidth().height(560.dp), tall = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "BOARDGO",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(28.dp))

            // Slogan display (putih + oranye, seperti design asli)
            Text(
                text = "FIND PEOPLE.",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "FIND GAMES.",
                style = MaterialTheme.typography.displayLarge,
                color = BoardGoColors.Meeple,
                textAlign = TextAlign.Center
            )
            Text(
                text = "PLAY TOGETHER.",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Foto papan Catan (cover asli dari repo web)
            Image(
                painter = painterResource(R.drawable.game_catan),
                contentDescription = "Catan board",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        4.dp,
                        Color.White.copy(alpha = 0.35f),
                        RoundedCornerShape(28.dp)
                    )
            )

            Spacer(Modifier.weight(1f))

            BoardGoPrimaryButton(
                text = "Create account",
                onClick = onStart, // mockup: tanpa backend
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .clickable(onClick = onStart)
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "I already have an account",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "skip — just browse",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onStart)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}
