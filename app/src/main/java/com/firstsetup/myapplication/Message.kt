package com.firstsetup.myapplication

data class Message(
    val text: String,
    val isOption: Boolean,
    val isUser: Boolean = false
)