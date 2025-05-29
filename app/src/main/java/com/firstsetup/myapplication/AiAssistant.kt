package com.firstsetup.myapplication

import android.content.Context
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class AiAssistant {

    interface Callback {
        fun onAiRespond(message: String)
    }

        fun askQuestion(context: Context, question: String, callback: Callback) {
            val url = "https://fluorescent-boiled-butter.glitch.me/ask-ai"

            val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)
            val cultivateurId = sharedPref.getInt("cultivateur_id", -1)

            val requestBody = JSONObject().apply {
                put("cultivateurId", cultivateurId)
                put("question", question)
            }

            val request = object : JsonObjectRequest(
                Method.POST, url, requestBody,
                { response ->
                    val answer = response.optString("answer", "Aucune réponse")
                    callback.onAiRespond(answer)
                },
                { error ->
                    callback.onAiRespond("Erreur réseau : ${error.message}")
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> {
                    return hashMapOf("Content-Type" to "application/json")
                }
            }

            Volley.newRequestQueue(context).add(request)
        }
    }

