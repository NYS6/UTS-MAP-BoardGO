package id.boardgo.app.ui.screens.auth

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.boardgo.app.ui.components.BoardGoPrimaryButton
import id.boardgo.app.ui.components.SkyBackdrop
import id.boardgo.app.ui.components.TopBar
import id.boardgo.app.ui.viewmodel.BoardGOViewModel

/**
 * ================== LAYAR REGISTER / LOGIN (AuthForm) =====================
 * Meniru AuthForm web app:
 *  - Register : judul "GRAB A SEAT" + Full name / Email / Password
 *  - Login    : judul "WELCOME BACK" + Email / Password
 * VALIDASI (kriteria UTS): nama wajib, email regex, password min 6 karakter,
 * keyboard email + password (toggle lihat password — state remember lokal).
 * =========================================================================
 */
@Composable
fun AuthScreen(
    isRegister: Boolean,
    viewModel: BoardGOViewModel,
    onBack: () -> Unit,
    onSwitchAuth: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.authState.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) } // state lokal UI

    // Reset form saat pindah antar register/login
    LaunchedEffect(isRegister) { viewModel.resetAuth() }

    // Navigasi setelah submit sukses (successTick naik)
    LaunchedEffect(state.successTick) {
        if (state.successTick > 0) {
            onSuccess()
            viewModel.resetAuth()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        SkyBackdrop(modifier = Modifier.fillMaxWidth().height(300.dp), tall = true)
        TopBar(title = "", onBack = onBack, light = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Judul display besar
            Text(
                text = if (isRegister) "GRAB A\nSEAT" else "WELCOME\nBACK",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))

            // Kartu form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isRegister) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = viewModel::onAuthNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Full name") },
                        singleLine = true,
                        isError = state.nameError != null,
                        supportingText = {
                            Text(
                                text = state.nameError ?: " ",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.nameError != null)
                                    MaterialTheme.colorScheme.error
                                else Color.Transparent
                            )
                        },
                        keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                        shape = RoundedCornerShape(16.dp),
                        colors = fieldColors()
                    )
                }

                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onAuthEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Email") },
                    singleLine = true,
                    isError = state.emailError != null,
                    supportingText = {
                        Text(
                            text = state.emailError ?: " ",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.emailError != null)
                                MaterialTheme.colorScheme.error
                            else Color.Transparent
                        )
                    },
                    // >>> keyboard EMAIL <<<
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors()
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onAuthPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Password") },
                    singleLine = true,
                    isError = state.passwordError != null,
                    supportingText = {
                        Text(
                            text = state.passwordError ?: "At least 6 characters",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.passwordError != null)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    // >>> keyboard password + toggle lihat <<<
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                                else Icons.Filled.Visibility,
                                contentDescription = "Toggle password",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = fieldColors()
                )

                BoardGoPrimaryButton(
                    text = if (state.isSubmitting) "One sec…"
                    else if (isRegister) "Create account" else "Log in",
                    onClick = { viewModel.submitAuth(isRegister) },
                    loading = state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )

                // Tombol Google (mockup)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                        .clickable(onClick = onSuccess)
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Ganti mode auth
            Row {
                Text(
                    text = if (isRegister) "Have an account? " else "New here? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isRegister) "Log in" else "Sign up",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onSwitchAuth)
                        .padding(horizontal = 2.dp)
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)
