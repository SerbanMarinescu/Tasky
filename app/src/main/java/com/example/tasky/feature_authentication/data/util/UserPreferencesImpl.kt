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
            .apply()
    }

    override fun getAuthenticatedUser(): AuthenticatedUser? {
        val fullName = prefs.getString("fullName", null)
        val email = prefs.getString("email", null)
        val token = prefs.getString("jwt", null)

        return fullName?.let {
            email?.let { it1 ->
                token?.let { it2 ->
                    AuthenticatedUser(
                        fullName = it,
                        email = it1,
                        token = it2
                    )
                }
            }
        }
    }

    override fun clearPreferences() {
        prefs.edit().clear().apply()
    }
}