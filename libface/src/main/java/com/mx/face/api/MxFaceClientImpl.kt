package com.mx.face.api

import android.content.Context
import org.zz.jni.JustouchFaceApi

/**
 *
 * @author zhangyw
 * @date 2019-07-03 14:33
 * @email zyawei@live.com
 */

object MxFaceClientImpl : MxFaceClient {

    private val justouchFaceApi = JustouchFaceApi()
    private lateinit var faceApi : MxFaceApi


    override fun init(application: Context, config: MxConfig): Int {
        faceApi = MxFaceApiImpl(justouchFaceApi,config)
        return if (config.mode == 0) {
            justouchFaceApi.initAlg(application, config.modelPath,config.licencePath)
        } else {
            justouchFaceApi.initAlgN(application, config.modelPath, config.licencePath)
        }
    }

    override fun free() {
        justouchFaceApi.freeAlg()
    }

    override fun getFaceApi(): MxFaceApi {
        return faceApi
    }




}