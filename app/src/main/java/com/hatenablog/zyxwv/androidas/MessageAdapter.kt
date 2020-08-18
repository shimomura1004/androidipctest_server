package com.hatenablog.zyxwv.androidas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import coil.api.load

class MessageAdapter(private val context: Context) : BaseAdapter() {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var dataSource = listOf<ASMessage>()

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val message = dataSource[p0]
        val view = inflater.inflate(R.layout.message_item, p2, false)
        view.findViewById<TextView>(R.id.body).text = message.body
        view.findViewById<TextView>(R.id.name).text = message.name
        view.findViewById<ImageView>(R.id.icon).load(message.profile_image_url)
        return view
    }

    override fun getItem(p0: Int): Any {
        return dataSource[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    fun setDataSource(_dataSource: List<ASMessage>) {
        dataSource = _dataSource
        notifyDataSetChanged()
    }
}
