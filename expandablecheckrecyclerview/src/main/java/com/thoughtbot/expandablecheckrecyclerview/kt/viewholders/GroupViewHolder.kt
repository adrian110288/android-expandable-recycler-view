package co.uk.rbsg.mobile.android.view.expandableadapter.kt.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.uk.rbsg.mobile.android.view.expandableadapter.listeners.OnGroupClickListener

abstract class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener {

    var listener: OnGroupClickListener? = null

    override fun onClick(v: View) {
        listener?.onGroupClick(adapterPosition)
    }

    fun expand() {}

    fun collapse() {}

    init {
        itemView.setOnClickListener(this)
    }
}