package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ColorDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hex") val hex: String,
    @ColumnInfo(name = "name") val name: String
) {
    companion object {
        val DEFAULT_COLORS = listOf(
            ColorDbModel(1, "#E57373", "mobile"),
            ColorDbModel(2, "#F06292", "home"),
            ColorDbModel(3, "#CE93D8", "work"),
            ColorDbModel(4, "#FF9800", "school"),
            ColorDbModel(5, "#FFEB3B", "iPhone"),
            ColorDbModel(6, "#9E9E9E", "Apple Watch"),
            ColorDbModel(7, "#BCAAA4", "fax"),
            ColorDbModel(8, "#FFFFFF", "Other"),
        )
        val DEFAULT_COLOR = DEFAULT_COLORS[0]
    }
}
