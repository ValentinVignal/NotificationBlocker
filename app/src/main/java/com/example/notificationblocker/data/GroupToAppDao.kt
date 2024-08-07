package com.example.notificationblocker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupToAppDao {
    @Query("SELECT app_id FROM group_app WHERE group_id = :groupId")
    fun getAllAppIds(groupId: Int): Flow<List<String>>

    @Upsert
    suspend fun upsert(groupToApp: GroupToApp)

    @Delete
    suspend fun delete(groupToApp: GroupToApp)
}
