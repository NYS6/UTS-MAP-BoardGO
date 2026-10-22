package id.boardgo.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.data.DummyData
import id.boardgo.app.ui.components.BoardGoGhostButton
import id.boardgo.app.ui.components.GameCover
import id.boardgo.app.ui.components.PlayerAvatar
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.SkyBackdrop
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ========================== LAYAR PROFIL ==================================
 * Meniru layar ME pada web app: avatar bulat + nama Anton uppercase,
 * stat card (rating / games / favorites), bio, tombol Edit & Log out,
 * daftar Favorite Games.
 * =========================================================================
 */
@Composable
fun ProfileScreen(
    viewModel: BoardGOViewModel,
    onGameClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.profileState.collectAsStateWithLifecycle()
    val me = state.me

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 130.dp)
        ) {
            // ---------- Header hero ----------
            item {
                Box {
                    SkyBackdrop(tall = true)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShapeRound)
                                .background(Color.White)
                                .padding(3.dp)
                        ) {
                            PlayerAvatar(
                                playerId = DummyData.CURRENT_USER_ID,
                                size = 88
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = (me?.name ?: "-").uppercase(),
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${(me?.area ?: "-").uppercase()} · ${(me?.level?.label ?: "-").uppercase()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = BoardGoColors.Meeple
                        )
                    }
                }
            }

            // ---------- Stat card ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(value = "${me?.rating ?: "0"}", label = "RATING", modifier = Modifier.weight(1f))
                    StatCard(value = "${me?.gamesPlayed ?: 0}", label = "GAMES", modifier = Modifier.weight(1f))
                    StatCard(value = "${state.favoriteGames.size}", label = "FAVORITES", modifier = Modifier.weight(1f))
                }
            }

            // ---------- Bio ----------
            item {
                Text(
                    text = me?.bio ?: "-",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // ---------- Tombol aksi ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BoardGoGhostButton(
                        text = "Edit profile",
                        onClick = { /* mockup: tidak ada backend */ },
                        modifier = Modifier.weight(1f)
                    )
                    BoardGoGhostButton(
                        text = "Log out",
                        onClick = onLogout,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ---------- Favorite games ----------
            item {
                SectionLabel(
                    text = "Favorite games",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            items(state.favoriteGames, key = { it.id }) { game ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .clickable { onGameClick(game.id) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameCover(
                        gameId = game.id,
                        name = game.name,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = game.name.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${game.category.label.uppercase()} · ${game.duration.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(18.dp),
                        tint = BoardGoColors.Meeple
                    )
                }
            }

            // ---------- Sesi yang diikuti ----------
            item {
                SectionLabel(
                    text = "Joined sessions (${state.joinedSessions.size})",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            if (state.joinedSessions.isEmpty()) {
                item {
                    Text(
                        text = "No upcoming sessions yet — host the first one!",
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
                            .padding(horizontal = 20.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = (game?.name ?: "-").uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${session.day.uppercase()} ${session.time} · ${venue?.name ?: "-"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        id.boardgo.app.ui.components.SeatPill(
                            seatsLeft = session.maxPlayers - session.joinedPlayerIds.size
                        )
                    }
                }
            }
        }
    }
}

/** Lingkaran. */
private val CircleShapeRound = androidx.compose.foundation.shape.CircleShape

/** Kartu statistik (nilai Anton + label mono). */
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
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
