package id.boardgo.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import id.boardgo.app.ui.screens.mygames.MyGamesScreen
import id.boardgo.app.ui.screens.nativefeature.NativeFeatureScreen
import id.boardgo.app.ui.screens.profile.ProfileScreen
import id.boardgo.app.ui.screens.welcome.WelcomeScreen
import id.boardgo.app.ui.theme.BoardGoColors
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ============================ NAVIGASI ====================================
 * Route:
 *  - "welcome"               : landing (start destination)
 *  - "list"                  : Layar List / Discover (LazyColumn)
 *  - "detail/{gameId}"       : Detail  << ARGUMEN gameId dari List
 *  - "create?gameId={gameId}" : Form (opsional pra-isi dari Detail)
 *  - "mygames"               : My Games (tab Upcoming/Past)
 *  - "native"                : Fitur Native mockup
 *  - "profile"               : Profil (Log out -> kembali ke welcome)
 * =======================================================================
 */
object Routes {
    const val WELCOME = "welcome"
    const val LIST = "list"
    const val DETAIL = "detail/{gameId}"
    const val CREATE = "create?gameId={gameId}"
    const val MYGAMES = "mygames"
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
            startDestination = Routes.WELCOME,
            modifier = Modifier.fillMaxSize()
        ) {
            // ---------- WELCOME (landing) ----------
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onStart = {
                        navController.navigate(Routes.LIST) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }

            // ---------- LIST / DISCOVER ----------
            composable(Routes.LIST) {
                GameListScreen(
                    viewModel = viewModel,
                    onGameClick = { gameId ->
                        // >>> PASSING ARGUMENT: gameId ke layar Detail <<<
                        navController.navigate(Routes.detail(gameId))
                    }
                )
            }

            // ---------- DETAIL (argumen gameId) ----------
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
                        // >>> PASSING ARGUMENT: gameId ke layar Form <<<
                        navController.navigate(Routes.create(selectedGameId))
                    }
                )
            }

            // ---------- FORM / INPUT ----------
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
                    onSuccess = { selectedGameId ->
                        navController.navigate(Routes.detail(selectedGameId)) {
                            popUpTo(Routes.LIST)
                        }
                    }
                )
            }

            // ---------- MY GAMES ----------
            composable(Routes.MYGAMES) {
                MyGamesScreen(
                    viewModel = viewModel,
                    onGameClick = { gameId -> navController.navigate(Routes.detail(gameId)) }
                )
            }

            // ---------- FITUR NATIVE (MOCKUP) ----------
            composable(Routes.NATIVE) {
                NativeFeatureScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // ---------- PROFIL ----------
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = viewModel,
                    onGameClick = { gameId -> navController.navigate(Routes.detail(gameId)) },
                    onLogout = {
                        // Mockup log out: kembali ke layar welcome
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        // ---------- Bottom navigation (pill melayang, gaya web app) ----------
        if (currentRoute != null && currentRoute != Routes.WELCOME) {
            BottomNavBar(
                currentRoute = currentRoute,
                modifier = Modifier.align(Alignment.BottomCenter),
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
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

/** Item bottom nav. */
private data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/** Bottom bar pill melayang — meniru web app (HOME · MINE · + · ME · FITUR). */
@Composable
private fun BottomNavBar(
    currentRoute: String?,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit,
    onCreate: () -> Unit
) {
    val items = listOf(
        NavItem(Routes.LIST, "HOME", Icons.Filled.Home),
        NavItem(Routes.MYGAMES, "MINE", Icons.Filled.ViewAgenda),
        null, // tombol "+"
        NavItem(Routes.PROFILE, "ME", Icons.Filled.Person),
        NavItem(Routes.NATIVE, "FITUR", Icons.Filled.Place)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.94f))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp))
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                if (item == null) {
                    // Tombol "+" (layar Form)
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .offset(y = (-12).dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        BoardGoColors.Meeple
                                    )
                                )
                            )
                            .clickable(onClick = onCreate),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Host a game",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    NavButton(
                        item = item,
                        selected = currentRoute == item.route ||
                            (item.route == Routes.LIST && currentRoute?.startsWith("detail") == true),
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
    }
}

/** Tombol nav: ikon + label mono; aktif = oranye + pill lembut. */
@Composable
private fun NavButton(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(16.dp),
            tint = tint
        )
        androidx.compose.foundation.layout.Spacer(Modifier.size(5.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}
