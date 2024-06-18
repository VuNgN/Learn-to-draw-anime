package com.vungn.application.model.data.entity

import androidx.room.PrimaryKey


@androidx.room.Entity(tableName = "entity_demo")
data class EntityDemo(@PrimaryKey val id: Int)