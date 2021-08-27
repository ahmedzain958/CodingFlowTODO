package com.zainco.codingflowtodo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zainco.codingflowtodo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint/*annotation placed in the activity that contains the Tasks fragment that has injected view model*/
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}