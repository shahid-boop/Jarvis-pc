package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_log")
data class AuditEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String,
    val target: String,
    val status: String, // "Executed", "Confirmed", "Cancelled", "Blocked"
    val timestamp: Long = System.currentTimeMillis(),
    val details: String = ""
)
