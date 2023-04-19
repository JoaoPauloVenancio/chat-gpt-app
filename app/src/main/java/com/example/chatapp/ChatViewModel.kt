package com.example.chatapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class ChatViewModel : ViewModel() {
    private var _chatMessage = MutableLiveData<Resource<String>>()
    val chatMessage: LiveData<Resource<String>> get() = _chatMessage

    private val json = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()
    private val jsonBody = JSONObject()

    fun callChatGptApi(question: String) {
        jsonBody.put("model", "text-davinci-003")
        jsonBody.put("prompt", question)
        jsonBody.put("max_tokens", 4000)
        jsonBody.put("temperature", 0)

        val body = jsonBody.toString().toRequestBody(json)

        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer $CHAT_KEY")
            .post(body)
            .build()

        _chatMessage.postValue(Resource.loading(null))

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _chatMessage.value =
                    Resource.error(null, "Failed to load response due to ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonObject = JSONObject(response.body?.string() ?: EMPTY_STRING)
                    val jsonArray = jsonObject.getJSONArray("choices")
                    val result = jsonArray.getJSONObject(0).getString("text")
                    _chatMessage.postValue(Resource.success(result.trim()))
                } else {
                    _chatMessage.value = Resource.error(
                        null,
                        "Failed to load response due to ${response.body.toString()}"
                    )
                }
            }
        })
    }

}