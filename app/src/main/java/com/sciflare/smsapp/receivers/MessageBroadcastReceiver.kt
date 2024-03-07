package com.sciflare.smsapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast
import com.sciflare.smsapp.encryption.ConstantValues
import com.sciflare.smsapp.encryption.Encryption
import com.sciflare.smsapp.model.MessageModel


class MessageBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Get SMS map from Intent
        if(context == null || intent == null || intent.action == null){
            return
        }
        if (intent.action != (Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            return
        }
        val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (message in smsMessages) {
            if (message.messageBody.startsWith(ConstantValues.IDENTIFICATION_KEY)){
                val spl = message.messageBody.split(ConstantValues.IDENTIFICATION_KEY)
                val data = Encryption.decrypt(spl[1].toByteArray(),
                    ConstantValues.SECRET_KEY,
                    Encryption.IVP_KEY)
                val msg= MessageModel(message.displayOriginatingAddress, data!!,message.timestampMillis.toString())
                mListener!!.messageReceived(msg)
            }
            Toast.makeText(context, "Message from ${message.displayOriginatingAddress} : body ${message.messageBody}", Toast.LENGTH_SHORT)
                .show()
        }

    }
    companion object {
        private var mListener: MessageListenerInterface? = null
        fun bindListener(listener: MessageListenerInterface) {
            mListener = listener
        }
    }
}