package com.emergencymesh.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seen_message_ids")
data class SeenMessageId(
    @PrimaryKey val messageId: String,
    val firstSeen: Long
)
