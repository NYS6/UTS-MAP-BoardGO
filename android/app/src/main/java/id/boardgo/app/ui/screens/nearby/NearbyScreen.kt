package id.boardgo.app.ui.screens.nearby

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.data.DummyData
import id.boardgo.app.data.model.Venue
import id.boardgo.app.ui.components.BoardGoChip
import id.boardgo.app.ui.components.BoardGoGhostButton
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.SessionMiniRow
import id.boardgo.app.ui.components.TopBar
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.theme.BoardGoDark
import id.boardgo.app.ui.viewmodel.BoardGOViewModel
import id.boardgo.app.ui.viewmodel.MAP_KM
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NearbyScreen(
    viewModel: BoardGOViewModel,
    onBack: () -> Unit,
    onToList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.nearbyState.collectAsStateWithLifecycle()
    val radius = state.radiusKm

    val sessions = viewModel.nearbySessions(radius)
    val liveVenueIds = sessions.map { it.venueId }.toSet()
    val countsByVenue = sessions.groupingBy { it.venueId }.eachCount()

    LaunchedEffect(state.hint) {
        if (state.hint != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.onNearbyHintShown()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 130.dp)
    ) {
        item {
            TopBar(
                title = "Nearby tables",
                onBack = onBack,
                trailing = {
                    IconButton(
                        onClick = onToList,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "List view",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
        }

        item {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            ) {
                val mapW = constraints.maxWidth.toFloat()
                val mapH = constraints.maxHeight.toFloat()
                val dark = MaterialTheme.colorScheme.background == BoardGoDark.Background

                val mapBg = if (dark) Color(0xFF16233A) else MaterialTheme.colorScheme.secondary
                val streetColor = if (dark) Color(0xFF26364F) else MaterialTheme.colorScheme.surface

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val bg = mapBg
                    val street = streetColor
                    val sky = BoardGoColors.Sky
                    val green = BoardGoColors.Success
                    val meeple = BoardGoColors.Meeple

                    drawRect(color = bg)

                    val river = androidx.compose.ui.graphics.Path().apply {
                        val w = size.width
                        val h = size.height
                        moveTo(0f, h * 0.78f)

                        cubicTo(w * 0.20f, h * 0.89f, w * 0.40f, h * 0.91f, w * 0.55f, h * 0.80f)

                        cubicTo(w * 0.72f, h * 0.71f, w * 0.86f, h * 0.69f, w, h * 0.76f)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(river, color = sky.copy(alpha = 0.35f))

                    drawRoundRect(
                        color = green.copy(alpha = 0.22f),
                        topLeft = Offset(size.width * 0.08f, size.height * 0.10f),
                        size = Size(size.width * 0.18f, size.height * 0.14f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f)
                    )
                    drawRoundRect(
                        color = green.copy(alpha = 0.22f),
                        topLeft = Offset(size.width * 0.68f, size.height * 0.30f),
                        size = Size(size.width * 0.20f, size.height * 0.12f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f)
                    )
                    drawCircle(
                        color = green.copy(alpha = 0.18f),
                        radius = size.width * 0.07f,
                        center = Offset(size.width * 0.30f, size.height * 0.58f)
                    )

                    val mainW = size.width * 0.03f
                    drawLine(
                        street, Offset(0f, size.height * 0.5f),
                        Offset(size.width, size.height * 0.5f), strokeWidth = mainW
                    )
                    drawLine(
                        street, Offset(size.width * 0.5f, 0f),
                        Offset(size.width * 0.5f, size.height), strokeWidth = mainW
                    )

                    drawLine(
                        street, Offset(0f, size.height * 0.15f),
                        Offset(size.width, size.height * 0.65f), strokeWidth = size.width * 0.022f
                    )
                    drawLine(
                        street, Offset(size.width * 0.1f, size.height),
                        Offset(size.width * 0.9f, 0f), strokeWidth = size.width * 0.022f
                    )

                    listOf(0.20, 0.35, 0.65, 0.80).forEach { v ->
                        drawLine(
                            street, Offset(0f, size.height * v.toFloat()),
                            Offset(size.width, size.height * v.toFloat()),
                            strokeWidth = size.width * 0.01f
                        )
                    }
                    listOf(0.15, 0.30, 0.70, 0.85).forEach { v ->
                        drawLine(
                            street, Offset(size.width * v.toFloat(), 0f),
                            Offset(size.width * v.toFloat(), size.height),
                            strokeWidth = size.width * 0.01f
                        )
                    }

                    drawLine(
                        meeple.copy(alpha = 0.6f), Offset(0f, size.height * 0.5f),
                        Offset(size.width, size.height * 0.5f), strokeWidth = size.width * 0.006f
                    )
                    drawLine(
                        meeple.copy(alpha = 0.6f), Offset(size.width * 0.5f, 0f),
                        Offset(size.width * 0.5f, size.height), strokeWidth = size.width * 0.006f
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = state.zoom
                            scaleY = state.zoom
                        }
                ) {

                    val radiusFraction = (state.radiusPct * 2 / 100f).coerceAtMost(1f)
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(fraction = radiusFraction)
                            .aspectRatio(1f)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                CircleShape
                            )
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                    )

                    DummyData.venues.forEach { venue ->
                        VenuePin(
                            venue = venue,
                            count = countsByVenue[venue.id] ?: 0,
                            inRadius = venue.id in liveVenueIds,
                            picked = state.pickedVenueId == venue.id,
                            mapW = mapW,
                            mapH = mapH,
                            onClick = {
                                if (venue.id in liveVenueIds) viewModel.onVenuePicked(venue.id)
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(26.dp)
                            .graphicsLayer { alpha = 0.25f }
                            .clip(CircleShape)
                            .background(BoardGoColors.Sky)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(BoardGoColors.Sky)
                            .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                ) {
                    IconButton(
                        onClick = { viewModel.onNearbyZoom(0.5f) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add, contentDescription = "Zoom in",
                            tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    IconButton(
                        onClick = { viewModel.onNearbyZoom(-0.5f) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Remove, contentDescription = "Zoom out",
                            tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = viewModel::onRecenter,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        Icons.Filled.MyLocation, contentDescription = "Recenter",
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "WITHIN ${radius.toInt()} KM · ${sessions.size} TABLES",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (state.hint != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(BoardGoColors.Success)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "✓ ${state.hint}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item {
            SectionLabel(
                text = "Distance from you",
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1.0, 2.0, 3.0, 5.0, 10.0).forEach { r ->
                    BoardGoChip(
                        text = "${r.toInt()} km",
                        selected = radius == r,
                        onClick = { viewModel.onNearbyRadiusChange(r) }
                    )
                }
            }
        }

        val pickedId = state.pickedVenueId
        if (pickedId != null) {
            val venue = DummyData.venueById(pickedId)
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = (venue?.name ?: "-").uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "${venue?.area ?: "-"} · ${venue?.distanceKm ?: "-"} KM AWAY",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "show all",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { viewModel.onVenuePicked(pickedId) }
                                .padding(4.dp)
                        )
                    }
                }
            }
            items(viewModel.venueSessions(pickedId), key = { it.id }) { s ->
                SessionMiniRow(
                    session = s,
                    onClick = { },
                    showKm = true,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }
        } else {
            item {
                SectionLabel(
                    text = "Tables near you (${sessions.size})",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            if (sessions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nothing this close",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "No tables within ${radius.toInt()} km. Try a wider distance.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(14.dp))
                        BoardGoGhostButton(
                            text = "Show 10 km",
                            onClick = { viewModel.onNearbyRadiusChange(10.0) },
                            modifier = Modifier.width(160.dp)
                        )
                    }
                }
            } else {
                items(sessions, key = { it.id }) { s ->
                    val venue = DummyData.venueById(s.venueId)
                    Box {
                        SessionMiniRow(
                            session = s,
                            onClick = { viewModel.onVenuePicked(s.venueId) },
                            showKm = true,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 22.dp, end = 68.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${venue?.distanceKm ?: "-"} km",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.BoxScope.VenuePin(
    venue: Venue,
    count: Int,
    inRadius: Boolean,
    picked: Boolean,
    mapW: Float,
    mapH: Float,
    onClick: () -> Unit
) {
    val r = (venue.distanceKm / MAP_KM) * 100.0
    val a = Math.toRadians(venue.bearing)
    val xPct = 50 + r * sin(a)
    val yPct = 50 - r * cos(a)

    val bgColor = when {
        picked -> MaterialTheme.colorScheme.onSurface
        inRadius -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .offset {
                androidx.compose.ui.unit.IntOffset(
                    x = (xPct / 100.0 * mapW).toInt() - 14,
                    y = (yPct / 100.0 * mapH).toInt() - 28
                )
            }
            .size(28.dp)
            .graphicsLayer { rotationZ = 45f }
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 2.dp, bottomStart = 10.dp))
            .background(bgColor)
            .then(
                if (inRadius) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (inRadius) count.toString() else "·",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.graphicsLayer { rotationZ = -45f }
        )
    }
}

