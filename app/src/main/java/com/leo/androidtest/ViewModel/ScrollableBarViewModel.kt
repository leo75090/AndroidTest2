package com.leo.androidtest.ViewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class ScrollableBarViewModel : ViewModel() {


    val currentName: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }
}