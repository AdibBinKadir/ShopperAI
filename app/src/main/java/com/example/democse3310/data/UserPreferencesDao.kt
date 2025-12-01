package com.example.democse3310.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getUserPreferences(userId: String): UserPreferences?

    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getUserPreferencesFlow(userId: String): Flow<UserPreferences?>

    @Insert
    suspend fun insertUserPreferences(prefs: UserPreferences)

    @Update
    suspend fun updateUserPreferences(prefs: UserPreferences)

    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deleteUserPreferences(userId: String)
}
