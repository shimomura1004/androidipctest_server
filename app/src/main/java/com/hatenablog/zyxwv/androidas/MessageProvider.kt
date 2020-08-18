package com.hatenablog.zyxwv.androidas

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log

class MessageProvider : ContentProvider() {
    var messages = ArrayList<Pair<String, String>>()

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        Log.d("MessageProvider", p0.toString())
        val values = ContentValues()
        val name = p1?.getAsString("name")
        val body = p1?.getAsString("body")

        messages.add(Pair<String, String>(name!!, body!!))

        return p0
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        Log.d("MessageProvider", p0.toString())
        Log.d("MessageProvider", p1.toString())

        val matrixCursor = MatrixCursor(arrayOf("name", "body"))
        for (message in messages) {
            matrixCursor.addRow(arrayOf(message.first, message.second))
        }

        return matrixCursor
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun getType(p0: Uri): String? {
        TODO("Not yet implemented")
    }
}