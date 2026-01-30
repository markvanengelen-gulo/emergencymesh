package com.emergencymesh.app.data.dao

import androidx.room.*
import com.emergencymesh.app.data.entity.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE groupId = :groupId ORDER BY timestamp DESC")
    fun getMessagesByGroup(groupId: String?): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE messageId = :messageId")
    suspend fun getMessageById(messageId: String): Message?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("DELETE FROM messages WHERE timestamp < :threshold")
    suspend fun deleteOldMessages(threshold: Long)

    @Query("SELECT COUNT(*) FROM messages")
    fun getMessageCount(): Flow<Int>
}
