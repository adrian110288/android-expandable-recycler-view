package com.thoughtbot.expandablecheckrecyclerview.kt.models

open class ExpandableGroup<T>(open val title: String, open val items: List<T>) {

    val itemCount: Int
        get() = items.size
}