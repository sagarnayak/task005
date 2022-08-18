package com.example.task005.models.core

import com.example.task005.enums.ResultType

data class Result(
    private var code: Int = 0,
    var type: String = "",
    var message: String = "Success",
    private var result: ResultType = ResultType.OK,
) {
    @Suppress("unused")
    fun isResultOk(): Boolean {
        @Suppress("SENSELESS_COMPARISON")
        if (result == null)
            result =
                if (code == 0) ResultType.OK else ResultType.FAIL

        return result == ResultType.OK
    }

    @Suppress("unused")
    fun getMessageToShow(): String {
        return message
    }

    fun getCode() = code
}