package com.example.myapplication.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelOnLiveFactory(private val countReserved : Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModelOnLive(countReserved) as T
    }
}