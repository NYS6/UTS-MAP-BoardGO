package id.boardgo.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.data.DummyData
import id.boardgo.app.ui.components.AvatarStack
import id.boardgo.app.ui.components.InfoPill
import id.boardgo.app.ui.components.PlayerAvatar
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.SkyBackdrop
import id.boardgo.app.ui.components.TopBar
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ====================== LAYAR 5: PROFIL / BERANDA =========================
 * Layar tambahan (kriteria "Layar Bebas") berisi profil pengguna,
 * statistik, game favorit, dan sesi yang diikuti.
 * =========================================================================
 */
@Composable
fun ProfileScreen(
    viewModel: BoardGOViewModel,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.profileState.collectAsStateWithLifecycle()
    val me = state.me

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // ---------- Header ----------
            item {
                Box {
                    SkyBackdrop(tall = true)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 30.dp, bottom = 20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(4.dp)
                            ) {
                                PlayerAvatar(
                                    playerId = DummyData.CURRENT_USER_ID,
                                    size = 64
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = me?.name ?: "-",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "${me?.area ?: "-"} · ${me?.level?.label ?: "-"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            // ---------- Statistik ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        value = "${me?.rating ?: "0"}",
                        label = "Rating",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "${me?.gamesPlayed ?: 0}",
                        label = "Game",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        value = "${state.joinedSessions.size}",
                        label = "Sesi",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---------- Bio ----------
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "Tentang saya",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = me?.bio ?: "-",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ---------- Game favorit ----------
            item {
                SectionLabel(text = "Game favorit")
            }
            items(state.favoriteGames, key = { it.id }) { game ->
                FavoriteRow(
                    name = game.name,
                    meta = "${game.category.label} · ${game.duration}",
                    onClick = { onGameClick(game.id) }
                )
            }

            // ---------- Sesi yang diikuti ----------
            item {
                SectionLabel(text = "Sesi yang diikuti (${state.joinedSessions.size})")
            }
            if (state.joinedSessions.isEmpty()) {
                item {
                    Text(
                        text = "Belum ikut sesi apa pun. Yuk host yang pertama!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(state.joinedSessions, key = { it.id }) { session ->
                    val game = DummyData.gameById(session.gameId)
                    val venue = DummyData.venueById(session.venueId)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = game?.name ?: "-",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${session.day} ${session.time} · ${venue?.name ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            AvatarStack(playerIds = session.joinedPlayerIds)
                        }
                        InfoPill(text = session.fee)
                    }
                }
            }
        }
    }
}

/** Kartu statistik angka besar. */
@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Baris game favorit. */
@Composable
private fun FavoriteRow(
    name: String,
    meta: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = BoardGoColors.Meeple
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = meta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
