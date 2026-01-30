package com.emergencymesh.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ConnectionStatus {
    DISCOVERED,
    CONNECTING,
    CONNECTED,
    DISCONNECTED
}

@Entity(tableName = "peers")
data class Peer(
    @PrimaryKey val deviceId: String,
    val nickname: String,
    val lastSeen: Long,
    val connectionStatus: ConnectionStatus,
    val groupId: String?
)
