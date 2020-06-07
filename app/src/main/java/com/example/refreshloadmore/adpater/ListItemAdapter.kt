package com.example.refreshloadmore.adpater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.refreshloadmore.R
import kotlinx.android.synthetic.main.list_item.view.*

/**
 * Goach All Rights Reserved
 *User: Goach
 *Email: goach0728@gmail.com
 *Des:列表
 */
class ListItemAdapter(private val list: MutableList<String>) :
    RecyclerView.Adapter<ListItemAdapter.ListItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ListItemHolder(rootView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        holder.itemView.tvTitle.text = list[position]
    }

    inner class ListItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}