package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.model.MainViewModel
import com.example.myapplication.model.MainViewModelFactory
import com.example.myapplication.model.MainViewModelOnLive
import com.example.myapplication.model.MainViewModelOnLiveFactory
import com.example.myapplication.observer.MyObserver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var viewModelOnLive: MainViewModelOnLive
    private lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sp = getPreferences(Context.MODE_PRIVATE)
        val countReserved = sp.getInt("count_reserved", 0)
//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel = ViewModelProvider(this,MainViewModelFactory(countReserved)).get(MainViewModel::class.java)
        viewModelOnLive = ViewModelProvider(this,MainViewModelOnLiveFactory(countReserved)).get(MainViewModelOnLive::class.java)
        plus_button.setOnClickListener {
//            viewModel.counter++
//            refreshCounter()
            viewModelOnLive.plusOne()
        }
        clearBtn.setOnClickListener {
//            viewModel.counter = 0
//            refreshCounter()
            viewModelOnLive.clear()
        }
//        refreshCounter()
        lifecycle.addObserver(MyObserver(lifecycle))
//        viewModelOnLive.counter.observe(this, Observer { count -> infoText.text = count.toString() }) // 写法1
            viewModelOnLive.counter.observe(this) {
                count -> infoText.text = count.toString()
            }
    }

    private fun refreshCounter() {
        infoText.text = viewModel.counter.toString()
    }

    override fun onPause() {
        super.onPause()
        sp.edit {
            putInt("count_reserved",viewModelOnLive.counter.value ?: 0)
        }
    }

}
