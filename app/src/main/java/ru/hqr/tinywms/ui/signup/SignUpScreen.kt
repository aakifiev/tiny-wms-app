package ru.hqr.tinywms.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import ru.hqr.tinywms.biometric.BiometricHelper
import ru.hqr.tinywms.biometric.BiometricPreferences
import ru.hqr.tinywms.type.NavRoute
import ru.hqr.tinywms.ui.component.CustomOutlinedTextField
import ru.hqr.tinywms.ui.component.EnableBiometricDialog

@Composable
fun SignUpScreen(navController: NavHostController) {
    val context = LocalContext.current as FragmentActivity
    val isBiometricAvailable = remember { BiometricHelper.isBiometricAvailable(context) }
    val viewModel = remember { SignUpScreenViewModel(BiometricPreferences(context)) }
    val emailId by viewModel.emailId.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val state by viewModel.state.collectAsState()
    val isPasswordError = state is SignUpState.InvalidPassword
    val isConfirmPasswordError = state is SignUpState.InvalidConfirmPassword
    val isEmailError = state is SignUpState.InvalidEmailId
    var showBiometricEnableDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = state) {
        if (state is SignUpState.SUCCESS) {
            showBiometricEnableDialog = true
        }
    }

    if (showBiometricEnableDialog) {
        if (isBiometricAvailable) {
            EnableBiometricDialog(
                onEnable = {
                    BiometricHelper.registerUserBiometrics(context) {
                        showBiometricEnableDialog = false
                        viewModel.setBiometricEnabled(true)
                        navController.navigate(NavRoute.HOME.name)
                    }
                },
                {
                    navController.navigate(NavRoute.HOME.name)
                }
            )
        } else {
            navController.navigate(NavRoute.HOME.name)
        }
    }

    Column(
        modifier = Modifier
            .background(color = Color.Red)
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Регистрация",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
//            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomOutlinedTextField(
            label = "E-mail",
            text = emailId,
            isPassword = false,
            isError = isEmailError
        ) {
            viewModel.onEmailIdChanged(it)
        }

        Spacer(modifier = Modifier.padding(3.dp))
        CustomOutlinedTextField(
            label = "Пароль",
            text = password,
            isPassword = true,
            isError = isPasswordError
        ) {
            viewModel.onPasswordChanged(it)
        }

        Spacer(modifier = Modifier.padding(3.dp))
        CustomOutlinedTextField(
            label = "Повторите пароль",
            text = confirmPassword,
            isPassword = true,
            isError = isConfirmPasswordError
        ) {
            viewModel.onConfirmPasswordChanged(it)
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            onClick = {
                viewModel.onSignUpClicked(context)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color = Color.Yellow, shape = RoundedCornerShape(4.dp))
                        .clip(RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Создать",
                    fontSize = 20.sp,
                    color = Color.Red
                )
            }
        }
    }
}