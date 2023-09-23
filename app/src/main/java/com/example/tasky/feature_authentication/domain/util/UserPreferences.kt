package com.example.tasky.feature_authentication.domain.util

import com.example.tasky.feature_authentication.domain.model.AuthenticatedUser

interface UserPreferences {
    fun saveAuthenticatedUser(user: AuthenticatedUser)
    fun getAuthenticatedUser(): AuthenticatedUser?
    fun clearPreferences()
}