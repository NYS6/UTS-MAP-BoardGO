package id.boardgo.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ViewAgenda
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.boardgo.app.ui.screens.detail.GameDetailScreen
import id.boardgo.app.ui.screens.form.CreateSessionScreen
import id.boardgo.app.ui.screens.list.GameListScreen
import id.boardgo.app.ui.screens.nativefeature.NativeFeatureScreen
import id.boardgo.app.ui.screens.profile.ProfileScreen
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ============ NAVIGASI (NavHost + NavController) ===========================
 * Route yang tersedia:
 *  - "list"        : Layar List (LazyColumn)
 *  - "detail/{gameId}" : Layar Detail  <-- ARGUMEN gameId dari List
 *  - "create?gameId={gameId}" : Layar Form (opsional terisi dari Detail)
 *  - "native"      : Layar Fitur Native (mockup)
 *  - "profile"     : Layar Profil
 * =========================================================================
 */
object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{gameId}"
    const val CREATE = "create?gameId={gameId}"
    const val NATIVE = "native"
    const val PROFILE = "profile"

    fun detail(gameId: String) = "detail/$gameId"
    fun create(gameId: String? = null) =
        if (gameId.isNullOrBlank()) "create" else "create?gameId=$gameId"
}

@Composable
fun BoardGoNavHost(
    viewModel: BoardGOViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.LIST,
            modifier = Modifier.fillMaxSize()
        ) {
            // ---------- 1. LIST ----------
            composable(Routes.LIST) {
                GameListScreen(
                    viewModel = viewModel,
                    onGameClick = { gameId ->
                        // >>> PASSING ARGUMENT: gameId dikirim ke layar Detail <<<
                        navController.navigate(Routes.detail(gameId))
                    }
                )
            }

            // ---------- 2. DETAIL (dengan argumen gameId) ----------
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("gameId") { type = NavType.StringType })
            ) { entry ->
                val gameId = entry.arguments?.getString("gameId").orEmpty()
                GameDetailScreen(
                    gameId = gameId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onCreateSession = { selectedGameId ->
                        // >>> PASSING ARGUMENT lagi: ke layar Form <<<
                        navController.navigate(Routes.create(selectedGameId))
                    }
                )
            }

            // ---------- 3. FORM / INPUT ----------
            composable(
                route = Routes.CREATE,
                arguments = listOf(
                    navArgument("gameId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                val gameId = entry.arguments?.getString("gameId")
                CreateSessionScreen(
                    viewModel = viewModel,
                    initialGameId = gameId,
                    onBack = { navController.popBackStack() },
                    onSuccess = { createdId ->
                        // Navigasi ke detail sesi yang baru dibuat
                        navController.navigate(Routes.detail(createdId)) {
                            popUpTo(Routes.LIST)
                        }
                    }
                )
            }

            // ---------- 4. FITUR NATIVE (MOCKUP) ----------
            composable(Routes.NATIVE) {
                NativeFeatureScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // ---------- 5. PROFIL ----------
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = viewModel,
                    onGameClick = { gameId -> navController.navigate(Routes.detail(gameId)) }
                )
            }
        }

        // ---------- Bottom navigation bar ----------
        if (currentRoute != null) {
            BottomNavBar(
                currentRoute = currentRoute,
                modifier = Modifier.align(Alignment.BottomCenter),
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onCreate = { navController.navigate(Routes.create()) }
            )
        }
    }
}

/** Bottom navigation bar (5 menu, dengan tombol "+" di tengah). */
@Composable
private fun BottomNavBar(
    currentRoute: String?,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
    onCreate: () -> Unit
) {
    val items = listOf(
        NavItem(Routes.LIST, "HOME", Icons.Filled.Home),
        NavItem(Routes.DETAIL.replace("{gameId}", "catan"), "FIND", Icons.Filled.Explore),
        null, // tempat tombol "+"
        NavItem(Routes.PROFILE, "ME", Icons.Filled.Person),
        NavItem(Routes.NATIVE, "GPS", Icons.Filled.Place)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                if (item == null) {
                    // Tombol "+" (untuk layar Form)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .offset(y = (-14).dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        BoardGoMeeple
                                    )
                                )
                            )
                            .clickable(onClick = onCreate),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Host game",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    NavButton(
                        item = item,
                        selected = currentRoute == item.route ||
                            (item.route.startsWith("detail") && currentRoute?.startsWith("detail") == true),
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
    }
}

/** Data item bottom nav. */
private data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/** Tombol navigasi. */
@Composable
private fun NavButton(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(20.dp),
            tint = tint
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}

/** Warna meeple (oranye-kuning) untuk gradien tombol +. */
private val BoardGoMeeple = Color(0xFFF2A119)
