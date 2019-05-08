package com.leo.androidtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import com.leo.androidtest.ViewModel.ScrollableBarViewModel
import com.leo.androidtest.ui.ScrollableBar
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initButtonBar();
    }

   private fun initButtonBar(){
        val buttonBar =  findViewById<ScrollableBar>(R.id.buttonBar)
        val buttonBarViewModel = ScrollableBarViewModel()
        buttonBar.initialView(this,this,buttonBarViewModel)
        buttonBarViewModel.buttonSize.value = 2
        buttonBarViewModel.leftButtonViewModel.buttonText.value = "left"
        buttonBarViewModel.centerButtonViewModel.buttonText.value = "center"
        buttonBarViewModel.rightButtonViewModel.buttonText.value = "right"
        buttonBarViewModel.leftButtonViewModel.buttonOnClick.value = View.OnClickListener {
           buttonBar.index = 0
       }
        buttonBarViewModel.centerButtonViewModel.buttonOnClick.value = View.OnClickListener {
           buttonBar.index = 1
       }
        buttonBarViewModel.rightButtonViewModel.buttonOnClick.value = View.OnClickListener {
           buttonBar.index = 2
       }
        buttonBar.scrollToIndex();
    }

}
