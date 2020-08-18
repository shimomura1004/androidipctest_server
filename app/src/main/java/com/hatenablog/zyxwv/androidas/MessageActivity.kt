package com.hatenablog.zyxwv.androidas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val message = intent.getParcelableExtra<ASMessage>("Message")
        name.text = message?.name
        body.text = message?.body
        message_icon.load(message?.profile_image_url)
    }
}