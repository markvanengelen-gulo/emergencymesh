package com.emergencymesh.app.data.database

import androidx.room.TypeConverter
import com.emergencymesh.app.data.entity.ConnectionStatus

class Converters {
    @TypeConverter
    fun fromConnectionStatus(value: ConnectionStatus): String {
        return value.name
    }

    @TypeConverter
    fun toConnectionStatus(value: String): ConnectionStatus {
        return ConnectionStatus.valueOf(value)
    }
}
