package com.example.lunark.admin



import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * UserPreferencesManager manages user preferences such as selected role and other settings
 */
class UserPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCE_NAME, Context.MODE_PRIVATE
    )

    /**
     * Save the user's selected role
     * @param role The selected role (USER or ADMIN)
     */
    fun saveUserRole(role: String) {
        sharedPreferences.edit {
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }

    /**
     * Get the user's saved role
     * @return The saved role or null if not set
     */
    fun getUserRole(): String? {
        return sharedPreferences.getString(KEY_USER_ROLE, null)
    }

    /**
     * Clear all saved preferences
     */
    fun clearPreferences() {
        sharedPreferences.edit {
            clear()
            apply()
        }
    }

    companion object {
        private const val PREFERENCE_NAME = "lunark_preferences"
        private const val KEY_USER_ROLE = "user_role"

        // Role constants
        const val ROLE_USER = "USER"
        const val ROLE_ADMIN = "ADMIN"
    }
}