package com.example.task005.utils

import com.example.task005.models.core.Result


interface SuperRepositoryCallback<in T> {
    fun success(result: T) {}
    fun noContent() {}
    fun notAuthorised() {}
    fun error(result: Result) {}
    fun pinNotCorrect() {}
    fun moreAuthRequired() {}
}