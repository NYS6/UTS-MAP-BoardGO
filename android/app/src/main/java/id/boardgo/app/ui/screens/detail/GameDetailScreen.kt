package id.boardgo.app.ui.screens.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.data.DummyData
import id.boardgo.app.data.model.GameSession
import id.boardgo.app.ui.components.AvatarStack
import id.boardgo.app.ui.components.BoardGoGhostButton
import id.boardgo.app.ui.components.BoardGoPrimaryButton
import id.boardgo.app.ui.components.ErrorScreen
import id.boardgo.app.ui.components.InfoPill
import id.boardgo.app.ui.components.LoadingScreen
import id.boardgo.app.ui.components.PlayerAvatar
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.TopBar
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel
import id.boardgo.app.ui.viewmodel.LoadStatus

/**
 * ====================== LAYAR 2: DETAIL GAME =============================
 * Menerima ARGUMEN "gameId" dari layar List (wajib passing argument).
 * =========================================================================
 */
@Composable
fun GameDetailScreen(
    gameId: String,
    viewModel: BoardGOViewModel,
    onBack: () -> Unit,
    onCreateSession: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()

    // Muat data detail ketika argumen gameId diterima
    LaunchedEffect(gameId) {
        viewModel.loadGameDetail(gameId)
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (state.status) {
            LoadStatus.LOADING -> LoadingScreen(message = "Memuat detail game…")
            LoadStatus.ERROR -> ErrorScreen(
                message = state.errorMessage ?: "Gagal memuat detail",
                onRetry = { viewModel.loadGameDetail(gameId) }
            )
            LoadStatus.SUCCESS -> {
                val game = state.game ?: return@Box

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    // ---------- Hero cover ----------
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            BoardGoColors.SkyDeep,
                                            MaterialTheme.colorScheme.primary
                                        )
                                    )
                                )
                        ) {
                            Text(
                                text = game.name.take(2).uppercase(),
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(top = 20.dp)
                            )
                        }
                    }

                    // ---------- Judul + metadata ----------
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = game.name,
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = viewModel::toggleFavorite) {
                                    Icon(
                                        imageVector = if (state.isFavorite) Icons.Filled.Favorite
                                        else Icons.Filled.FavoriteBorder,
                                        contentDescription = "Favorit",
                                        tint = if (state.isFavorite) BoardGoColors.Primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoPill(text = game.category.label, accent = MaterialTheme.colorScheme.primary)
                                InfoPill(text = game.difficulty.label)
                            }

                            Spacer(Modifier.height(14.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                                MetaItem(Icons.Filled.Groups, game.players + " pemain")
                                MetaItem(Icons.Filled.Schedule, game.duration)
                            }

                            Spacer(Modifier.height(16.dp))

                            Text(
                                text = game.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // ---------- Sesi tersedia ----------
                    item {
                        SectionLabel(text = "Sesi tersedia (${state.sessions.size})")
                    }

                    if (state.sessions.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Filled.SentimentDissatisfied,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "Belum ada sesi untuk game ini.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(state.sessions, key = { it.id }) { session ->
                            SessionCard(
                                session = session,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // ---------- Tombol aksi (fixed di bawah) ----------
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BoardGoGhostButton(
                            text = "Kembali",
                            onClick = onBack,
                            modifier = Modifier.width(120.dp)
                        )
                        BoardGoPrimaryButton(
                            text = "Host sesi",
                            onClick = { onCreateSession(game.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Tombol back mengambang di atas hero
        if (state.status == LoadStatus.SUCCESS) {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, top = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/** Metadata kecil dengan ikon. */
@Composable
private fun MetaItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Kartu sesi di layar detail. */
@Composable
private fun SessionCard(session: GameSession, modifier: Modifier = Modifier) {
    val venue = DummyData.venueById(session.venueId)
    val host = DummyData.playerById(session.hostId)
    val seatsLeft = session.maxPlayers - session.joinedPlayerIds.size

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${session.day} · ${session.time}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            InfoPill(
                text = if (seatsLeft > 0) "$seatsLeft kursi kosong" else "Penuh",
                accent = if (seatsLeft > 0) BoardGoColors.Success else BoardGoColors.Destructive
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${venue?.name ?: "-"} · ${venue?.area ?: "-"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarStack(playerIds = session.joinedPlayerIds)
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Host: ${host?.name ?: "-"}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            InfoPill(text = session.fee)
        }
    }
}
