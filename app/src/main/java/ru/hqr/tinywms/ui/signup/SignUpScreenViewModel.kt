package ru.hqr.tinywms.ui.signup

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
import ru.hqr.tinywms.ui.signin.SignInState

//class SignUpScreenViewModel @Inject constructor(
//    private val preferences: BiometricPreferences
//) : ViewModel() {
class SignUpScreenViewModel(
    private val preferences: BiometricPreferences
) : ViewModel() {

    val state = MutableStateFlow<SignUpState?>(null)
    val emailId = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    fun onEmailIdChanged(emailId: String) {
        this.emailId.value = emailId
    }

    fun onPasswordChanged(password: String) {
        this.password.value = password
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        this.confirmPassword.value = confirmPassword
    }

    fun onSignUpClicked(context: Context) {
//        if (!validateEmailId()) {
//            state.tryEmit(SignUpState.InvalidEmailId)
//        } else if (!validatePassword()) {
//            state.tryEmit(SignUpState.InvalidPassword)
//        } else if (!validateConfirmPassword()) {
//            state.tryEmit(SignUpState.InvalidConfirmPassword)
//        } else {
            saveUserCredentials(context)
//        }
    }

    private fun saveUserCredentials(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val sharedPreferences =
                    context.getSharedPreferences("TinyPrefs", Context.MODE_PRIVATE)
                EmployeeWmsRest.retrofitService
                    .createEmployee(emailId.value, password.value)
                    .enqueue(object : Callback<Int?> {
                        override fun onResponse(p0: Call<Int?>, p1: Response<Int?>) {
                            p1.body()?.let {
                                sharedPreferences.edit {
                                    putInt("clientId", it)
                                }
                                state.tryEmit(SignUpState.SUCCESS)
                                viewModelScope.launch {
                                    preferences.setUserName(emailId.value)
                                    preferences.setPassword(password.value)
                                }
                            }
                        }

                        override fun onFailure(p0: Call<Int?>, p1: Throwable) {
                            state.tryEmit(SignUpState.FAILURE)
                            Log.i("", "login failure")
                        }
                    })
//                preferences.setUserName(emailId.value)
//                preferences.setPassword(password.value)
//                state.tryEmit(SignUpState.SUCCESS)
            }
        }
    }

    private fun validateEmailId(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailId.value).matches()
    }

    private fun validatePassword(): Boolean {
        return password.value.length > 5
    }

    private fun validateConfirmPassword(): Boolean {
        return password.value == confirmPassword.value
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                preferences.setBiometricEnabled(enabled)
            }
        }
    }
}

sealed class SignUpState {
    data object SUCCESS : SignUpState()
    data object FAILURE : SignUpState()
    data object InvalidEmailId : SignUpState()
    data object InvalidPassword : SignUpState()
    data object InvalidConfirmPassword : SignUpState()
}