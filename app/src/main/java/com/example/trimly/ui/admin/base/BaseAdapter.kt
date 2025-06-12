package com.example.trimly.ui.admin.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseAdapter.ViewHolder<T>>() {

    protected var items: List<T> = listOf()

    fun updateItems(newItems: List<T>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val view = LayoutInflater.from(parent.context)
            .inflate(getItemLayoutId(), parent, false)
        return createViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    abstract fun getItemLayoutId(): Int
    abstract fun createViewHolder(view: View): ViewHolder<T>

    abstract class ViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }
} 