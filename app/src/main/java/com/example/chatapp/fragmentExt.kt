package com.example.chatapp

import androidx.fragment.app.Fragment

fun Fragment.runOnUiThread(action: () -> Unit) {
    if (isAdded.not()) return
    activity?.runOnUiThread(action)
}
