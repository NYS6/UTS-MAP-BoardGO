package id.boardgo.app.ui.screens.notifications

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.ui.components.SectionLabel
import id.boardgo.app.ui.components.TopBar
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.AppNotification
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ===================== LAYAR NOTIFICATIONS =================================
 * Meniru halaman notifikasi web app:
 *  - Daftar notifikasi (judul + isi + waktu), titik unread oranye
 *  - Aksi "mark all read" di top bar
 *  - Ketuk notifikasi -> tandai sudah dibaca + buka target
 *    (chat sesi / detail game) bila ada tautannya
 *  - Notifikasi baru ter-push saat Join sesi & Publish tabel
 * =========================================================================
 */
@Composable
fun NotificationsScreen(
    viewModel: BoardGOViewModel,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onOpenGame: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val notifState by viewModel.notifState.collectAsStateWithLifecycle()
    val notifications = notifState.notifications

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(
            title = "Notifications",
            onBack = onBack,
            trailing = {
                if (notifState.unreadCount > 0) {
                    Text(
                        text = "mark all read",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { viewModel.markAllNotifsRead() }
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }
            }
        )

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "You're all caught up!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 130.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    SectionLabel(
                        text = "Recent activity (${notifState.unreadCount} unread)"
                    )
                }

                items(notifications, key = { it.id }) { notif ->
                    NotificationCard(
                        notif = notif,
                        onClick = {
                            viewModel.markNotifRead(notif.id)
                            when {
                                notif.chatSessionId != null -> onOpenChat(notif.chatSessionId)
                                notif.gameId != null -> onOpenGame(notif.gameId)
                            }
                        }
                    )
                }
            }
        }
    }
}

/** Kartu notifikasi — unread diberi titik oranye + ring lembut. */
@Composable
private fun NotificationCard(
    notif: AppNotification,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (notif.read) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surface.copy(alpha = 1f)
            )
            .border(
                width = if (notif.read) 1.dp else 1.5.dp,
                color = if (notif.read) MaterialTheme.colorScheme.outline
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Titik unread
        Box(
            modifier = Modifier
                .padding(top = 6.dp, end = 0.dp)
                .size(if (notif.read) 6.dp else 9.dp)
                .clip(CircleShape)
                .background(
                    if (notif.read) MaterialTheme.colorScheme.outline
                    else BoardGoColors.Meeple
                )
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notif.title,
                style = MaterialTheme.typography.titleSmall,
                color = if (notif.read) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = notif.body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = notif.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
