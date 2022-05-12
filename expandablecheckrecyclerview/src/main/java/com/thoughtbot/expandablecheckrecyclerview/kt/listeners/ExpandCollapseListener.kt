package com.thoughtbot.expandablecheckrecyclerview.kt.listeners

interface ExpandCollapseListener {

    fun onGroupExpanded(positionStart: Int, itemCount: Int)

    fun onGroupCollapsed(positionStart: Int, itemCount: Int)
}