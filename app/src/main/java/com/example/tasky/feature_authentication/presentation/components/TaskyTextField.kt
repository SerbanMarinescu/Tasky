package com.example.tasky.feature_authentication.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.presentation.theme.GreenValid
import com.example.tasky.presentation.theme.TextFieldBackground
import com.example.tasky.presentation.theme.HintColor
import com.example.tasky.presentation.theme.InputTextColor
import com.example.tasky.presentation.theme.RedInvalid
import com.example.tasky.presentation.theme.VisibilityIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskyTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    hint: String,
    keyboardType: KeyboardType,
    isValid: Boolean = false,
    contentDescription: String? = null,
    passwordVisible: Boolean = false,
    onClick: () -> Unit = {},
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall,
                color = HintColor
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = InputTextColor,
            containerColor = TextFieldBackground
        ),
        textStyle = MaterialTheme.typography.labelSmall + TextStyle(
            color = if(isError) RedInvalid else InputTextColor
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = {
            if((keyboardType == KeyboardType.Email || keyboardType == KeyboardType.Text) && isValid) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = contentDescription,
                    tint = GreenValid
                )
            }
            if(keyboardType == KeyboardType.Password) {
                Icon(
                    painterResource(
                        id = if(passwordVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
                    ),
                    contentDescription = contentDescription,
                    tint = VisibilityIcon,
                    modifier = Modifier.clickable {
                        onClick()
                    }
                )
            }
        },
        visualTransformation = if(keyboardType == KeyboardType.Password) {
            if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        isError = isError
    )
}