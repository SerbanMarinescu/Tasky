package com.example.tasky.feature_authentication.presentation.login_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.tasky.R
import com.example.tasky.feature_authentication.domain.util.AuthUseCaseResult
import com.example.tasky.feature_authentication.presentation.components.TaskyTextField
import com.example.tasky.presentation.theme.BackgroundBlack
import com.example.tasky.presentation.theme.BackgroundWhite
import com.example.tasky.presentation.theme.BtnNavRegScreen
import com.example.tasky.presentation.theme.LoginBtnTextColor
import com.example.tasky.presentation.theme.HintColor
import com.example.tasky.util.Screen

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.authResult.collect { result ->
            when(result) {
                is AuthUseCaseResult.GenericError -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                }
                is AuthUseCaseResult.Success -> {
                    navController.navigate(Screen.AgendaScreen.route)
                }
                else -> Unit
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        Text(
            text = stringResource(id = R.string.WelcomeMessage),
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 77.dp)
        )
        Spacer(modifier = Modifier.height(72.dp))
        Box(modifier = Modifier
            .fillMaxSize()
            .clip(
                RoundedCornerShape(
                    topStart = 30.dp,
                    topEnd = 30.dp
                )
            )
            .background(BackgroundWhite)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 50.dp,
                        start = 17.dp,
                        end = 15.dp
                    )
            ) {
                TaskyTextField(
                    value = state.email,
                    onValueChanged = {
                        viewModel.onEvent(LoginEvent.EmailChanged(it))
                    },
                    hint = stringResource(id = R.string.EmailHint),
                    keyboardType = KeyboardType.Email,
                    isValid = state.isEmailValid,
                    contentDescription = stringResource(id = R.string.DescriptionEmailValid),
                    isError = state.emailError != null,
                    errorMessage = state.emailError?.asString(context)
                )
                Spacer(modifier = Modifier.height(15.dp))
                TaskyTextField(
                    value = state.password,
                    onValueChanged = {
                        viewModel.onEvent(LoginEvent.PasswordChanged(it))
                    },
                    hint = stringResource(id = R.string.PasswordHint),
                    keyboardType = KeyboardType.Password,
                    passwordVisible = state.isPasswordVisible,
                    onClick = {
                        viewModel.onEvent(LoginEvent.ChangePasswordVisibility)
                    },
                    contentDescription = stringResource(id = R.string.DescriptionPasswordVisibility),
                    isError = state.passwordError != null,
                    errorMessage = state.passwordError?.asString(context)
                )
                Spacer(modifier = Modifier.height(25.dp))
                Button(
                    onClick = {
                        viewModel.onEvent(LoginEvent.SignIn)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BackgroundBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 18.dp, end = 18.dp)
                    ,
                    shape = RoundedCornerShape(38.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.SignInBtn),
                        style = MaterialTheme.typography.labelLarge,
                        color = LoginBtnTextColor
                    )
                }
                Spacer(modifier = Modifier.height(283.dp))
            }
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 70.dp,
                        bottom = 40.dp
                        )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),

                ) {
                    Text(
                        text = stringResource(id = R.string.NeedAccount),
                        style = MaterialTheme.typography.labelMedium,
                        color = HintColor
                    )
                    Text(
                        text = stringResource(id = R.string.GoSignUp),
                        style = MaterialTheme.typography.labelMedium,
                        color = BtnNavRegScreen,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.RegisterScreen.route)
                        }
                    )
                }
            }
        }
    }
}
