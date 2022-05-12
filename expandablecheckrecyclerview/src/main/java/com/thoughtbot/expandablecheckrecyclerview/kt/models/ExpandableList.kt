package com.thoughtbot.expandablecheckrecyclerview.kt.models

class ExpandableList(var groups: List<ExpandableGroup<*>>) {

    var expandedGroupIndexes: BooleanArray = BooleanArray(groups.size)

    private fun numberOfVisibleItemsInGroup(group: Int): Int {
        return if (expandedGroupIndexes[group]) {
            groups[group].itemCount + 1
        } else {
            1
        }
    }

    val visibleItemCount: Int
        get() {
            var count = 0
            for (i in groups.indices) {
                count += numberOfVisibleItemsInGroup(i)
            }
            return count
        }

    fun getUnflattenedPosition(flPos: Int): ExpandableListPosition {
        var groupItemCount: Int
        var adapted = flPos
        for (i in groups.indices) {
            groupItemCount = numberOfVisibleItemsInGroup(i)
            if (adapted == 0) {
                return ExpandableListPosition.obtain(ExpandableListPosition.GROUP, i, -1, flPos)
            } else if (adapted < groupItemCount) {
                return ExpandableListPosition.obtain(
                    ExpandableListPosition.CHILD,
                    i,
                    adapted - 1,
                    flPos
                )
            }
            adapted -= groupItemCount
        }
        throw RuntimeException("Unknown state")
    }

    fun getFlattenedGroupIndex(listPosition: ExpandableListPosition): Int {
        val groupIndex: Int = listPosition.groupPos
        var runningTotal = 0
        for (i in 0 until groupIndex) {
            runningTotal += numberOfVisibleItemsInGroup(i)
        }
        return runningTotal
    }

    fun getFlattenedGroupIndex(groupIndex: Int): Int {
        var runningTotal = 0
        for (i in 0 until groupIndex) {
            runningTotal += numberOfVisibleItemsInGroup(i)
        }
        return runningTotal
    }

    fun getFlattenedGroupIndex(group: ExpandableGroup<*>?): Int {
        val groupIndex = groups.indexOf(group)
        var runningTotal = 0
        for (i in 0 until groupIndex) {
            runningTotal += numberOfVisibleItemsInGroup(i)
        }
        return runningTotal
    }

    fun getFlattenedChildIndex(packedPosition: Long): Int {
        val listPosition: ExpandableListPosition =
            ExpandableListPosition.obtainPosition(packedPosition)
                ?: throw RuntimeException("Wrong list position $packedPosition")
        return getFlattenedChildIndex(listPosition)
    }

    fun getFlattenedChildIndex(listPosition: ExpandableListPosition): Int {
        val groupIndex: Int = listPosition.groupPos
        val childIndex: Int = listPosition.childPos
        var runningTotal = 0
        for (i in 0 until groupIndex) {
            runningTotal += numberOfVisibleItemsInGroup(i)
        }
        return runningTotal + childIndex + 1
    }

    fun getFlattenedChildIndex(groupIndex: Int, childIndex: Int): Int {
        var runningTotal = 0
        for (i in 0 until groupIndex) {
            runningTotal += numberOfVisibleItemsInGroup(i)
        }
        return runningTotal + childIndex + 1
    }

    fun getFlattenedFirstChildIndex(groupIndex: Int): Int {
        return getFlattenedGroupIndex(groupIndex) + 1
    }

    fun getFlattenedFirstChildIndex(listPosition: ExpandableListPosition): Int {
        return getFlattenedGroupIndex(listPosition) + 1
    }

    fun getExpandableGroupItemCount(listPosition: ExpandableListPosition): Int {
        return groups[listPosition.groupPos].itemCount
    }

    fun getExpandableGroup(listPosition: ExpandableListPosition): ExpandableGroup<*> {
        return groups[listPosition.groupPos]
    }

    init {
        for (i in groups.indices) {
            expandedGroupIndexes[i] = false
        }
    }
}