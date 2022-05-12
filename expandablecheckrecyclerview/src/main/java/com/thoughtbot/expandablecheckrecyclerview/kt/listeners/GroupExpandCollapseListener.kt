package com.thoughtbot.expandablecheckrecyclerview.kt.listeners

import co.uk.rbsg.mobile.android.view.expandableadapter.models.ExpandableGroup

interface GroupExpandCollapseListener {

    fun onGroupExpanded(group: ExpandableGroup<*>)

    fun onGroupCollapsed(group: ExpandableGroup<*>)
}