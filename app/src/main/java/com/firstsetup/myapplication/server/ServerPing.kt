// ServerPing.kt
package com.firstsetup.myapplication

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class ServerPing {

    fun pingServer(context: Context) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://fluorescent-boiled-butter.glitch.me/"

        val pingRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                // Handle success
            },
            { error ->
                // Handle error
            }
        )

        queue.add(pingRequest)
    }
}
