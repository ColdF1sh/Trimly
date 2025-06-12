package com.example.trimly.ui.admin.masters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.example.trimly.data.MasterSession
import com.example.trimly.data.Master

class SessionsGroupedAdapter(
    private var groupedSessions: Map<String, List<MasterSession>>,
    private var masterMap: Map<Int, Master>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Item>()

    fun updateItems(newGrouped: Map<String, List<MasterSession>>) {
        items.clear()
        newGrouped.toSortedMap().forEach { (date, sessions) ->
            items.add(Item.Header(date))
            items.addAll(sessions.map { Item.Session(it) })
        }
        notifyDataSetChanged()
    }

    fun updateMasterMap(newMap: Map<Int, Master>) {
        masterMap = newMap
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Item.Header -> 0
        is Item.Session -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false)
            SessionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is Item.Header -> (holder as HeaderViewHolder).bind(item.date)
            is Item.Session -> (holder as SessionViewHolder).bind(item.session, masterMap)
        }
    }

    override fun getItemCount() = items.size

    sealed class Item {
        data class Header(val date: String) : Item()
        data class Session(val session: MasterSession) : Item()
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDate: TextView = view.findViewById(R.id.tvSessionDate)
        fun bind(date: String) {
            tvDate.text = date
        }
    }

    class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTime: TextView = view.findViewById(R.id.tvSessionTime)
        private val tvStatus: TextView = view.findViewById(R.id.tvSessionStatus)
        private val tvMasterName: TextView = view.findViewById(R.id.tvMasterName)
        fun bind(session: MasterSession, masterMap: Map<Int, Master>) {
            tvTime.text = "${session.startTime} - ${session.endTime}"
            tvStatus.text = session.status.toString()
            val master = masterMap[session.masterId]
            tvMasterName.text = if (master != null) {
                "Майстер: ${master.firstName}${if (master.lastName != null) " ${master.lastName}" else ""}"
            } else {
                "Майстер: ?"
            }
        }
    }
} 