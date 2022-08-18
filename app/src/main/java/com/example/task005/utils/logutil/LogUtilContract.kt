package com.example.task005.utils.logutil

interface LogUtilContract {
    fun logged(message: String, logLevel: LogLevel, logTag: String)
}