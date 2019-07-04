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
    private val faceApi = MxFaceApiImpl(justouchFaceApi)


    override fun init(application: Context, config: MxConfig): Int {
        return if (config.mode == 0) {
            justouchFaceApi.initAlg(application, config.licencePath, config.modelPath)
        } else {
            justouchFaceApi.initAlgN(application, config.licencePath, config.modelPath)
        }
    }

    override fun free() {
        justouchFaceApi.freeAlg()
    }

    override fun getFaceApi(): MxFaceApi {
        return faceApi
    }




}