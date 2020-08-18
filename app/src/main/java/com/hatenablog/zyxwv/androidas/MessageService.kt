package com.hatenablog.zyxwv.androidas

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result

class MessageService : Service() {
    val API_SERVER = "https://asakusa-satellite.herokuapp.com"
    val LIST_ENDPOINT = "/api/v1/message/list.json"
    val API_KEY = ""
    val ROOM_ID = ""

    val mMessages = mutableListOf<ASMessage>()

    val mStub = object : IASMessageService.Stub() {
        override fun getLatestMessages(): Array<out ASMessage>? {
            getLatestMessagesFromServer()
            return mMessages.toTypedArray()
        }

        override fun registerCallback(callback: IMessageCallback?) {
            TODO("Not yet implemented")
        }

        override fun unregisterCallback(callback: IMessageCallback?) {
            TODO("Not yet implemented")
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("SERVICE", "service bound")
        return mStub
    }

    fun getLatestMessagesFromServer(number: Int = 10) {
        val url = API_SERVER + LIST_ENDPOINT
        val parameters = listOf("api_key" to API_KEY, "room_id" to ROOM_ID)

        val asyncTask = Fuel.get(url, parameters).responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    println("FAILURE!")
                }
                is Result.Success -> {
                    mMessages.clear()

                    val json = result.value.array()
                    val messagesArrayList = ArrayList<com.hatenablog.zyxwv.androidas.ASMessage>()
                    for (i in 0 until json.length()) {
                        val message = json.getJSONObject(i)
                        val body = message.getString("body")
                        val profile_image_url = message.getString("profile_image_url")
                        val name = message.getString("name")
                        mMessages.add(ASMessage(body, profile_image_url, name))
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("MessageService", "Created!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MessageService", "StartCommand!")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("MessageService", "UnBind!")
        return super.onUnbind(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d("MessageService", "Destroyed!")
    }
}