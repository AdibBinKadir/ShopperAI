package com.example.democse3310.repository

import com.example.democse3310.data.ChatMessageDao
import com.example.democse3310.data.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val chatDao: ChatMessageDao) {
    
    val allMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    
    fun getMessagesBySession(sessionId: String): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesBySession(sessionId)
    }
    
    suspend fun insertMessage(message: ChatMessageEntity) {
        chatDao.insertMessage(message)
    }
    
    suspend fun deleteAllMessages() {
        chatDao.deleteAllMessages()
    }
    
    suspend fun deleteSession(sessionId: String) {
        chatDao.deleteSession(sessionId)
    }
    
    suspend fun deleteMessage(message: ChatMessageEntity) {
        chatDao.deleteMessage(message)
    }
}
