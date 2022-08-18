package com.example.task005.utils.logutil

import android.util.Log

/**
 * created by SAGAR KUMAR NAYAK
 * this is a class for control all your log printing.
 * this can print log of any length.
 * with several log types.
 * can block logs in release apk.
 *
 *
 * the exposed methods are
 * [.logV]
 * [.logV]
 * [.logD]
 * [.logD]
 * [.logI]
 * [.logI]
 * [.logW]
 * [.logW]
 * [.logE]
 * [.logE]
 */
@Suppress("unused")
class LogUtil(builder: Builder) {

    /**
     * This is the custom log tag for the application. this tag will be used for whole application as
     * the log tag. you can change this as per your application.
     * unless you use a method that has a custom tag argument this tag will be used.
     * the methods those who will use this tag as log tag are
     * [.logV][.logD][.logI][.logW]
     * [.logE]
     * and the methods that will use the custom tag provided by the user are
     * [.logV][.logD][.logI]
     * [.logW][.logE]
     */
    private val LOG_TAG: String

    /**
     * by default the class will not print any logs in release apk. and if you want to print logs
     * even in release apk then just make this value as TRUE. otherwise make it FALSE.
     */
    private val SHOW_LOG: Boolean

    /**
     * this is the max length of the log after which the longer logs are split. log of any length
     * will be split into parts and printed.
     */
    private val MAX_LOG_LENGTH = 1000

    init {
        this.LOG_TAG = builder.customLogTag
        this.SHOW_LOG = builder.shouldShowLog
    }

    /**
     * method to print a log with V TAG with custom tag
     *
     * @param message message to print
     */
    fun logV(message: String) {
        newLog(message, LogLevel.VERBOSE)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.v(LOG_TAG, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with D TAG with custom tag
     *
     * @param message message to print
     */
    fun logD(message: String) {
        newLog(message, LogLevel.DEBUG)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.d(LOG_TAG, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with I TAG with custom tag
     *
     * @param message message to print
     */
    fun logI(message: String) {
        newLog(message, LogLevel.INFO)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.i(LOG_TAG, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with W TAG with custom tag
     *
     * @param message message to print
     */
    fun logW(message: String) {
        newLog(message, LogLevel.WARN)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.w(LOG_TAG, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with E TAG with custom tag
     *
     * @param message message to print
     */
    fun logE(message: String) {
        newLog(message, LogLevel.ERROR)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.e(LOG_TAG, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with V TAG with tag provided by user
     *
     * @param tag     Custom tag for log
     * @param message log message
     */
    fun logV(tag: String, message: String) {
        newLog(message, LogLevel.VERBOSE, tag)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.v(tag, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with D TAG with tag provided by user
     *
     * @param tag     Custom tag for log
     * @param message log message
     */
    fun logD(tag: String, message: String) {
        newLog(message, LogLevel.DEBUG, tag)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.d(tag, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with I TAG with tag provided by user
     *
     * @param tag     Custom tag for log
     * @param message log message
     */
    fun logI(tag: String, message: String) {
        newLog(message, LogLevel.INFO, tag)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.i(tag, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with W TAG with tag provided by user
     *
     * @param tag     Custom tag for log
     * @param message log message
     */
    fun logW(tag: String, message: String) {
        newLog(message, LogLevel.WARN, tag)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.w(tag, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to print a log with E TAG with tag provided by user
     *
     * @param tag     Custom tag for log
     * @param message log message
     */
    fun logE(tag: String, message: String) {
        newLog(message, LogLevel.ERROR, tag)
        if (!SHOW_LOG)
            return
        for (i in 0..message.length / MAX_LOG_LENGTH) {
            val start = i * MAX_LOG_LENGTH
            var end = (i + 1) * MAX_LOG_LENGTH
            end = if (end > message.length) message.length else end
            Log.e(tag, getFormattedString(message, i, start, end))
        }
    }

    /**
     * method to get the split log message with formatted string added.
     * the longer logs will be divided into splits of and passed into this
     * method. this method will add a identifier to the message to that it can be identified that
     * this is the part of previous log.
     *
     * @param msg   message to print
     * @param index index of split
     * @param start starting point of the split
     * @param end   end of split
     * @return formatted log message to print
     */
    private fun getFormattedString(msg: String, index: Int, start: Int, end: Int): String {
        return if (index > 0)
            ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" +
                    "Part " +
                    (index + 1) +
                    " of previous Log " +
                    "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" +
                    "\n" +
                    msg.substring(start, end)
        else
            msg.substring(start, end)
    }

    /**
     * Builder class for LogUtil.
     */
    class Builder {
        var customLogTag = "LogUtil_TAG"
        var shouldShowLog = false

        fun setCustomLogTag(customLogTag: String): Builder {
            this.customLogTag = customLogTag
            return this
        }

        fun setShouldHideLog(shouldShowLog: Boolean): Builder {
            this.shouldShowLog = shouldShowLog
            return this
        }

        fun build(): LogUtil {
            return LogUtil(this)
        }
    }

    private lateinit var logUtilContract: LogUtilContract

    fun registerForCallback(logUtilContract: LogUtilContract) {
        this.logUtilContract = logUtilContract
    }

    private fun newLog(message: String, logLevel: LogLevel, tag: String? = null) {
        if (this::logUtilContract.isInitialized)
            logUtilContract.logged(message, logLevel, tag ?: LOG_TAG)
    }
}