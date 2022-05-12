package com.thoughtbot.expandablecheckrecyclerview.kt.models

import android.widget.ExpandableListView

class ExpandableListPosition private constructor() {

    var groupPos = 0

    var childPos = 0

    var flatListPos = 0

    var type = 0
    private fun resetState() {
        groupPos = 0
        childPos = 0
        flatListPos = 0
        type = 0
    }

    val packedPosition: Long
        get() = if (type == CHILD) {
            ExpandableListView.getPackedPositionForChild(groupPos, childPos)
        } else {
            ExpandableListView.getPackedPositionForGroup(groupPos)
        }

    fun recycle() {
        synchronized(sPool) {
            if (sPool.size < MAX_POOL_SIZE) {
                sPool.add(this)
            }
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as ExpandableListPosition
        if (groupPos != that.groupPos) return false
        if (childPos != that.childPos) return false
        return if (flatListPos != that.flatListPos) false else type == that.type
    }

    override fun hashCode(): Int {
        var result = groupPos
        result = 31 * result + childPos
        result = 31 * result + flatListPos
        result = 31 * result + type
        return result
    }

    override fun toString(): String {
        return "ExpandableListPosition{" +
                "groupPos=" + groupPos +
                ", childPos=" + childPos +
                ", flatListPos=" + flatListPos +
                ", type=" + type +
                '}'
    }

    companion object {
        private const val MAX_POOL_SIZE = 5
        private val sPool = ArrayList<ExpandableListPosition>(MAX_POOL_SIZE)

        /**
         * This data type represents a child position
         */
        const val CHILD = 1

        /**
         * This data type represents a group position
         */
        const val GROUP = 2
        fun obtainGroupPosition(groupPosition: Int): ExpandableListPosition {
            return obtain(GROUP, groupPosition, 0, 0)
        }

        fun obtainChildPosition(groupPosition: Int, childPosition: Int): ExpandableListPosition {
            return obtain(CHILD, groupPosition, childPosition, 0)
        }

        fun obtainPosition(packedPosition: Long): ExpandableListPosition? {
            if (packedPosition == ExpandableListView.PACKED_POSITION_VALUE_NULL) {
                return null
            }
            val elp = recycledOrCreate
            elp.groupPos = ExpandableListView.getPackedPositionGroup(packedPosition)
            if (ExpandableListView.getPackedPositionType(packedPosition) ==
                ExpandableListView.PACKED_POSITION_TYPE_CHILD
            ) {
                elp.type = CHILD
                elp.childPos = ExpandableListView.getPackedPositionChild(packedPosition)
            } else {
                elp.type = GROUP
            }
            return elp
        }

        fun obtain(
            type: Int, groupPos: Int, childPos: Int,
            flatListPos: Int
        ): ExpandableListPosition {
            val elp = recycledOrCreate
            elp.type = type
            elp.groupPos = groupPos
            elp.childPos = childPos
            elp.flatListPos = flatListPos
            return elp
        }

        private val recycledOrCreate: ExpandableListPosition
            get() {
                var elp: ExpandableListPosition
                synchronized(sPool) {
                    elp = if (sPool.size > 0) {
                        sPool.removeAt(0)
                    } else {
                        return ExpandableListPosition()
                    }
                }
                elp.resetState()
                return elp
            }
    }
}
