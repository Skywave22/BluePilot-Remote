package com.bluepilot.remote.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bluepilot.remote.ui.screens.permission.PermissionScreen
import com.bluepilot.remote.ui.screens.connection.NewConnectionScreen
import com.bluepilot.remote.ui.screens.mouse.MouseKeyboardScreen
import com.bluepilot.remote.ui.screens.multimedia.MultimediaScreen
import com.bluepilot.remote.ui.screens.presenter.PresenterScreen
import com.bluepilot.remote.ui.screens.keyboard.PCKeyboardScreen
import com.bluepilot.remote.ui.screens.numpad.NumpadScreen
import com.bluepilot.remote.ui.screens.scanner.ScannerScreen
import com.bluepilot.remote.ui.screens.gamepad.GamepadScreen
import com.bluepilot.remote.ui.screens.settings.SettingsScreen
import com.bluepilot.remote.ui.screens.devices.DevicesScreen
import com.bluepilot.remote.ui.screens.help.HelpScreen

/**
 * Navigation routes for the app
 */
object Routes {
    const val PERMISSION = "permission"
    const val NEW_CONNECTION = "new_connection"
    const val MOUSE_KEYBOARD = "mouse_keyboard"
    const val MULTIMEDIA = "multimedia"
    const val PRESENTER = "presenter"
    const val PC_KEYBOARD = "pc_keyboard"
    const val NUMPAD = "numpad"
    const val SCANNER = "scanner"
    const val GAMEPAD = "gamepad"
    const val SETTINGS = "settings"
    const val DEVICES = "devices"
    const val HELP = "help"
}

/**
 * Main navigation component for BluePilot Remote
 */
@Composable
fun BluePilotNavigation(
    navController: NavHostController = rememberNavController(),
    onRequestPermissions: (List<String>) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.PERMISSION
    ) {
        composable(Routes.PERMISSION) {
            PermissionScreen(
                onRequestPermissions = onRequestPermissions,
                onContinue = {
                    navController.navigate(Routes.NEW_CONNECTION) {
                        popUpTo(Routes.PERMISSION) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.NEW_CONNECTION) {
            NewConnectionScreen(
                onNavigateToScreen = { route ->
                    navController.navigate(route)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToDevices = {
                    navController.navigate(Routes.DEVICES)
                }
            )
        }

        composable(Routes.MOUSE_KEYBOARD) {
            MouseKeyboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToConnection = {
                    navController.navigate(Routes.NEW_CONNECTION)
                },
                onNavigateToKeyboard = {
                    navController.navigate(Routes.PC_KEYBOARD)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }

        composable(Routes.MULTIMEDIA) {
            MultimediaScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PRESENTER) {
            PresenterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PC_KEYBOARD) {
            PCKeyboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.NUMPAD) {
            NumpadScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SCANNER) {
            ScannerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.GAMEPAD) {
            GamepadScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DEVICES) {
            DevicesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HELP) {
            HelpScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
