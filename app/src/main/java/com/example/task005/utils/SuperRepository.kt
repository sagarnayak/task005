@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.task005.utils

import androidx.lifecycle.MutableLiveData
import com.example.task005.contracts.Event
import com.example.task005.contracts.Repository
import com.example.task005.contracts.SuperMutableLiveData
import com.example.task005.enums.ResultType
import com.example.task005.models.core.NetworkCallTime
import com.example.task005.utils.logutil.LogUtil
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*

abstract class SuperRepository : Repository() {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //fields needs to supplied from outside
    var internalServerErrorMessage = ""
    var fallbackErrorMessage = ""
    var payloadTooLargeMessage = ""
    var notFound = ""
    var timeoutOccurred = "Timeout occurred"
    var networkError = "Network Error"
    var somethingWentWrong = "Something went wrong."

    var ERROR_MESSAGE_DATABASE_ERROR = ""

    lateinit var logUtilForSuper: LogUtil
    ////////////////////////////////////////////////////////////////////////////////////////////////

    init {
        super.networkCallTimeMaster = com.example.task005.utils.NetworkCallTimeMaster()
    }

    fun logThisError(error: String) {
        if (this::logUtilForSuper.isInitialized) {
            logUtilForSuper.logE(error)
        }
    }

    val keysMayHaveDynamicFields: ArrayList<String> = ArrayList()

    protected fun appendToKeysMayHaveDynamicFields(keys: ArrayList<String>) {
        keysMayHaveDynamicFields.addAll(keys)
    }

    //util function
    fun getErrorMessage(throwable: Throwable): String {
        return if (throwable is HttpException) {
            val responseBody = throwable.response()!!.errorBody()
            try {
                val jsonObject = JSONObject(responseBody!!.string())
                jsonObject.getString("error")
            } catch (e: Exception) {
                logThisError(e.message!!)
                e.message!!
            }
        } else (when (throwable) {
            is SocketTimeoutException -> timeoutOccurred
            is IOException -> networkError
            else -> throwable.message
        })!!
    }

    @Suppress("unused")
    private fun getErrorMessage(responseBody: ResponseBody): String {
        return try {
            val jsonObject = JSONObject(responseBody.string())
            jsonObject.getString("error")
        } catch (e: Exception) {
            somethingWentWrong
        }
    }

    lateinit var superRepositoryUnAuthorisedCallbackGlobal: SuperRepositoryCallback<com.example.task005.models.core.Result>

    fun registerForUnAuthorisedGlobalCallback(callback: SuperRepositoryCallback<com.example.task005.models.core.Result>) {
        this.superRepositoryUnAuthorisedCallbackGlobal = callback
    }

