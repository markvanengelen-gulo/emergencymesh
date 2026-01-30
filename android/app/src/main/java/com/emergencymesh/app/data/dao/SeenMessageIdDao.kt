package com.emergencymesh.app.data.dao

import androidx.room.*
import com.emergencymesh.app.data.entity.SeenMessageId

@Dao
interface SeenMessageIdDao {
    @Query("SELECT * FROM seen_message_ids WHERE messageId = :messageId")
    suspend fun getSeenMessageId(messageId: String): SeenMessageId?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeenMessageId(seenMessageId: SeenMessageId)

    @Query("DELETE FROM seen_message_ids WHERE firstSeen < :threshold")
    suspend fun deleteOldSeenMessageIds(threshold: Long)

    @Query("SELECT COUNT(*) FROM seen_message_ids")
    suspend fun getSeenMessageCount(): Int
}
