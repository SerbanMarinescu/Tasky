package com.example.tasky.feature_authentication.data.util

import android.content.SharedPreferences
import com.example.tasky.feature_authentication.domain.model.AuthenticatedUser
import com.example.tasky.feature_authentication.domain.util.UserPreferences

class UserPreferencesImpl(
    private val prefs: SharedPreferences
) : UserPreferences{
    override fun saveAuthenticatedUser(user: AuthenticatedUser) {
        prefs.edit()
            .putString("jwt", user.token)
            .putString("fullName", user.fullName)
            .putString("email", user.email)
            .putString("userId", user.userId)
            .apply()
    }

    override fun getAuthenticatedUser(): AuthenticatedUser? {
        val fullName = prefs.getString("fullName", null) ?: return null
        val email = prefs.getString("email", null) ?: return null
        val token = prefs.getString("jwt", null) ?: return null
        val userId = prefs.getString("userId", null) ?: return null

        return AuthenticatedUser(
            fullName = fullName,
            email = email,
            token = token,
            userId = userId
        )
    }

    override fun clearPreferences() {
        prefs.edit().clear().apply()
    }
}