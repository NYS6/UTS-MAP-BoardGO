package id.boardgo.app.ui.screens.mygames

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.data.DummyData
import id.boardgo.app.data.model.GameSession
import id.boardgo.app.ui.components.AvatarStack
import id.boardgo.app.ui.components.GameCover
import id.boardgo.app.ui.components.SeatPill
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.TopBar
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ====================== LAYAR MY GAMES ====================================
 * Meniru tab MINE web app: tab Upcoming / Past + daftar sesi (LazyColumn)
 * dengan cover foto, nama game Anton, venue, seat pill, dan link chat.
 * =========================================================================
 */
@Composable
fun MyGamesScreen(
    viewModel: BoardGOViewModel,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.myGamesState.collectAsStateWithLifecycle()
    val sessions = state.current

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 130.dp)
        ) {
            item {
                TopBar(title = "My games")
            }

            // ---------- Tab Upcoming / Past (state remember + ViewModel) ----------
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabPill(
                        text = "Upcoming",
                        selected = !state.showPast,
                        onClick = { viewModel.onMyGamesTabChange(false) }
                    )
                    TabPill(
                        text = "Past",
                        selected = state.showPast,
                        onClick = { viewModel.onMyGamesTabChange(true) }
                    )
                }
            }

            item {
                SectionLabel(
                    text = "${sessions.size} sessions",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (sessions.isEmpty()) {
                item {
                    Text(
                        text = if (state.showPast) "No past sessions yet."
                        else "No upcoming tables — join or host one!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                    )
                }
            } else {
                items(sessions, key = { it.id }) { session ->
                    SessionRow(
                        session = session,
                        past = state.showPast,
                        onClick = { onGameClick(session.gameId) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

/** Pill tab. */
@Composable
private fun TabPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .border(
                1.dp,
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Baris sesi (cover + info + seat pill + link chat). */
@Composable
private fun SessionRow(
    session: GameSession,
    past: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val game = DummyData.gameById(session.gameId)
    val venue = DummyData.venueById(session.venueId)
    val seatsLeft = session.maxPlayers - session.joinedPlayerIds.size

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameCover(
            gameId = session.gameId,
            name = game?.name ?: "?",
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.width(14.dp))
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
            Spacer(Modifier.height(8.dp))
            if (past) {
                Text(
                    text = "Done ✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = BoardGoColors.Success
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarStack(playerIds = session.joinedPlayerIds, size = 22)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "open chat →",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        if (!past) {
            SeatPill(seatsLeft = seatsLeft)
        }
    }
}
