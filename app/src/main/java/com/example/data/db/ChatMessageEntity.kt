package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "user", "jarvis", "system"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "en", // "en", "ur", "ur-roman"
    val actionType: String? = null, // "pc_command", "code_gen", "file_action", "workflow", "browser"
    val executionStatus: String = "none" // "none", "pending", "success", "failed"
)
