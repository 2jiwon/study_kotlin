package com.fancytank.mypaws

import android.content.Context

object UserPreferences {
    private const val PREF_NAME = "MyPawsPrefs"
    private const val KEY_USER_ID = "USER_ID"

    fun saveUserId(context: Context, userId: Long) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putLong(KEY_USER_ID, userId).apply()
    }

    fun getUserId(context: Context): Long? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val userId = sharedPref.getLong(KEY_USER_ID, -1L)
        return if (userId != -1L) userId else null
    }

    fun clearUserData(context: Context) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }
}