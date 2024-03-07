package com.sciflare.smsapp.receivers

import com.sciflare.smsapp.model.MessageModel

interface MessageListenerInterface {
    fun messageReceived(message:MessageModel)
}