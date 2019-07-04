package com.mx.face.dome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.miaxis.image.MxImage
import com.miaxis.image.MxImages
import com.mx.face.api.MxFaceApi
import com.mx.face.api.MxFaceClientImpl
import com.mx.face.api.vo.FaceData
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    var mxFaceApi = MxFaceClientImpl.getFaceApi()
    val executor = Executors.newSingleThreadExecutor();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
