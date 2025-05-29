package com.firstsetup.myapplication.model
data class Message(
    val text: String,
    val isOption: Boolean = false,
    val isUser: Boolean = false
)
