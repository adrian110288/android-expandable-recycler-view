package com.thoughtbot.expandablecheckrecyclerview.kt

import com.example.expandablerecyclerview.listeners.ExpandCollapseListener
import co.uk.rbsg.mobile.android.view.expandableadapter.models.ExpandableGroup
import co.uk.rbsg.mobile.android.view.expandableadapter.models.ExpandableList
import co.uk.rbsg.mobile.android.view.expandableadapter.models.ExpandableListPosition

class ExpandCollapseController(
    private val expandableList: ExpandableList,
    private val listener: ExpandCollapseListener?
) {

    private fun collapseGroup(listPosition: ExpandableListPosition) {
        expandableList.expandedGroupIndexes[listPosition.groupPos] = false
        listener?.onGroupCollapsed(
            expandableList.getFlattenedGroupIndex(listPosition) + 1,
            expandableList.groups[listPosition.groupPos].itemCount
        )
    }

    private fun expandGroup(listPosition: ExpandableListPosition) {
        expandableList.expandedGroupIndexes[listPosition.groupPos] = true
        listener?.onGroupExpanded(
            expandableList.getFlattenedGroupIndex(listPosition) + 1,
            expandableList.groups[listPosition.groupPos].itemCount
        )
    }

    fun isGroupExpanded(group: ExpandableGroup<*>?): Boolean {
        val groupIndex = expandableList.groups.indexOf(group)
        return expandableList.expandedGroupIndexes[groupIndex]
    }

    fun isGroupExpanded(flatPos: Int): Boolean {
        val listPosition = expandableList.getUnflattenedPosition(flatPos)
        return expandableList.expandedGroupIndexes[listPosition.groupPos]
    }

    fun toggleGroup(flatPos: Int): Boolean {
        val listPos = expandableList.getUnflattenedPosition(flatPos)
        val expanded = expandableList.expandedGroupIndexes[listPos.groupPos]
        if (expanded) {
            collapseGroup(listPos)
        } else {
            expandGroup(listPos)
        }
        return expanded
    }

    fun toggleGroup(group: ExpandableGroup<*>?): Boolean {
        val listPos =
            expandableList.getUnflattenedPosition(expandableList.getFlattenedGroupIndex(group))
        val expanded = expandableList.expandedGroupIndexes[listPos.groupPos]
        if (expanded) {
            collapseGroup(listPos)
        } else {
            expandGroup(listPos)
        }
        return expanded
    }

    fun expandGroup(group: ExpandableGroup<*>?) {
        val listPos =
            expandableList.getUnflattenedPosition(expandableList.getFlattenedGroupIndex(group))
        val isExpanded = expandableList.expandedGroupIndexes[listPos.groupPos]
        // No-op on repeating calls
        if (!isExpanded) {
            expandGroup(listPos)
        }
    }

    fun expandGroup(flatPos: Int) {
        val listPos = expandableList.getUnflattenedPosition(flatPos)
        val isExpanded = expandableList.expandedGroupIndexes[listPos.groupPos]
        // No-op on repeating calls
        if (!isExpanded) {
            expandGroup(listPos)
        }
    }

    fun collapseGroup(group: ExpandableGroup<*>?) {
        val listPos =
            expandableList.getUnflattenedPosition(expandableList.getFlattenedGroupIndex(group))
        val isExpanded = expandableList.expandedGroupIndexes[listPos.groupPos]
        // No-op on repeating calls
        if (isExpanded) {
            collapseGroup(listPos)
        }
    }

    fun collapseGroup(flatPos: Int) {
        val listPos = expandableList.getUnflattenedPosition(flatPos)
        val isExpanded = expandableList.expandedGroupIndexes[listPos.groupPos]
        // No-op on repeating calls
        if (isExpanded) {
            collapseGroup(listPos)
        }
    }
}