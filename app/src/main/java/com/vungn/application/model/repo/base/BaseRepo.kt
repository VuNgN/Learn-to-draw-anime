package com.vungn.application.model.repo.base

import com.vungn.application.db.AppDatabase
import com.vungn.application.server.ServiceGenerator

abstract class BaseRepo {
    /**
     * Service generator
     */
    protected val service = ServiceGenerator.getApiService()

    /**
     * Database instance
     */
    protected val database = AppDatabase.getInstance()
}