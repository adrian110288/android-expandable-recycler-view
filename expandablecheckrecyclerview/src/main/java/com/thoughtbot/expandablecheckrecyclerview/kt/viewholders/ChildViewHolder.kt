package com.thoughtbot.expandablecheckrecyclerview.kt.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.uk.rbsg.mobile.android.view.expandableadapter.listeners.OnChildClickListener

abstract class ChildViewHolder<T>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    var listener: OnChildClickListener<T>? = null
}