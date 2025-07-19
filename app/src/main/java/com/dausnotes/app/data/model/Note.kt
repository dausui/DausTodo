package com.dausnotes.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "General",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val CATEGORY_GENERAL = "General"
        const val CATEGORY_WORK = "Work"
        const val CATEGORY_PERSONAL = "Personal"
        const val CATEGORY_STUDY = "Study"
        const val CATEGORY_IDEAS = "Ideas"
        
        fun getDefaultCategories(): List<String> {
            return listOf(
                CATEGORY_GENERAL,
                CATEGORY_WORK,
                CATEGORY_PERSONAL,
                CATEGORY_STUDY,
                CATEGORY_IDEAS
            )
        }
    }
}