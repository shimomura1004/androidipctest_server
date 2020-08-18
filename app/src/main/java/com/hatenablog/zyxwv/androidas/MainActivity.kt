package com.hatenablog.zyxwv.androidas

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.transition.Explode
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import kotlinx.android.synthetic.main.activity_main.*
import java.security.KeyStore
import java.security.Security

class MainActivity : AppCompatActivity() {
    val API_SERVER = "https://asakusa-satellite.herokuapp.com"
    val LIST_ENDPOINT = "/api/v1/message/list.json"
    val API_KEY = "Bdi6ZUciDu1fCDn8ao007N"
    val ROOM_ID = "4e361518a1ea6d0001000045"

    // remote service connection
    // これが AIDL で定義したサービスの実態
    var mService : IASMessageService? = null
    // bind するときの使う無名クラス
    val mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            mService = null
        }

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            // binder による通信が確立されたときに得られたバインダをスタブに渡してラップしてもらう
            mService = IASMessageService.Stub.asInterface(p1)
        }
    }

    val message_bodies = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = Explode()

        setContentView(R.layout.activity_main)

        button.setOnClickListener(View.OnClickListener {
//            getLatestMessages()
            // メッセージ受信サービスから AIDL 経由でメッセージを取得
            val messages = mService?.latestMessages
            for(message in messages!!) {
                Log.d("MESSAGE", message.toString())
            }
        })
        button2.setOnClickListener(View.OnClickListener {
            // ContentProvider で(同一プロセスから)メッセージを取得
            val uri = Uri.parse("content://com.hatenablog.zyxwv.androidas.message_provider")
            val projection = arrayOf("name", "body")
            val selection = null
            val selectionArgs = null
            val sortOrder = ""
            val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val name = cursor.getString(0)
                    val body = cursor.getString(1)
                    Log.d("AndroidAS", name)
                    Log.d("AndroidAS", body)
                }
            }
        })

        val messageAdapter = MessageAdapter(this)
        messages.adapter = messageAdapter
        messages.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(this, MessageActivity::class.java).apply {
                putExtra("Message", messageAdapter.getItem(i) as ASMessage)
            }
            val offset = messages.firstVisiblePosition - messages.headerViewsCount
            val options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    messages.getChildAt(i - offset).findViewById<View>(R.id.icon),
                    "icon")
            startActivity(intent, options.toBundle())
        }

        getLatestMessages()

//        // keystore
//        val keystore = KeyStore.getInstance("AndroidKeyStore")
//        keystore.load(null)

        // start service process
        val intent = Intent(this, MessageService::class.java)
        bindService(Intent(intent), mServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mService != null) {
            unbindService(mServiceConnection)
        }
    }

    fun getLatestMessages(number: Int = 10) {
        Toast.makeText(this, "Getting messages", Toast.LENGTH_SHORT).show()

        val url = API_SERVER + LIST_ENDPOINT
        val parameters = listOf("api_key" to API_KEY, "room_id" to ROOM_ID)

        val asyncTask = Fuel.get(url, parameters).responseJson { request, response, result ->
            when (result) {
                is Result.Failure -> {
                    println("FAILURE!")
                }
                is Result.Success -> {
                    val json = result.value.array()
                    val messagesArrayList = ArrayList<ASMessage>()
                    for (i in 0 until json.length()) {
                        val message = json.getJSONObject(i)
                        val body = message.getString("body")
                        val profile_image_url = message.getString("profile_image_url")
                        val name = message.getString("name")
                        messagesArrayList.add(
                                ASMessage(
                                        body,
                                        profile_image_url,
                                        name
                                )
                        )
                    }
                    (messages.adapter as MessageAdapter).setDataSource(messagesArrayList)

                    // insert data for contentprovider
                    val values = ContentValues()
                    for(message in messagesArrayList) {
                        values.put("name", message.name)
                        values.put("body", message.body)
                        val uri = Uri.parse("content://com.hatenablog.zyxwv.androidas.message_provider")
                        contentResolver.insert(uri, values)
                    }
                }
            }
        }
    }
}