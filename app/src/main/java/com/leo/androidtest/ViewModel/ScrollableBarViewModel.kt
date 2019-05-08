package com.leo.androidtest.ViewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View

class ScrollableBarViewModel : ViewModel() {

    val buttonSize: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().apply { value = 3 }
    }

    val buttonHorizontal: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>().apply { value = 0f }
    }

    val leftButtonViewModel: ScrollableBarButtonViewModel by lazy {
        ScrollableBarButtonViewModel()
    }
    val centerButtonViewModel: ScrollableBarButtonViewModel by lazy {
        ScrollableBarButtonViewModel()
    }
    val rightButtonViewModel: ScrollableBarButtonViewModel by lazy {
        ScrollableBarButtonViewModel()
    }


    class ScrollableBarButtonViewModel : ViewModel() {
        val buttonText: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        val buttonOnClick: MutableLiveData<View.OnClickListener> by lazy {
            MutableLiveData<View.OnClickListener>()
        }

    }
}

