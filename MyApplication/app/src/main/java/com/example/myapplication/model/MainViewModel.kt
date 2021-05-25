package com.example.myapplication.model

import androidx.lifecycle.ViewModel

class MainViewModel(countReserved:Int) : ViewModel() {

    var counter = countReserved

}