    inline fun <reified T> makeApiCall(
        observable: Observable<Response<ResponseBody>>,
        responseJsonKeyword: String = "",
        doNotLookForResponseBody: Boolean = false,
        lookForOnlySuccessCode: Boolean = false,
        callback: SuperRepositoryCallback<T>? = null,
        successMutableLiveData: MutableLiveData<Event<T>>? = null,
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>? = null,
        superMutableLiveData: SuperMutableLiveData<T>? = null,
        ignoreUnAuthorisedResponse: Boolean = false,
        giveRawResponse: SuperRepositoryCallbackForRawResponse? = null
    ) {
        val networkCallTime = NetworkCallTime(startTime = Calendar.getInstance().timeInMillis)

        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : Observer<Response<ResponseBody>> {
                    override fun onComplete() {
                        networkCallTime.endTime = Calendar.getInstance().timeInMillis
                        networkCallTimeMaster.gotNetworkCallTime(networkCallTime)
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Response<ResponseBody>) {
                        when (t.code()) {
                            StatusCode.OK.code -> {
                                process200SeriesResponse(
                                    responseJsonKeyword,
                                    doNotLookForResponseBody,
                                    lookForOnlySuccessCode,
                                    callback,
                                    successMutableLiveData,
                                    errorMutableLiveData,
                                    t,
                                    superMutableLiveData,
                                    giveRawResponse
                                )
                            }
                            StatusCode.Created.code -> {
                                process200SeriesResponse(
                                    responseJsonKeyword,
                                    doNotLookForResponseBody,
                                    lookForOnlySuccessCode,
                                    callback,
                                    successMutableLiveData,
                                    errorMutableLiveData,
                                    t,
                                    superMutableLiveData
                                )
                            }
                            StatusCode.Unauthorized.code -> {
                                processUnAuthorisedResponse(
                                    t,
                                    ignoreUnAuthorisedResponse,
                                    errorMutableLiveData,
                                    superMutableLiveData,
                                    callback
                                )
                            }
                            StatusCode.PayloadTooLarge.code -> {
                                processPayloadTooLargeResponse(
                                    errorMutableLiveData,
                                    superMutableLiveData,
                                    callback
                                )
                            }
                            StatusCode.NotFound.code -> {
                                processNotFoundResponse(
                                    errorMutableLiveData,
                                    superMutableLiveData,
                                    callback
                                )
                            }
                            StatusCode.InternalServerError.code -> {
                                processInternalServerErrorResponse(
                                    errorMutableLiveData,
                                    superMutableLiveData,
                                    callback
                                )
                            }
                            StatusCode.Forbidden.code -> {
                                processForbiddenResponse(
                                    t,
                                    errorMutableLiveData,
                                    superMutableLiveData,
                                    callback
                                )
                            }
                            StatusCode.BadRequest.code -> {
                                processBadRequestResponse(
                                    t,
                                    errorMutableLiveData,
                                    superMutableLiveData,
                                    callback
                                )
                            }
                            else -> {
                                try {
                                    val errorBody = t.errorBody()
                                    val errorResponse: com.example.task005.models.core.Result =
                                        fromJson(errorBody!!.string())
                                    errorMutableLiveData?.postValue(
                                        Event(
                                            errorResponse
                                        )
                                    )
                                    superMutableLiveData?.getFail()?.postValue(
                                        Event(
                                            errorResponse
                                        )
                                    )
                                    callback?.error(errorResponse)
                                } catch (ex: java.lang.Exception) {
                                    logThisError(ex.toString())
                                    ex.printStackTrace()
                                    val errorReply = com.example.task005.models.core.Result(
                                        StatusCode.FailedToParseData.code,
                                        message = "We are having some error. Please try after some time.",
                                        result = ResultType.FAIL
                                    )
                                    errorMutableLiveData?.postValue(
                                        Event(
                                            errorReply
                                        )
                                    )
                                    superMutableLiveData?.getFail()?.postValue(
                                        Event(
                                            errorReply
                                        )
                                    )
                                    callback?.error(errorReply)
                                }
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        val errorMessage = getErrorMessage(e)
                        val errorReply = com.example.task005.models.core.Result(
                            StatusCode.FailedToParseData.code,
                            errorMessage,
                            errorMessage,
                            result = ResultType.FAIL
                        )
                        if (
                            errorReply.type.equals("Network Error", true)
                        ) {
                            errorReply.message = fallbackErrorMessage
                        }
                        errorMutableLiveData?.postValue(
                            Event(
                                errorReply
                            )
                        )
                        superMutableLiveData?.getFail()?.postValue(
                            Event(
                                errorReply
                            )
                        )

                        callback?.error(errorReply)
                    }
                }
            )
    }

    inline fun <reified T> processForbiddenResponse(
        t: Response<ResponseBody>,
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>?,
        superMutableLiveData: SuperMutableLiveData<T>?,
        callback: SuperRepositoryCallback<T>?
    ) {
        try {
            val errorBody = t.errorBody()
            val errorResponse: com.example.task005.models.core.Result =
                fromJson(errorBody!!.string())
            errorMutableLiveData?.postValue(
                Event(
                    errorResponse
                )
            )
            superMutableLiveData?.getFail()?.postValue(
                Event(
                    errorResponse
                )
            )
            callback?.error(errorResponse)
        } catch (ex: Exception) {
            ex.printStackTrace()
            logThisError(ex.toString())
            val errorResponse =
                com.example.task005.models.core.Result(
                    message = internalServerErrorMessage,
                    code = StatusCode.InternalServerError.code,
                    result = ResultType.FAIL
                )
            errorMutableLiveData?.postValue(
                Event(
                    errorResponse
                )
            )
            superMutableLiveData?.getFail()?.postValue(
                Event(
                    errorResponse
                )
            )
            callback?.error(errorResponse)
        }
    }

    inline fun <reified T> processBadRequestResponse(
        t: Response<ResponseBody>,
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>?,
        superMutableLiveData: SuperMutableLiveData<T>?,
        callback: SuperRepositoryCallback<T>?
    ) {
        try {
            val errorBody = t.errorBody()
            val errorResponse: com.example.task005.models.core.Result =
                fromJson(errorBody!!.string())
            errorMutableLiveData?.postValue(
                Event(
                    errorResponse,
                )
            )
            superMutableLiveData?.getFail()?.postValue(
                Event(
                    errorResponse
                )
            )
            callback?.error(errorResponse)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            logThisError(ex.toString())
            val errorReply = com.example.task005.models.core.Result(
                StatusCode.FailedToParseData.code,
                message = "We are having some error. Please try after some time.",
                result = ResultType.FAIL
            )
            errorMutableLiveData?.postValue(
                Event(
                    errorReply
                )
            )
            superMutableLiveData?.getFail()?.postValue(
                Event(
                    errorReply
                )
            )
            callback?.error(errorReply)
        }
    }

    inline fun <reified T> processInternalServerErrorResponse(
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>?,
        superMutableLiveData: SuperMutableLiveData<T>?,
        callback: SuperRepositoryCallback<T>?
    ) {
        val errorResponse =
            com.example.task005.models.core.Result(
                message = internalServerErrorMessage,
                code = StatusCode.InternalServerError.code,
                result = ResultType.FAIL
            )
        errorMutableLiveData?.postValue(
            Event(
                errorResponse
            )
        )
        superMutableLiveData?.getFail()?.postValue(
            Event(
                errorResponse
            )
        )
        callback?.error(errorResponse)
    }

    inline fun <reified T> processNotFoundResponse(
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>?,
        superMutableLiveData: SuperMutableLiveData<T>?,
        callback: SuperRepositoryCallback<T>?
    ) {
        val errorResponse =
            com.example.task005.models.core.Result(
                message = notFound,
                code = StatusCode.NotFound.code,
                result = ResultType.FAIL
            )
        errorMutableLiveData?.postValue(
            Event(
                errorResponse
            )
        )
        superMutableLiveData?.getFail()?.postValue(
            Event(
                errorResponse
            )
        )
        callback?.error(errorResponse)
    }

    inline fun <reified T> processPayloadTooLargeResponse(
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>?,
        superMutableLiveData: SuperMutableLiveData<T>?,
        callback: SuperRepositoryCallback<T>?
    ) {
        val errorResponse =
            com.example.task005.models.core.Result(
                message = payloadTooLargeMessage,
                code = StatusCode.PayloadTooLarge.code,
                result = ResultType.FAIL
            )
        errorMutableLiveData?.postValue(
            Event(
                errorResponse
            )
        )
        superMutableLiveData?.getFail()?.postValue(
            Event(
                errorResponse
            )
        )
        callback?.error(errorResponse)
    }

    inline fun <reified T> processUnAuthorisedResponse(
        t: Response<ResponseBody>,
        ignoreUnAuthorisedResponse: Boolean,
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>?,
        superMutableLiveData: SuperMutableLiveData<T>?,
        callback: SuperRepositoryCallback<T>?
    ) {
        try {
            val errorBody = t.errorBody()
            val errorResponse: com.example.task005.models.core.Result =
                fromJson(errorBody!!.string())
            var sendDataThroughCallbacksAndLiveData = true
            if (!ignoreUnAuthorisedResponse) {
                superRepositoryUnAuthorisedCallbackGlobal.let {
                    superRepositoryUnAuthorisedCallbackGlobal.notAuthorised()
                }
            }
            if (sendDataThroughCallbacksAndLiveData) {
                errorMutableLiveData?.postValue(
                    Event(
                        errorResponse
                    )
                )
                superMutableLiveData?.getFail()?.postValue(
                    Event(
                        errorResponse
                    )
                )
                callback?.error(errorResponse)
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            logThisError(ex.toString())
            val errorReply = com.example.task005.models.core.Result(
                StatusCode.FailedToParseData.code,
                message = "We are having some error. Please try after some time.",
                result = ResultType.FAIL
            )
            errorMutableLiveData?.postValue(
                Event(
                    errorReply
                )
            )
            superMutableLiveData?.getFail()?.postValue(
                Event(
                    errorReply
                )
            )
            callback?.error(errorReply)
        }
    }

    inline fun <reified T> process200SeriesResponse(
        responseJsonKeyword: String = "",
        doNotLookForResponseBody: Boolean = false,
        lookForOnlySuccessCode: Boolean = false,
        callback: SuperRepositoryCallback<T>? = null,
        successMutableLiveData: MutableLiveData<Event<T>>? = null,
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>? = null,
        response: Response<ResponseBody>,
        superMutableLiveData: SuperMutableLiveData<T>? = null,
        giveRawResponse: SuperRepositoryCallbackForRawResponse? = null
    ) {
        giveRawResponse?.let {
            giveRawResponse.giveRawResponse(response.body())
            return
        }
        if (lookForOnlySuccessCode) {
            successMutableLiveData?.postValue(
                Event(
                    T::class.java.newInstance()
                )
            )

            callback?.success(
                T::class.java.newInstance()
            )

            return
        }
        try {
            val toParse = response.body()
            val jsonObject = JSONObject(toParse!!.string())

            var statusReply = com.example.task005.models.core.Result(
                StatusCode.OK.code,
                "Success",
                result = ResultType.OK
            )

            val doesItHasStatusObject = jsonObject.has("code")

            if (
                doesItHasStatusObject
            ) {
                val code = jsonObject.getInt("code")
                val message = jsonObject.getString("message")

                statusReply =
                    com.example.task005.models.core.Result(
                        code = code,
                        message = message,
                        result = ResultType.OK
                    )
            }

            if (statusReply.isResultOk()) {
                if (doNotLookForResponseBody) {
                    successMutableLiveData?.postValue(
                        Event(
                            T::class.java.newInstance()
                        )
                    )
                    superMutableLiveData?.getSuccess()?.postValue(
                        Event(
                            T::class.java.newInstance()
                        )
                    )

                    callback?.success(
                        T::class.java.newInstance()
                    )

                    return
                }

                val resultToSendString = if (responseJsonKeyword != "") {
                    jsonObject.getString(responseJsonKeyword)
                } else {
                    jsonObject.toString()
                }

                val resultToSend =
                    if (
                        !resultToSendString.contains("{") &&
                        !resultToSendString.contains("[")
                    )
                        resultToSendString as T
                    else if (
                        T::class.java.newInstance() is Collections
                    ) {
                        resultToSendString as T
                    } else {
                        fromJson(
                            resultToSendString
                        )
                    }

                try {
                    var resultToParse =
                        if (responseJsonKeyword.isNotEmpty()) jsonObject.getJSONObject(
                            responseJsonKeyword
                        ) else jsonObject
                    var operationSuccess = false
                    if (keysMayHaveDynamicFields.contains(responseJsonKeyword))
                        operationSuccess = true
                    keysMayHaveDynamicFields.forEach { key ->
                        if (!operationSuccess && resultToParse.has(key)) {
                            resultToParse = resultToParse.getJSONObject(key)
                            operationSuccess = true
                        }
                    }
                    if (!operationSuccess) {
                        throw RuntimeException("No need to check for dynamic fields...")
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }

                val eventToReturn = Event(
                    resultToSend
                )

                successMutableLiveData?.postValue(
                    eventToReturn
                )
                superMutableLiveData?.getSuccess()?.postValue(
                    eventToReturn
                )

                callback?.success(
                    resultToSend
                )
            } else {
                errorMutableLiveData?.postValue(
                    Event(
                        statusReply
                    )
                )
                superMutableLiveData?.getFail()?.postValue(
                    Event(
                        statusReply
                    )
                )

                callback?.error(statusReply)
            }
        } catch (ex: Exception) {
            logThisError(ex.toString())
            val errorReply = com.example.task005.models.core.Result(
                StatusCode.FailedToParseData.code,
                "Failed to parse data",
                result = ResultType.FAIL
            )
            errorMutableLiveData?.postValue(
                Event(
                    errorReply
                )
            )
            superMutableLiveData?.getFail()?.postValue(
                Event(
                    errorReply
                )
            )

            callback?.error(errorReply)
        }
    }

    inline fun <reified T> makeDatabaseCall(
        observable: Observable<T>? = null,
        completable: Completable? = null,
        readDataOnlyOnce: Boolean = true,
        canReadResultOnlyOnce: Boolean = true,
        callback: SuperRepositoryCallback<T>? = null,
        successMutableLiveData: MutableLiveData<Event<T>>? = null,
        errorMutableLiveData: MutableLiveData<Event<com.example.task005.models.core.Result>>? = null,
        superMutableLiveData: SuperMutableLiveData<T>? = null
    ) {
        observable?.let { observablePositive ->
            val observableToWatch =
                if (readDataOnlyOnce) observablePositive.take(1) else observablePositive

            observableToWatch
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        successMutableLiveData?.postValue(
                            Event(
                                it,
                                canReadResultOnlyOnce
                            )
                        )
                        superMutableLiveData?.getSuccess()?.postValue(
                            Event(
                                it,
                                canReadResultOnlyOnce
                            )
                        )
                        callback?.success(it)
                    },
                    {
                        val resultToSend = com.example.task005.models.core.Result(
                            StatusCode.Unknown.code,
                            it.message ?: ERROR_MESSAGE_DATABASE_ERROR,
                            result = ResultType.FAIL
                        )
                        errorMutableLiveData?.postValue(
                            Event(
                                resultToSend
                            )
                        )
                        superMutableLiveData?.getFail()?.postValue(
                            Event(
                                resultToSend
                            )
                        )
                        callback?.error(resultToSend)
                    }
                )
        }

        completable?.let { completablePositive ->
            completablePositive
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        successMutableLiveData?.postValue(
                            Event(
                                T::class.java.newInstance(),
                                canReadResultOnlyOnce
                            )
                        )
                        superMutableLiveData?.getSuccess()?.postValue(
                            Event(
                                T::class.java.newInstance(),
                                canReadResultOnlyOnce
                            )
                        )
                        callback?.success(T::class.java.newInstance())
                    },
                    {
                        val resultToSend = com.example.task005.models.core.Result(
                            StatusCode.Unknown.code,
                            it.message ?: ERROR_MESSAGE_DATABASE_ERROR,
                            result = ResultType.FAIL
                        )
                        errorMutableLiveData?.postValue(
                            Event(
                                resultToSend
                            )
                        )
                        superMutableLiveData?.getFail()?.postValue(
                            Event(
                                resultToSend
                            )
                        )
                        callback?.error(resultToSend)
                    }
                )
        }
    }
}