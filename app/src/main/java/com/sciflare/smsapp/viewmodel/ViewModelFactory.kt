package com.sciflare.smsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MessagesViewModel::class.java)) {
            return MessagesViewModel() as T
        }
        return super.create(modelClass)
    }
}