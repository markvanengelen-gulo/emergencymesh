package com.emergencymesh.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val messageId: String,
    val senderId: String,
    val senderNickname: String,
    val content: String,
    val timestamp: Long,
    val groupId: String?,
    val ttl: Int,
    val isLocal: Boolean
)
