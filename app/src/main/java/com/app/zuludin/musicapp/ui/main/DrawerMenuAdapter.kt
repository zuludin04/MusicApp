package com.app.zuludin.musicapp.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.zuludin.musicapp.R
import kotlinx.android.synthetic.main.item_menu_drawer.view.*

class DrawerMenuAdapter(
    private val items: List<DrawerMenu>,
    private val listener: (Int) -> Unit
) : RecyclerView.Adapter<DrawerMenuViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerMenuViewHolder =
        DrawerMenuViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu_drawer, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DrawerMenuViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }
}

class DrawerMenuViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(menu: DrawerMenu, listener: (Int) -> Unit) {
        itemView.drawer_image.setImageResource(menu.icon)
        itemView.drawer_title.text = menu.title

        itemView.setOnClickListener { listener(adapterPosition) }
    }
}