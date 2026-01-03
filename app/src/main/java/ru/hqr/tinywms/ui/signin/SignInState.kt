package ru.hqr.tinywms.ui.signin


import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hqr.tinywms.biometric.BiometricPreferences
import ru.hqr.tinywms.conf.EmployeeWmsRest

class SignInScreenViewModel(
    val preferences: BiometricPreferences
) : ViewModel() {

    val emailId = MutableStateFlow("")
    val password = MutableStateFlow("")
    val state = MutableStateFlow<SignInState?>(null)
    val showBiometricPrompt = MutableStateFlow(false)

    fun onEmailIdChanged(emailId: String) {
        this.emailId.value = emailId
    }

    fun onPasswordChanged(password: String) {
        this.password.value = password
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                preferences.setBiometricEnabled(enabled)
            }
        }
    }

    fun onLoginClicked(context: Context) = viewModelScope.launch {
//        if (!validateEmailId()) {
//            state.tryEmit(SignInState.InvalidEmailId)
//        } else if (!validatePassword()) {
//            state.tryEmit(SignInState.InvalidPassword)
//        } else {
        validateUserCredentials(context)
//        }
    }

    suspend fun onLoginClicked2(context: Context) {
        validateUserCredentials2(context)
    }

    fun onSignOutClicked(context: Context) = viewModelScope.launch {
        preferences.resetUserName()
        preferences.resetPassword()
        preferences.setBiometricEnabled(false)
        showBiometricPrompt.tryEmit(false)
//        setBiometricEnabled(false)
    }

    private fun validateUserCredentials(context: Context) {
        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
            val sharedPreferences =
                context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
            EmployeeWmsRest.retrofitService
                .findEmployee(emailId.value, password.value)
                .enqueue(object : Callback<Int?> {
                    override fun onResponse(p0: Call<Int?>, p1: Response<Int?>) {
                        p1.body()?.let {
                            sharedPreferences.edit {
                                putInt("clientId", it)
                            }
                            state.tryEmit(SignInState.Success)
                            viewModelScope.launch {
                                preferences.setUserName(emailId.value)
                                preferences.setPassword(password.value)
                            }
                        }
                    }

                    override fun onFailure(p0: Call<Int?>, p1: Throwable) {
                        state.tryEmit(SignInState.InvalidCredentials)
                        Log.i("", "login failure")
                    }
                })
//                val username = preferences.getUserName()
//                val password = preferences.getPassword()
//                if (emailId.value != username || this@SignInScreenViewModel.password.value != password) {
//                    state.tryEmit(SignInState.InvalidCredentials)
//                } else {
//                    state.tryEmit(SignInState.Success)
//                }
//            }
        }
    }

    private suspend fun validateUserCredentials2(context: Context) {
        withContext(Dispatchers.IO) {
            val sharedPreferences =
                context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
            val execute = EmployeeWmsRest.retrofitService
                .findEmployee(emailId.value, password.value)
                .execute()
            if (execute.isSuccessful) {
                execute.body()?.let {
                    sharedPreferences.edit {
                        putInt("clientId", it)
                    }
                    state.tryEmit(SignInState.Success)
                    viewModelScope.launch {
                        preferences.setUserName(emailId.value)
                        preferences.setPassword(password.value)
                    }
                }
            } else {
                state.tryEmit(SignInState.InvalidCredentials)
                Log.i("", "login failure")
            }

//                .enqueue(object : Callback<Int?> {
//                    override fun onResponse(p0: Call<Int?>, p1: Response<Int?>) {
//                        p1.body()?.let {
//                            sharedPreferences.edit {
//                                putInt("clientId", it)
//                            }
//                            state.tryEmit(SignInState.Success)
//                            viewModelScope.launch {
//                                preferences.setUserName(emailId.value)
//                                preferences.setPassword(password.value)
//                            }
//                        }
//                    }
//
//                    override fun onFailure(p0: Call<Int?>, p1: Throwable) {
//                        state.tryEmit(SignInState.InvalidCredentials)
//                        Log.i("", "login failure")
//                    }
//                })
//                val username = preferences.getUserName()
//                val password = preferences.getPassword()
//                if (emailId.value != username || this@SignInScreenViewModel.password.value != password) {
//                    state.tryEmit(SignInState.InvalidCredentials)
//                } else {
//                    state.tryEmit(SignInState.Success)
//                }
        }
    }

    private fun validateEmailId(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailId.value).matches()
    }

    private fun validatePassword(): Boolean {
        return password.value.length > 5
    }

    fun checkIfBiometricLoginEnabled() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val isBiometricEnabled = preferences.isBiometricEnabled()
                if (isBiometricEnabled) {
                    showBiometricPrompt.tryEmit(true)
                }
            }
        }
    }

    fun setToken(plainText: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                //Now that you have obtained the token, you can query the server for additional
                // information. We can call this token as sample token for now because it wasn't actually
                // retrieved from the server. If you obtain it from the server then, it would be a genuine token.
                preferences.setToken(plainText)
            }
        }
    }
}

sealed class SignInState {

    data object InvalidEmailId : SignInState()
    data object InvalidPassword : SignInState()
    data object InvalidCredentials : SignInState()
    data object Success : SignInState()
}