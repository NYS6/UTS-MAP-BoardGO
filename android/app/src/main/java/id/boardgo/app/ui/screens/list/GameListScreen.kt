package id.boardgo.app.ui.screens.list

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.data.model.Game
import id.boardgo.app.ui.components.BoardGoChip
import id.boardgo.app.ui.components.ErrorScreen
import id.boardgo.app.ui.components.InfoPill
import id.boardgo.app.ui.components.LoadingScreen
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.SkyBackdrop
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel
import id.boardgo.app.ui.viewmodel.LoadStatus

/**
 * ===================== LAYAR 1: LIST GAME (LazyColumn) =====================
 * Menampilkan daftar seluruh game board + pencarian + filter kategori.
 * Klik item -> navigasi ke layar Detail dengan gameId sebagai argumen.
 * =========================================================================
 */
@Composable
fun GameListScreen(
    viewModel: BoardGOViewModel,
    onGameClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (state.status) {
            LoadStatus.LOADING -> LoadingScreen(message = "Memuat katalog game…")
            LoadStatus.ERROR -> ErrorScreen(
                message = state.errorMessage ?: "Gagal memuat data",
                onRetry = viewModel::loadGames
            )
            LoadStatus.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // ---------- Header hero ----------
                    item {
                        Box {
                            SkyBackdrop(tall = true)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 28.dp, bottom = 16.dp)
                            ) {
                                Text(
                                    text = "BoardGO",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                                Text(
                                    text = "Temukan meja untuk bermain hari ini",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // ---------- Search field ----------
                    item {
                        OutlinedTextField(
                            value = state.searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            placeholder = {
                                Text("Cari game…", style = MaterialTheme.typography.bodyMedium)
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }

                    // ---------- Filter kategori (horizontal) ----------
                    item {
                    SectionLabel(
                        text = "Kategori",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categories = listOf("All") + state.allGames
                                .map { it.category.label }
                                .distinct()
                            items(categories) { category ->
                                BoardGoChip(
                                    text = category,
                                    selected = state.selectedCategory == category,
                                    onClick = { viewModel.onCategorySelected(category) }
                                )
                            }
                        }
                    }

                    // ---------- Daftar game (LAZY COLUMN) ----------
                    item {
                        SectionLabel(
                            text = "${state.filteredGames.size} game ditemukan",
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    if (state.filteredGames.isEmpty()) {
                        item {
                            EmptyState(
                                message = "Tidak ada game yang cocok dengan pencarianmu.",
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 48.dp)
                            )
                        }
                    } else {
                        items(
                            items = state.filteredGames,
                            key = { it.id }
                        ) { game ->
                            GameListItem(
                                game = game,
                                onClick = { onGameClick(game.id) },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Satu baris item game di dalam LazyColumn. */
@Composable
fun GameListItem(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Cover" game digambar dari gradient + inisial (tidak memakai gambar asli)
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            BoardGoColors.SkyDeep,
                            MaterialTheme.colorScheme.primary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = game.name.take(2).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = androidx.compose.ui.graphics.Color.White
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = game.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoPill(text = game.players + " pemain")
                InfoPill(text = game.duration)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = game.category.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Tampilan saat hasil filter kosong. */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.SentimentDissatisfied,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
