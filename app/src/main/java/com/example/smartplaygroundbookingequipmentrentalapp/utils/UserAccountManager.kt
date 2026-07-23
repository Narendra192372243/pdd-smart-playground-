package com.example.smartplaygroundbookingequipmentrentalapp.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class UserAccount(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val location: String,
    val password: String
)

object UserAccountManager {
    private const val PREF_NAME = "SmartPlaygroundAccountsPref"
    private const val KEY_ACCOUNTS_JSON = "accounts_json"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserAccount(
        context: Context,
        name: String,
        email: String,
        phone: String,
        location: String,
        password: String
    ): UserAccount {
        val prefs = getPrefs(context)
        val jsonString = prefs.getString(KEY_ACCOUNTS_JSON, "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)

        val id = "usr_" + Math.abs((phone + email).hashCode())
        val newAccount = JSONObject().apply {
            put("id", id)
            put("name", name)
            put("email", email)
            put("phone", phone)
            put("location", location)
            put("password", password)
        }

        var updated = false
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            if (obj.optString("phone") == phone || (email.isNotEmpty() && obj.optString("email") == email)) {
                jsonArray.put(i, newAccount)
                updated = true
                break
            }
        }
        if (!updated) {
            jsonArray.put(newAccount)
        }

        prefs.edit().putString(KEY_ACCOUNTS_JSON, jsonArray.toString()).apply()
        return UserAccount(id, name, email, phone, location, password)
    }

    fun findUserAccount(context: Context, input: String): UserAccount? {
        val prefs = getPrefs(context)
        val jsonString = prefs.getString(KEY_ACCOUNTS_JSON, "[]") ?: "[]"
        val jsonArray = JSONArray(jsonString)

        val query = input.trim()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val p = obj.optString("phone")
            val e = obj.optString("email")
            if (p.equals(query, ignoreCase = true) || e.equals(query, ignoreCase = true) || (query.length >= 6 && p.endsWith(query))) {
                return UserAccount(
                    id = obj.optString("id"),
                    name = obj.optString("name"),
                    email = obj.optString("email"),
                    phone = obj.optString("phone"),
                    location = obj.optString("location", "Adyar, Chennai"),
                    password = obj.optString("password")
                )
            }
        }
        return null
    }
}
