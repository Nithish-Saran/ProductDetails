package com.apps.saneforcetask.apiRepo

import android.content.Context
import com.apps.saneforcetask.Constant
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ApiRepo {

    suspend fun getProducts(context: Context): JSONArray? = suspendCoroutine {
        httpGetJSON(
            context = context,
            url = Constant.GET_API_URL,
        ) { _, _, _, jsonArray -> it.resume(jsonArray) }
    }

    suspend fun updateProducts(context: Context, json: JSONObject): JSONObject? = suspendCoroutine {
        httpPostJSON(
            context = context,
            jsonData = json,
            url = Constant.POST_API_URL,
        ) { _, _, jsonObject, _ -> it.resume(jsonObject) }
    }

    private fun httpGetJSON(
        context: Context, url: String, responder: (Boolean, Int, JSONObject?, JSONArray?) -> Unit
    ) {
        AsyncHttpClient().let {
            it.setMaxRetriesAndTimeout(2, 0)
            it.get(context, url, object : JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int, headers: Array<out Header>?,
                    response: JSONArray?
                ) {
                    responder(true, statusCode, null, response)
                }

                override fun onSuccess(
                    statusCode: Int, headers: Array<out Header>?,
                    response: JSONObject?
                ) {
                    responder(true, statusCode, response, null)
                }

                override fun onSuccess(
                    statusCode: Int, headers: Array<out Header>?,
                    responseString: String?
                ) {
                    responder(true, statusCode, null, null)
                }

                override fun onFailure(
                    statusCode: Int, headers: Array<out Header>?,
                    responseString: String?, throwable: Throwable?
                ) {
                    responder(false, statusCode, null, null)
                }

                override fun onFailure(
                    statusCode: Int, headers: Array<out Header>?,
                    throwable: Throwable?, errorResponse: JSONArray?
                ) {
                    responder(false, statusCode, null, null)
                }

                override fun onFailure(
                    statusCode: Int, headers: Array<out Header>?,
                    throwable: Throwable?, errorResponse: JSONObject?
                ) {
                    responder(false, statusCode, null, null)
                }
            })
        }
    }

    // get JSON from http post call
    private fun httpPostJSON(
        context: Context,
        url: String,
        jsonData: JSONObject,
        responder: (Boolean, Int, JSONObject?, JSONArray?) -> Unit
    ) {
        AsyncHttpClient().let {
            it.setMaxRetriesAndTimeout(2, 0)
            it.post(
                context, url, StringEntity(jsonData.toString()),
                "application/json", object : JsonHttpResponseHandler() {

                    override fun onSuccess(
                        statusCode: Int, headers: Array<out Header>?,
                        response: JSONArray?
                    ) {
                        responder(true, statusCode, null, response)
                    }

                    override fun onSuccess(
                        statusCode: Int, headers: Array<out Header>?,
                        response: JSONObject?
                    ) {
                        responder(true, statusCode, response, null)
                    }

                    override fun onSuccess(
                        statusCode: Int, headers: Array<out Header>?,
                        responseString: String?
                    ) {
                        responder(true, statusCode, null, null)
                    }

                    override fun onFailure(
                        statusCode: Int, headers: Array<out Header>?, responseString: String?,
                        throwable: Throwable?
                    ) {
                        responder(false, statusCode, null, null)
                    }

                    override fun onFailure(
                        statusCode: Int, headers: Array<out Header>?,
                        throwable: Throwable?, errorResponse: JSONArray?
                    ) {
                        responder(false, statusCode, null, null)
                    }

                    override fun onFailure(
                        statusCode: Int, headers: Array<out Header>?, throwable: Throwable?,
                        errorResponse: JSONObject?
                    ) {
                        responder(false, statusCode, null, null)
                    }
                })
        }
    }

}