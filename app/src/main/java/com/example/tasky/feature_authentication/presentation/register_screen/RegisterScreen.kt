package com.example.tasky.feature_authentication.presentation.register_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.tasky.presentation.theme.LoginBtnTextColor
import com.example.tasky.util.Screen

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
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
            text = stringResource(id = R.string.RegisterScreenTitle),
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
                        top = 40.dp,
                        start = 16.dp,
                        end = 16.dp,
                    )
            ) {
                TaskyTextField(
                    value = state.fullName,
                    onValueChanged = {
                        viewModel.onEvent(RegisterEvent.FullNameChanged(it))
                    },
                    hint = stringResource(id = R.string.FullNameHint),
                    keyboardType = KeyboardType.Text,
                    contentDescription = stringResource(id = R.string.DescriptionFullNameValid),
                    isValid = state.isFullNameValid,
                    isError = state.fullNameError != null,
                    errorMessage = state.fullNameError?.asString(context)
                )
                Spacer(modifier = Modifier.height(15.dp))
                TaskyTextField(
                    value = state.email,
                    onValueChanged = {
                        viewModel.onEvent(RegisterEvent.EmailChanged(it))
                    },
                    hint = stringResource(id = R.string.EmailHint),
                    keyboardType = KeyboardType.Email,
                    contentDescription = stringResource(id = R.string.DescriptionEmailValid),
                    isValid = state.isEmailValid,
                    isError = state.emailError != null,
                    errorMessage = state.emailError?.asString(context)
                )
                Spacer(modifier = Modifier.height(15.dp))
                TaskyTextField(
                    value = state.password,
                    onValueChanged = {
                        viewModel.onEvent(RegisterEvent.PasswordChanged(it))
                    },
                    hint = stringResource(id = R.string.PasswordHint),
                    onClick = {
                        viewModel.onEvent(RegisterEvent.ChangePasswordVisibility)
                    },
                    contentDescription = stringResource(id = R.string.DescriptionPasswordVisibility),
                    keyboardType = KeyboardType.Password,
                    passwordVisible = state.isPasswordVisible,
                    isError = state.passwordError != null,
                    errorMessage = state.passwordError?.asString(context)
                )
            }
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 330.dp,
                        start = 16.dp,
                        end = 16.dp,
                    )
            ) {
                Button(
                    onClick = {
                        viewModel.onEvent(RegisterEvent.SignUp)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BackgroundBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(38.dp)
                ) {
                    Text(text = stringResource(id = R.string.SignUpBtn),
                        style = MaterialTheme.typography.labelLarge,
                        color = LoginBtnTextColor
                    )
                }
            }
            Box(contentAlignment = Alignment.BottomStart,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        bottom = 68.dp
                    )
            ){
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.LoginScreen.route)
                    },
                    modifier = Modifier
                        .size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}