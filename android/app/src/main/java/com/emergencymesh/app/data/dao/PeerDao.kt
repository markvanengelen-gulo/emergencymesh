package com.emergencymesh.app.data.dao

import androidx.room.*
import com.emergencymesh.app.data.entity.Peer
import kotlinx.coroutines.flow.Flow

@Dao
interface PeerDao {
    @Query("SELECT * FROM peers ORDER BY lastSeen DESC")
    fun getAllPeers(): Flow<List<Peer>>

    @Query("SELECT * FROM peers WHERE deviceId = :deviceId")
    suspend fun getPeerById(deviceId: String): Peer?

    @Query("SELECT COUNT(*) FROM peers WHERE connectionStatus = 'CONNECTED'")
    fun getConnectedPeerCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeer(peer: Peer)

    @Update
    suspend fun updatePeer(peer: Peer)

    @Delete
    suspend fun deletePeer(peer: Peer)

    @Query("DELETE FROM peers WHERE lastSeen < :threshold")
    suspend fun deleteOldPeers(threshold: Long)
}
