package id.boardgo.app.ui.screens.detail

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SignalCellularAlt
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.data.DummyData
import id.boardgo.app.data.model.GameSession
import id.boardgo.app.ui.components.AvatarStack
import id.boardgo.app.ui.components.BoardGoGhostButton
import id.boardgo.app.ui.components.BoardGoPrimaryButton
import id.boardgo.app.ui.components.ErrorScreen
import id.boardgo.app.ui.components.GameCover
import id.boardgo.app.ui.components.InfoPill
import id.boardgo.app.ui.components.LoadingScreen
import id.boardgo.app.ui.components.SeatPill
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel
import id.boardgo.app.ui.viewmodel.LoadStatus

@Composable
fun GameDetailScreen(
    gameId: String,
    viewModel: BoardGOViewModel,
    onBack: () -> Unit,
    onCreateSession: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()

    LaunchedEffect(gameId) {
        viewModel.loadGameDetail(gameId)
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (state.status) {
            LoadStatus.LOADING -> LoadingScreen(message = "Loading game…")
            LoadStatus.ERROR -> ErrorScreen(
                message = state.errorMessage ?: "Failed to load game",
                onRetry = { viewModel.loadGameDetail(gameId) }
            )
            LoadStatus.SUCCESS -> {
                val game = state.game ?: return@Box

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 200.dp)
                ) {

                    item {
                        Box {
                            GameCover(
                                gameId = game.id,
                                name = game.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .align(Alignment.TopCenter)
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)
                                        )
                                    )
                            )
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = game.name.uppercase(),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = viewModel::toggleFavorite) {
                                    Icon(
                                        imageVector = if (state.isFavorite) Icons.Filled.Favorite
                                        else Icons.Filled.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (state.isFavorite) BoardGoColors.Primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoPill(
                                    text = game.category.label.uppercase(),
                                    accent = MaterialTheme.colorScheme.primary
                                )
                                InfoPill(text = game.difficulty.label.uppercase())
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatBlock(
                                icon = Icons.Filled.Groups,
                                value = game.players,
                                label = "PLAYERS",
                                modifier = Modifier.weight(1f)
                            )
                            StatBlock(
                                icon = Icons.Filled.Schedule,
                                value = game.duration.replace(" min", "").uppercase(),
                                label = "MINUTES",
                                modifier = Modifier.weight(1f)
                            )
                            StatBlock(
                                icon = Icons.Filled.SignalCellularAlt,
                                value = game.difficulty.label,
                                label = "SKILL",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                            Text(
                                text = game.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        SectionLabel(
                            text = "Tables available (${state.sessions.size})",
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    if (state.sessions.isEmpty()) {
                        item {
                            Text(
                                text = "No open tables for this game yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    horizontal = 20.dp,
                                    vertical = 32.dp
                                )
                            )
                        }
                    } else {
                        items(state.sessions, key = { it.id }) { session ->
                            SessionCard(
                                session = session,
                                onJoin = { viewModel.joinSession(session.id) },
                                onChat = { onOpenChat(session.id) },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 104.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BoardGoGhostButton(
                            text = "Back",
                            onClick = onBack,
                            modifier = Modifier.width(110.dp)
                        )
                        BoardGoPrimaryButton(
                            text = "Host a session",
                            onClick = { onCreateSession(game.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 20.dp, top = 8.dp)
                        .size(38.dp)
                        .clip(CircleShapeRounded)
                        .background(Color.White.copy(alpha = 0.92f))
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
}

private val CircleShapeRounded = RoundedCornerShape(50)

@Composable
private fun StatBlock(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SessionCard(
    session: GameSession,
    onJoin: () -> Unit,
    onChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val venue = DummyData.venueById(session.venueId)
    val host = DummyData.playerById(session.hostId)
    val seatsLeft = session.maxPlayers - session.joinedPlayerIds.size
    val joined = id.boardgo.app.data.DummyData.CURRENT_USER_ID in session.joinedPlayerIds

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${session.day.uppercase()} · ${session.time}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            SeatPill(seatsLeft = seatsLeft)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${venue?.name ?: "-"} · ${venue?.area ?: "-"} · ${venue?.distanceKm ?: "-"} KM AWAY",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (session.note.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "“${session.note}”",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarStack(playerIds = session.joinedPlayerIds)
            Spacer(Modifier.width(10.dp))
            Text(
                text = "HOSTED BY ${host?.name?.uppercase() ?: "-"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            InfoPill(text = session.fee)
        }

        if (joined) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BoardGoColors.Success.copy(alpha = 0.12f))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓ YOU'RE IN",
                    style = MaterialTheme.typography.labelMedium,
                    color = BoardGoColors.Success
                )
            }
        } else if (seatsLeft > 0) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onJoin)
                    .padding(horizontal = 18.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "JOIN",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "open chat →",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onChat)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

