package com.emergencymesh.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emergencymesh.app.data.dao.MessageDao
import com.emergencymesh.app.data.dao.PeerDao
import com.emergencymesh.app.data.dao.SeenMessageIdDao
import com.emergencymesh.app.data.entity.Message
import com.emergencymesh.app.data.entity.Peer
import com.emergencymesh.app.data.entity.SeenMessageId

@Database(
    entities = [Peer::class, Message::class, SeenMessageId::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun peerDao(): PeerDao
    abstract fun messageDao(): MessageDao
    abstract fun seenMessageIdDao(): SeenMessageIdDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "emergency_mesh_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
