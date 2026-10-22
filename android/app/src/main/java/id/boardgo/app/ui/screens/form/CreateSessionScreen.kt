package id.boardgo.app.ui.screens.form

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.data.DummyData
import id.boardgo.app.ui.components.BoardGoChip
import id.boardgo.app.ui.components.BoardGoGhostButton
import id.boardgo.app.ui.components.BoardGoPrimaryButton
import id.boardgo.app.ui.components.InfoPill
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.TopBar
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ======================= LAYAR 3: FORM / INPUT ============================
 * Form untuk membuat sesi bermain baru.
 *
 * VALIDASI (kriteria UTS):
 * - Field ANGKA "Jumlah pemain"  -> KeyboardType.Number + filter digit + rentang 2..12
 * - Field EMAIL "Email kontak"    -> KeyboardType.Email + regex email
 * - Field wajib (game/venue)      -> pesan error di bawah field
 * =========================================================================
 */
@Composable
fun CreateSessionScreen(
    viewModel: BoardGOViewModel,
    initialGameId: String?,
    onBack: () -> Unit,
    onSuccess: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.formState.collectAsStateWithLifecycle()

    // Argumen dari navigasi: game yang dipilih di layar Detail
    LaunchedEffect(initialGameId) {
        if (!initialGameId.isNullOrBlank()) {
            viewModel.prefillGame(initialGameId)
        }
    }

    // Navigasi ke layar Detail GAME yang sesi-nya baru dibuat
    // (createdSessionId hanya penanda sukses; tujuan navigasi = gameId terpilih)
    LaunchedEffect(state.isSubmitSuccess, state.createdSessionId) {
        val selectedGame = state.gameId
        if (state.isSubmitSuccess && selectedGame.isNotBlank()) {
            onSuccess(selectedGame)
            viewModel.resetForm()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(title = "Host a game", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // ---------------- Step 1: pilih game ----------------
            SectionLabel(text = "1 · Pilih game")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(DummyData.games, key = { it.id }) { game ->
                    BoardGoChip(
                        text = game.name,
                        selected = state.gameId == game.id,
                        onClick = { viewModel.onFormGameChange(game.id) }
                    )
                }
            }
            FieldError(message = state.gameIdError)

            // ---------------- Step 2: hari & jam ----------------
            SectionLabel(text = "2 · Hari & jam")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DummyData.dayOptions.take(3).forEach { day ->
                    BoardGoChip(
                        text = day,
                        selected = state.day == day,
                        onClick = { viewModel.onFormDayChange(day) }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DummyData.dayOptions.drop(3).forEach { day ->
                    BoardGoChip(
                        text = day,
                        selected = state.day == day,
                        onClick = { viewModel.onFormDayChange(day) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DummyData.timeOptions.forEach { time ->
                    BoardGoChip(
                        text = time,
                        selected = state.time == time,
                        onClick = { viewModel.onFormTimeChange(time) }
                    )
                }
            }

            // ---------------- Step 3: pilih venue ----------------
            SectionLabel(text = "3 · Pilih venue")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DummyData.venues.forEach { venue ->
                    VenueOption(
                        name = venue.name,
                        area = venue.area,
                        distanceKm = venue.distanceKm,
                        selected = state.venueId == venue.id,
                        onClick = { viewModel.onFormVenueChange(venue.id) }
                    )
                }
            }
            FieldError(message = state.venueIdError)

            // ---------------- Step 4: jumlah pemain (ANGKA) ----------------
            SectionLabel(text = "4 · Jumlah pemain")
            OutlinedTextField(
                value = state.maxPlayers,
                onValueChange = viewModel::onFormMaxPlayersChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Maksimal pemain") },
                placeholder = { Text("Contoh: 4") },
                singleLine = true,
                // >>> KEYBOARD NUMBER + validasi hanya angka <<<
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = state.maxPlayersError != null,
                supportingText = {
                    Text(
                        text = state.maxPlayersError ?: "Hanya angka, minimal 2 maksimal 12 pemain",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.maxPlayersError != null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // ---------------- Step 5: email kontak (EMAIL) ----------------
            SectionLabel(text = "5 · Email kontak")
            OutlinedTextField(
                value = state.contactEmail,
                onValueChange = viewModel::onFormEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email untuk hubungi pemain") },
                placeholder = { Text("contoh: dimas@boardgo.id") },
                singleLine = true,
                // >>> KEYBOARD EMAIL + validasi regex email <<<
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = state.contactEmailError != null,
                supportingText = {
                    Text(
                        text = state.contactEmailError ?: "Opsional — dipakai pemain untuk menghubungi host",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.contactEmailError != null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // ---------------- Step 6: biaya + catatan ----------------
            SectionLabel(text = "6 · Biaya")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DummyData.feeOptions.forEach { fee ->
                    BoardGoChip(
                        text = fee,
                        selected = state.fee == fee,
                        onClick = { viewModel.onFormFeeChange(fee) }
                    )
                }
            }

            SectionLabel(text = "7 · Catatan (opsional)")
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::onFormNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("House rules, expansion, apa yang perlu dibawa…") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Text(
                text = "${state.note.length}/120 karakter",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )

            // ---------------- Ringkasan ----------------
            SectionLabel(text = "Ringkasan")
            SummaryCard(
                gameName = DummyData.gameById(state.gameId)?.name ?: "—",
                venueName = DummyData.venueById(state.venueId)?.name ?: "—",
                day = state.day,
                time = state.time,
                maxPlayers = state.maxPlayers,
                fee = state.fee
            )

            // ---------------- Aksi ----------------
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BoardGoGhostButton(
                    text = "Batal",
                    onClick = onBack,
                    modifier = Modifier.width(110.dp)
                )
                BoardGoPrimaryButton(
                    text = "Publish meja",
                    onClick = viewModel::submitSessionForm,
                    modifier = Modifier.weight(1f),
                    loading = state.isSubmitting
                )
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

/** Pesan error di bawah field. */
@Composable
private fun FieldError(message: String?) {
    if (message != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                Icons.Filled.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/** Opsi venue yang bisa dipilih. */
@Composable
private fun VenueOption(
    name: String,
    area: String,
    distanceKm: Double,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = area,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        InfoPill(text = "$distanceKm km")
        if (selected) {
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = "Dipilih",
                modifier = Modifier.size(20.dp),
                tint = BoardGoColors.Success
            )
        }
    }
}

/** Kartu ringkasan data sesi. */
@Composable
private fun SummaryCard(
    gameName: String,
    venueName: String,
    day: String,
    time: String,
    maxPlayers: String,
    fee: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text(
            text = gameName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$day · $time · $venueName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoPill(text = "$maxPlayers pemain")
            InfoPill(text = fee)
        }
    }
}
