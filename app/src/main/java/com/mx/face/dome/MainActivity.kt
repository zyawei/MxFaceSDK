package com.mx.face.dome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.miaxis.image.MxImages

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val channelName = MxImages.getChannelName(3)
        Log.i("Mx-Main","name $channelName")
    }
}
