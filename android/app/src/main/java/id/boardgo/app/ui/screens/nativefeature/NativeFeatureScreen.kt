package id.boardgo.app.ui.screens.nativefeature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.ui.components.BoardGoChip
import id.boardgo.app.ui.components.BoardGoGhostButton
import id.boardgo.app.ui.components.BoardGoPrimaryButton
import id.boardgo.app.ui.components.InfoPill
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.TopBar
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ================== LAYAR FITUR NATIVE (MOCKUP) ===========================
 * Mockup UI GPS / Kamera (QR) / Biometrik — TANPA akses hardware sungguhan.
 * Semua hasil disimulasikan oleh ViewModel (dummy).
 * ========================================================================
 */
@Composable
fun NativeFeatureScreen(
    viewModel: BoardGOViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.nativeState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(title = "Native features", onBack = onBack)

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =============== MOCKUP 1: GPS ===============
            item {
                FeatureCard(
                    icon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White) },
                    iconGradient = listOf(BoardGoColors.SkyDeep, BoardGoColors.Sky),
                    title = "Location (GPS)",
                    subtitle = "Mockup — no real GPS access"
                ) {
                    when {
                        state.isGpsLoading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Getting your location…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        state.currentLocation != null -> {
                            Text(
                                text = "📍 ${state.currentLocation}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(12.dp))
                            SectionLabel(text = "Nearby venues (≤ ${state.nearbyRadiusKm} km)")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(1, 3, 5, 10).forEach { radius ->
                                    BoardGoChip(
                                        text = "$radius km",
                                        selected = state.nearbyRadiusKm == radius,
                                        onClick = { viewModel.onNearbyRadiusChange(radius) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            val venues = viewModel.nearbyVenues(state.nearbyRadiusKm)
                            if (venues.isEmpty()) {
                                Text(
                                    text = "No venues within this radius.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                venues.forEach { venue ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = venue.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = venue.area,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        InfoPill(text = "${venue.distanceKm} KM")
                                    }
                                }
                            }
                        }

                        state.gpsErrorMessage != null -> {
                            Text(
                                text = "⚠ ${state.gpsErrorMessage}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BoardGoColors.Destructive
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    BoardGoPrimaryButton(
                        text = if (state.currentLocation == null) "Get location" else "Refresh location",
                        onClick = viewModel::mockFetchLocation,
                        enabled = !state.isGpsLoading,
                        loading = state.isGpsLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // =============== MOCKUP 2: QR SCANNER ===============
            item {
                FeatureCard(
                    icon = { Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = Color.White) },
                    iconGradient = listOf(
                        MaterialTheme.colorScheme.primary,
                        BoardGoColors.Meeple
                    ),
                    title = "QR Scanner (Camera)",
                    subtitle = "Mockup — no real camera"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF111A28))
                            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isQrScanning) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = BoardGoColors.Meeple)
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Scanning…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.QrCodeScanner,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = BoardGoColors.Meeple
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Point at the QR code on your table",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = state.qrScanResult != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Scan result: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                InfoPill(
                                    text = state.qrScanResult ?: "",
                                    accent = BoardGoColors.Success
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BoardGoPrimaryButton(
                            text = "Scan",
                            onClick = viewModel::mockScanQr,
                            enabled = !state.isQrScanning,
                            loading = state.isQrScanning,
                            modifier = Modifier.weight(1f)
                        )
                        if (state.qrScanResult != null) {
                            BoardGoGhostButton(
                                text = "Reset",
                                onClick = viewModel::clearQrResult,
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }
            }

            // =============== MOCKUP 3: BIOMETRIK ===============
            item {
                FeatureCard(
                    icon = { Icon(Icons.Filled.Fingerprint, contentDescription = null, tint = Color.White) },
                    iconGradient = listOf(BoardGoColors.Success, BoardGoColors.Sky),
                    title = "Sign in with Biometrics",
                    subtitle = "Mockup — no real fingerprint sensor"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.Fingerprint,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = when {
                                    state.isBioChecking -> MaterialTheme.colorScheme.primary
                                    state.biometricResult == "success" -> BoardGoColors.Success
                                    state.biometricResult == "failed" -> BoardGoColors.Destructive
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = when {
                                    state.isBioChecking -> "Verifying fingerprint…"
                                    state.biometricResult == "success" -> "✔ Identity verified"
                                    state.biometricResult == "failed" -> "✖ Fingerprint not recognized"
                                    else -> "Touch the sensor to sign in"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    state.biometricResult == "success" -> BoardGoColors.Success
                                    state.biometricResult == "failed" -> BoardGoColors.Destructive
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BoardGoPrimaryButton(
                            text = "Authenticate",
                            onClick = viewModel::mockAuthenticateBiometric,
                            enabled = !state.isBioChecking,
                            loading = state.isBioChecking,
                            modifier = Modifier.weight(1f)
                        )
                        if (state.biometricResult != null) {
                            BoardGoGhostButton(
                                text = "Reset",
                                onClick = viewModel::clearBiometricResult,
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Kartu fitur native (ikon gradient + judul + konten). */
@Composable
private fun FeatureCard(
    icon: @Composable () -> Unit,
    iconGradient: List<Color>,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(iconGradient)),
                contentAlignment = Alignment.Center
            ) { icon() }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        content()
    }
}
