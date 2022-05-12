package com.thoughtbot.expandablecheckrecyclerview.kt

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.expandablerecyclerview.listeners.ExpandCollapseListener
import co.uk.rbsg.mobile.android.view.expandableadapter.listeners.GroupExpandCollapseListener
import co.uk.rbsg.mobile.android.view.expandableadapter.listeners.OnChildClickListener
import co.uk.rbsg.mobile.android.view.expandableadapter.listeners.OnGroupClickListener
import co.uk.rbsg.mobile.android.view.expandableadapter.models.ExpandableGroup
import co.uk.rbsg.mobile.android.view.expandableadapter.models.ExpandableList
import co.uk.rbsg.mobile.android.view.expandableadapter.models.ExpandableListPosition
import co.uk.rbsg.mobile.android.view.expandableadapter.viewholders.ChildViewHolder
import co.uk.rbsg.mobile.android.view.expandableadapter.viewholders.GroupViewHolder

abstract class ExpandableRecyclerViewAdapter<GVH : GroupViewHolder, CVH : ChildViewHolder<T2>, T : ExpandableGroup<T2>, T2>(
    groups: List<T>,
    private val singleExpanded: Boolean = true,
    private val onChildClicked: OnChildClickListener<T2>
) :
    RecyclerView.Adapter<ViewHolder>(), ExpandCollapseListener, OnGroupClickListener {

    private var expandableList: ExpandableList

    private val expandCollapseController: ExpandCollapseController

    private var groupClickListener: OnGroupClickListener? = null

    private var expandCollapseListener: GroupExpandCollapseListener? = null

    private val groups: List<ExpandableGroup<*>>
        get() = expandableList.groups

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            ExpandableListPosition.GROUP -> {
                val gvh = onCreateGroupViewHolder(parent, viewType)
                gvh.listener = this
                gvh
            }
            ExpandableListPosition.CHILD ->
                onCreateChildViewHolder(parent, viewType).apply {
                    this.listener = onChildClicked
                }
            else -> throw IllegalArgumentException("viewType is not valid")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listPos = expandableList.getUnflattenedPosition(position)
        val group = expandableList.getExpandableGroup(listPos) as T
        when (listPos.type) {
            ExpandableListPosition.GROUP -> {
                onBindGroupViewHolder(holder as GVH, position, group)
                if (isGroupExpanded(group)) {
                    holder.expand()
                } else {
                    holder.collapse()
                }
            }
            ExpandableListPosition.CHILD -> onBindChildViewHolder(
                holder as CVH,
                position,
                group,
                listPos.childPos
            )
        }
    }

    override fun getItemCount(): Int {
        return expandableList.visibleItemCount
    }

    override fun getItemViewType(position: Int): Int {
        return expandableList.getUnflattenedPosition(position).type
    }

    override fun onGroupExpanded(positionStart: Int, itemCount: Int) {
        //update header
        val headerPosition = positionStart - 1
        notifyItemChanged(headerPosition)

        // only insert if there items to insert
        if (itemCount > 0) {
            notifyItemRangeInserted(positionStart, itemCount)

            val groupIndex = expandableList.getUnflattenedPosition(positionStart).groupPos

            if (singleExpanded) {

                val currentExpandedGroup = groups[groupIndex]
                groups.forEach { group ->
                    if (group != currentExpandedGroup && expandCollapseController.isGroupExpanded(
                            group
                        )
                    ) {
                        expandCollapseController.collapseGroup(group)
                    }
                }
            }

            expandCollapseListener?.onGroupExpanded(groups[groupIndex])
        }
    }

    override fun onGroupCollapsed(positionStart: Int, itemCount: Int) {
        //update header
        val headerPosition = positionStart - 1
        notifyItemChanged(headerPosition)

        // only remote if there items to remove
        if (itemCount > 0) {
            notifyItemRangeRemoved(positionStart, itemCount)
            if (expandCollapseListener != null) {
                //minus one to return the position of the header, not first child
                val groupIndex = expandableList.getUnflattenedPosition(positionStart - 1).groupPos
                expandCollapseListener!!.onGroupCollapsed(groups[groupIndex])
            }
        }
    }

    override fun onGroupClick(flatPos: Int): Boolean {
        groupClickListener?.onGroupClick(flatPos)
        return expandCollapseController.toggleGroup(flatPos)
    }

    fun toggleGroup(flatPos: Int): Boolean {
        return expandCollapseController.toggleGroup(flatPos)
    }

    fun toggleGroup(group: ExpandableGroup<*>): Boolean {
        return expandCollapseController.toggleGroup(group)
    }

    fun expandGroup(group: ExpandableGroup<*>) {
        expandCollapseController.expandGroup(group)
    }

    fun collapseGroup(group: ExpandableGroup<*>) {
        expandCollapseController.collapseGroup(group)
    }

    fun isGroupExpanded(flatPos: Int): Boolean {
        return expandCollapseController.isGroupExpanded(flatPos)
    }

    fun isGroupExpanded(group: ExpandableGroup<*>): Boolean {
        return expandCollapseController.isGroupExpanded(group)
    }

    fun setOnGroupClickListener(listener: OnGroupClickListener) {
        groupClickListener = listener
    }

    fun setOnGroupExpandCollapseListener(listener: GroupExpandCollapseListener) {
        expandCollapseListener = listener
    }

    abstract fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): GVH

    abstract fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): CVH

    abstract fun onBindChildViewHolder(holder: CVH, flatPosition: Int, group: T, childIndex: Int)

    abstract fun onBindGroupViewHolder(holder: GVH, flatPosition: Int, group: T)

    init {
        expandableList = ExpandableList(groups)
        expandCollapseController = ExpandCollapseController(expandableList, this)
    }
}