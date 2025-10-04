package com.example.clase7

import android.content.Context
import android.util.Patterns

fun validateEmail (value: String, context: Context): Pair<Boolean, String> {
    return when {
        value.isEmpty() -> Pair(false, context.getString(R.string.login_screen_validation_email_empty))
        !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> Pair(false, context.getString(R.string.login_screen_validation_email_invalid))
        else -> Pair(true, "")
    }
}

fun validatePassword(value: String, context: Context): Pair<Boolean, String> {
    return when {
        value.isEmpty() -> Pair(false, context.getString(R.string.login_screen_validation_password_empty))
        value.length < 6 -> Pair(false, context.getString(R.string.login_screen_validation_password_invalid))
        else -> Pair(true, "")
    }
}