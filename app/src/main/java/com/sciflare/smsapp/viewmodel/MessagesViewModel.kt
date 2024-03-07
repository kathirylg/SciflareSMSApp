package com.sciflare.smsapp.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.provider.Telephony
import android.telephony.SmsManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sciflare.smsapp.encryption.ConstantValues
import com.sciflare.smsapp.encryption.Encryption
import com.sciflare.smsapp.model.MessageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


class MessagesViewModel : ViewModel() {
    val launchMessages = MutableLiveData<Boolean>()

    private val viewState = MutableLiveData<UIViewState<ArrayList<MessageModel>>>()
    private val sendMessagesViewState = MutableLiveData<UIViewState<MessageModel>>()
    fun launchMessages(){
        launchMessages.value = true
    }
    fun getAllMessages(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            viewState.postValue(UIViewState.Loading)
            val smsList = ArrayList<MessageModel>()
            val contentResolver: ContentResolver = context.contentResolver
            val cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                    if (body.startsWith(ConstantValues.IDENTIFICATION_KEY)){
                        val spl = body.split(ConstantValues.IDENTIFICATION_KEY)
                        val data = Encryption.decrypt(spl[1].toByteArray(),
                            ConstantValues.SECRET_KEY, Encryption.IVP_KEY)
                        smsList.add(MessageModel(address, data.toString(),date))
                    }
                } while (cursor.moveToNext())
            }
            cursor?.close()
            viewState.postValue(UIViewState.Success(smsList))
        }
    }

    fun newMessageReceived(messageModel: MessageModel){
        viewState.postValue(UIViewState.Update(arrayListOf(messageModel)))
    }
    fun getUIState():LiveData<UIViewState<ArrayList<MessageModel>>>{
        return viewState
    }
    fun getSendSMSUIState():LiveData<UIViewState<MessageModel>>{
        return sendMessagesViewState
    }

    fun sendSMS(context:Context,senderId: String, messageBody: String) {
        viewModelScope.launch {
            try {
                sendMessagesViewState.postValue(UIViewState.Loading)
                val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)

                //Encrypt message body
                val encrypt = Encryption.encrypt(messageBody.toByteArray(),
                    ConstantValues.SECRET_KEY, Encryption.IVP_KEY)
                val valueWithIdentity = ConstantValues.IDENTIFICATION_KEY.plus(encrypt)
                smsManager.sendTextMessage(senderId, null, valueWithIdentity, null, null)
                val messageModel = MessageModel(senderId,messageBody,Date().time.toString())
                sendMessagesViewState.postValue(UIViewState.Success(messageModel))
            } catch (e: Exception) {
                sendMessagesViewState.postValue(UIViewState.Error("Failed to send SMS"))
            }
        }
    }
}