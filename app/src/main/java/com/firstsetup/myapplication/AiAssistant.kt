package com.firstsetup.myapplication

import android.content.Context
import org.json.JSONObject

class AiAssistant {

    private lateinit var startData: JSONObject
    private lateinit var categoriesData: JSONObject
    private lateinit var topicsData: JSONObject

    private var currentState = "start"

    interface Callback {
        fun onAiRespond(message: String, options: List<String>)
    }

    fun loadConversation(context: Context) {
        startData = loadJsonFromAssets(context, "start.json")
        categoriesData = loadJsonFromAssets(context, "categories.json")
        topicsData = loadJsonFromAssets(context, "topics.json")
    }

    private fun loadJsonFromAssets(context: Context, fileName: String): JSONObject {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charsets.UTF_8)
        return JSONObject(json)
    }

    fun startConversation(callback: Callback) {
        val message = startData.getString("message")
        val optionsArray = startData.getJSONArray("options")
        val options = mutableListOf<String>()
        for (i in 0 until optionsArray.length()) {
            options.add(optionsArray.getString(i))
        }
        callback.onAiRespond(message, options)
    }

    fun handleUserChoice(choice: String, callback: Callback) {
        val nextNode = topicsData.optJSONObject(choice)

        if (nextNode != null) {
            val message = nextNode.getString("message")
            val optionsArray = nextNode.optJSONArray("options")
            val options = mutableListOf<String>()
            if (optionsArray != null) {
                for (i in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(i))
                }
            }

            val next = nextNode.optString("next", null)
            if (next != null && next == "categories") {
                loadCategories(callback)
            } else {
                callback.onAiRespond(message, options)
            }
        } else {
            loadCategories(callback)
        }
    }

    private fun loadCategories(callback: Callback) {
        val message = categoriesData.getString("message")
        val optionsArray = categoriesData.getJSONArray("options")
        val options = mutableListOf<String>()
        for (i in 0 until optionsArray.length()) {
            options.add(optionsArray.getString(i))
        }
        callback.onAiRespond(message, options)
    }
}
