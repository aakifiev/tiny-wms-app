package ru.hqr.tinywms.ui.signin

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.biometric.BiometricHelper
import ru.hqr.tinywms.biometric.BiometricPreferences
import ru.hqr.tinywms.conf.EmployeeWmsRest
import ru.hqr.tinywms.cripto.CryptoManager
import ru.hqr.tinywms.type.NavRoute
import ru.hqr.tinywms.ui.component.CustomOutlinedTextField
import ru.hqr.tinywms.ui.component.EnableBiometricDialog
import ru.hqr.tinywms.util.ENCRYPTED_FILE_NAME
import ru.hqr.tinywms.util.PREF_BIOMETRIC

@Composable
fun SignInScreen(navController: NavHostController) {

    val context = LocalContext.current as FragmentActivity
    val viewModel = remember { SignInScreenViewModel(BiometricPreferences(context)) }
    val emailId by viewModel.emailId.collectAsState()
    val password by viewModel.password.collectAsState()

    val rememberCoroutineScope = rememberCoroutineScope()

    val state by viewModel.state.collectAsState()

    val isPasswordError = state is SignInState.InvalidPassword
    val isEmailError = state is SignInState.InvalidEmailId
    val isBiometricAvailable = remember { BiometricHelper.isBiometricAvailable(context) }
    val showBiometricPrompt by viewModel.showBiometricPrompt.collectAsState()
    val showBiometricIcon = remember { mutableStateOf(false) }

    var showBiometricEnableDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = state) {
        if (state is SignInState.Success && !showBiometricEnableDialog) {
            navController.navigate(NavRoute.HOME.name)
        }
    }

    LaunchedEffect(key1 = isBiometricAvailable) {
        if (isBiometricAvailable) {
            viewModel.checkIfBiometricLoginEnabled()
        }
    }

    LaunchedEffect(key1 = showBiometricPrompt) {
        if (showBiometricPrompt) {
            BiometricHelper.authenticateUser(
                context,
                onSuccess = { plainText ->
                    viewModel.setToken(plainText)
                    rememberCoroutineScope.launch {
                        val password1 = viewModel.preferences.getPassword()
                        val email1 = viewModel.preferences.getUserName()
                        EmployeeWmsRest.retrofitService
                            .findEmployee(email1!!, password1!!)
                            .enqueue(object : Callback<Int?> {
                                override fun onResponse(p0: Call<Int?>, p1: Response<Int?>) {
                                    p1.body()?.let {
                                        navController.navigate(NavRoute.HOME.name)
                                    }
                                }

                                override fun onFailure(p0: Call<Int?>, p1: Throwable) {
                                    Log.i("", "login failure")
                                }
                            })
                    }
                })
        } else {
            val cryptoManager = CryptoManager()
            val encryptedData = cryptoManager.getFromPrefs(
                context,
                ENCRYPTED_FILE_NAME,
                Context.MODE_PRIVATE,
                PREF_BIOMETRIC
            )
            encryptedData?.let {
                showBiometricIcon.value = true
            }
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

    Scaffold { padding ->
        Column(
            modifier = Modifier
//                .padding(16.dp)
                .background(color = Color.Red)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Вход в TinyWms",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
//                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomOutlinedTextField(
                label = "E-mail",
                text = emailId,
                isPassword = false,
                isError = isEmailError,
                showTrailingIcon = showBiometricIcon.value,
                onValueChange = {
                    viewModel.onEmailIdChanged(it)
                },
                onTrailingIconClicked = {
                    BiometricHelper.authenticateUser(context) { plainText ->
                        viewModel.setToken(plainText)
                        navController.navigate(NavRoute.HOME.name)
                    }
                }
            )

            Spacer(modifier = Modifier.padding(3.dp))
            CustomOutlinedTextField(
                label = "Пароль",
                text = password,
                isPassword = true,
                isError = isPasswordError
            ) {
                viewModel.onPasswordChanged(it)
            }
            Row(
                modifier = Modifier.background(color = Color.Transparent)
                    .padding(start = 16.dp, end = 16.dp)
//                    .background(color = Color.Green)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                TextButton(
                    modifier = Modifier
//                        .background(color = Color.Blue)
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    onClick = {  },
                ) {
                    Text(
                        text = "Забыли пароль?",
                        color = Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            AnimatedVisibility(visible = state is SignInState.InvalidCredentials) {
                Text(
                    text = "email or password",
                    fontSize = 14.sp,
                    color = Color.Red
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp),
                onClick = {
                    rememberCoroutineScope.launch {
                        viewModel.setBiometricEnabled(false)
                        viewModel.onLoginClicked2(context)
                        viewModel.state.value.let {
                            if (SignInState.Success == it && isBiometricAvailable) {
                                showBiometricEnableDialog = true
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(color = Color.Yellow, shape = RoundedCornerShape(4.dp))
//                            .background(
//                                brush =
//                                    Brush.horizontalGradient(
//                                        colors = listOf(
//                                            Color(0xFF484BF1),
//                                            Color(0xFFB4A0F5)
//                                        )
//                                    ),
//                                shape = RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp)
//                            )
                            .clip(RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Войти", fontSize = 20.sp, color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp),
                onClick = { navController.navigate(NavRoute.SIGN_UP.name) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(color = Color.Red, shape = RoundedCornerShape(4.dp))
                            .border(width = 1.dp, color = Color.Yellow, shape = RoundedCornerShape(4.dp))
                            .clip(RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp))
                            .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Регистрация", fontSize = 20.sp, color = Color.Yellow)
                }
            }
        }
    }
}