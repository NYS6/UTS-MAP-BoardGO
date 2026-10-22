package id.boardgo.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.boardgo.app.R
import id.boardgo.app.data.DummyData
import id.boardgo.app.ui.theme.BoardGoColors

object GameCovers {
    private val covers = mapOf(
        "catan" to R.drawable.game_catan,
        "wingspan" to R.drawable.game_wingspan,
        "azul" to R.drawable.game_azul,
        "splendor" to R.drawable.game_splendor,
        "ticket" to R.drawable.game_ticket,
        "mars" to R.drawable.game_mars,
        "mahjong" to R.drawable.game_mahjong,
        "chess" to R.drawable.game_chess,
        "checkers" to R.drawable.game_checkers,
        "monopoly" to R.drawable.game_monopoly,
        "scrabble" to R.drawable.game_scrabble,
        "werewolf" to R.drawable.game_werewolf
    )

    fun coverOf(gameId: String): Int? = covers[gameId]
}

@Composable
fun GameCover(
    gameId: String,
    name: String,
    modifier: Modifier = Modifier
) {
    val coverRes = GameCovers.coverOf(gameId)
    Box(
        modifier = modifier.clip(RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (coverRes != null) {
            Image(
                painter = painterResource(coverRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(BoardGoColors.SkyDeep, MaterialTheme.colorScheme.primary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SkyBackdrop(modifier: Modifier = Modifier, tall: Boolean = false) {
    Box(modifier = modifier.fillMaxWidth().height(if (tall) 300.dp else 190.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(BoardGoColors.SkyDeep, BoardGoColors.Sky, BoardGoColors.Background)
                    )
                )
        )

        Box(
            modifier = Modifier
                .offset(x = (-40).dp, y = 40.dp)
                .size(150.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.14f))
        )
        Box(
            modifier = Modifier
                .offset(x = 240.dp, y = 10.dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(BoardGoColors.Primary.copy(alpha = 0.28f))
        )
    }
}

@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    light: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    val fg = if (light) Color.White else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (light) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        if (light) Color.White.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = fg
                )
            }
        } else {
            Spacer(Modifier.width(38.dp))
        }

        if (title.isNotBlank()) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(Modifier.weight(1f))
        }

        trailing?.invoke()
    }
}

@Composable
fun GlassCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: Boolean = false
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f))
                .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape)
        ) {
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        if (badge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(BoardGoColors.Meeple)
                    .border(1.5.dp, BoardGoColors.SkyDeep, CircleShape)
            )
        }
    }
}

@Composable
fun BoardGoChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 7.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge, color = fg)
    }
}

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.outline)
        )
        trailing?.invoke()
    }
}

@Composable
fun InfoPill(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = accent
        )
    }
}

@Composable
fun SeatPill(
    seatsLeft: Int,
    modifier: Modifier = Modifier
) {
    val (label, fg, bg) = when {
        seatsLeft <= 0 -> Triple("FULL", BoardGoColors.Destructive, BoardGoColors.Destructive.copy(alpha = 0.10f))
        seatsLeft == 1 -> Triple("FILLING FAST · 1 SEAT", BoardGoColors.Primary, BoardGoColors.Primary.copy(alpha = 0.10f))
        else -> Triple("$seatsLeft SEATS OPEN", BoardGoColors.Success, BoardGoColors.Success.copy(alpha = 0.10f))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = fg)
    }
}

@Composable
fun PlayerAvatar(
    playerId: String,
    modifier: Modifier = Modifier,
    size: Int = 40
) {
    val player = DummyData.playerById(playerId)
    val color = if (player != null) {
        Color(DummyData.playerColors[player.colorIndex % DummyData.playerColors.size])
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val initial = player?.name?.firstOrNull()?.uppercase() ?: "?"

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initial, style = MaterialTheme.typography.titleMedium, color = Color.White)
    }
}

@Composable
fun AvatarStack(
    playerIds: List<String>,
    modifier: Modifier = Modifier,
    size: Int = 28
) {
    Row(modifier = modifier) {
        playerIds.take(4).forEachIndexed { index, id ->
            Box(modifier = Modifier.offset(x = if (index == 0) 0.dp else (-8).dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(1.5.dp)
                ) {
                    PlayerAvatar(playerId = id, size = size)
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(message: String = "Loading…", modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "OOPS!",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            BoardGoPrimaryButton(
                text = "Try again",
                onClick = onRetry,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
fun BoardGoPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun BoardGoGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun SuccessBanner(
    visible: Boolean,
    message: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = visible, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(BoardGoColors.Success)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun SessionMiniRow(
    session: id.boardgo.app.data.model.GameSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showKm: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    val game = DummyData.gameById(session.gameId)
    val venue = DummyData.venueById(session.venueId)
    val seatsLeft = session.maxPlayers - session.joinedPlayerIds.size

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameCover(
            gameId = session.gameId,
            name = game?.name ?: "?",
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = (game?.name ?: "-").uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = buildString {
                    append(session.day.uppercase()); append(" · "); append(session.time)
                    append(" · "); append(venue?.name ?: "-")
                    if (showKm && venue != null) append(" · ${venue.distanceKm} KM")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarStack(playerIds = session.joinedPlayerIds, size = 22)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${session.joinedPlayerIds.size}/${session.maxPlayers} PLAYERS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        if (trailing != null) {
            trailing()
        } else {
            SeatPill(seatsLeft = seatsLeft)
        }
    }
}

