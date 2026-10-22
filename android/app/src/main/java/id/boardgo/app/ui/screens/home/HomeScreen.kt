package id.boardgo.app.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import id.boardgo.app.data.model.GameSession
import id.boardgo.app.ui.components.AvatarStack
import id.boardgo.app.ui.components.BoardGoChip
import id.boardgo.app.ui.components.BoardGoPrimaryButton
import id.boardgo.app.ui.components.ErrorScreen
import id.boardgo.app.ui.components.GameCover
import id.boardgo.app.ui.components.GlassCircleButton
import id.boardgo.app.ui.components.LoadingScreen
import id.boardgo.app.ui.components.SeatPill
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.SessionMiniRow
import id.boardgo.app.ui.components.SkyBackdrop
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel
import id.boardgo.app.ui.viewmodel.LoadStatus

/**
 * ========================= LAYAR HOME =====================================
 * Meniru /home web app: logo + bell, baris "◉ NEARBY" (-> layar jarak GPS),
 * slogan "FIND A TABLE IN 30 SEC", chip filter game, kartu Featured
 * dengan tombol Join (mockup), dan daftar "Up next".
 * =========================================================================
 */
@Composable
fun HomeScreen(
    viewModel: BoardGOViewModel,
    onSeeAll: () -> Unit,
    onSessionClick: (String) -> Unit,
    onNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.homeState.collectAsStateWithLifecycle()
    val notifState by viewModel.notifState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (state.status) {
            LoadStatus.LOADING -> LoadingScreen(message = "Loading tables…")
            LoadStatus.ERROR -> ErrorScreen(
                message = state.errorMessage ?: "Failed to load data",
                onRetry = viewModel::loadHome
            )
            LoadStatus.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 130.dp)
                ) {
                    // ---------- Hero + header ----------
                    item {
                        Box {
                            SkyBackdrop(tall = true)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(horizontal = 20.dp)
                            ) {
                                // Header: logo B + BOARDGO + bell
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "B",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = "BOARDGO",
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Color.White
                                    )
                                    Spacer(Modifier.weight(1f))
                                    GlassCircleButton(
                                        icon = Icons.Filled.Notifications,
                                        contentDescription = "Notifications",
                                        onClick = onNotifications,
                                        badge = notifState.unreadCount > 0
                                    )
                                }

                                Spacer(Modifier.height(14.dp))

                                // Baris lokasi (mockup locate — GPS terintegrasi di tab FIND)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.mockLocate() }
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (state.locating) "◉ LOCATING…"
                                        else "◉ NEARBY",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = BoardGoColors.Meeple
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "📍 ${state.locationArea} · ${DummyData.venues.size} VENUES",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = "${state.openSessions.size} SESSIONS",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                // Slogan besar
                                Text(
                                    text = "FIND A TABLE\nIN 30 SEC",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = Color.White
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Live sessions around you — grab a meeple, roll in, play tonight.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // ---------- Chip filter game (game utama di depan) ----------
                    item {
                        Spacer(Modifier.height(14.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val chips =
                                listOf("All") + DummyData.featuredGameIds + "catan" + "wingspan"
                            items(chips) { chipId ->
                                val label =
                                    if (chipId == "All") "All" else DummyData.gameById(chipId)?.name ?: chipId
                                BoardGoChip(
                                    text = label,
                                    selected = state.selectedChip == chipId,
                                    onClick = { viewModel.onHomeChipChange(chipId) }
                                )
                            }
                        }
                    }

                    // ---------- Kartu featured ----------
                    state.featured?.let { featured ->
                        item {
                            FeaturedCard(
                                session = featured,
                                onJoin = { viewModel.joinSession(featured.id) },
                                onClick = { onSessionClick(featured.gameId) },
                                modifier = Modifier.padding(
                                    start = 20.dp, end = 20.dp, top = 16.dp
                                )
                            )
                        }
                    }

                    // ---------- Up next ----------
                    item {
                        SectionLabel(
                            text = "Up next",
                            trailing = {
                                Text(
                                    text = "see all →",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable(onClick = onSeeAll)
                                        .padding(vertical = 2.dp)
                                )
                            },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    items(state.rest.take(4), key = { it.id }) { session ->
                        SessionMiniRow(
                            session = session,
                            onClick = { onSessionClick(session.gameId) },
                            showKm = true,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )
                    }

                    if (state.openSessions.isEmpty()) {
                        item {
                            Text(
                                text = "No open tables for this filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Kartu Featured (gaya web app: cover + info + tombol Join). */
@Composable
private fun FeaturedCard(
    session: GameSession,
    onJoin: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val game = DummyData.gameById(session.gameId)
    val venue = DummyData.venueById(session.venueId)
    val seatsLeft = session.maxPlayers - session.joinedPlayerIds.size
    val joined = DummyData.CURRENT_USER_ID in session.joinedPlayerIds

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
    ) {
        // Cover + overlay ringan
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clickable(onClick = onClick)
        ) {
            GameCover(
                gameId = session.gameId,
                name = game?.name ?: "?",
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                SeatPill(seatsLeft = seatsLeft)
            }
        }

        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = (game?.name ?: "-").uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${session.day.uppercase()} ${session.time} · ${venue?.name ?: "-"} · ${venue?.distanceKm ?: "-"} KM",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AvatarStack(playerIds = session.joinedPlayerIds, size = 26)
            }

            Spacer(Modifier.height(12.dp))

            if (joined) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(BoardGoColors.Success.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ You're in — ${session.joinedPlayerIds.size}/${session.maxPlayers} players",
                        style = MaterialTheme.typography.titleSmall,
                        color = BoardGoColors.Success
                    )
                }
            } else {
                BoardGoPrimaryButton(
                    text = if (seatsLeft <= 0) "Table full" else "Join the table",
                    onClick = onJoin,
                    enabled = seatsLeft > 0,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
