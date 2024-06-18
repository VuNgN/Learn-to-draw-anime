package com.vungn.application.db.dao

import androidx.room.Dao
import com.example.ardrawsketch.db.dao.BaseDao
import com.vungn.application.model.data.entity.EntityDemo

@Dao
interface DemoDao : BaseDao<EntityDemo>