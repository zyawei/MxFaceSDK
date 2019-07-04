package com.mx.face.api

import android.content.Context

/**
 *
 * @author zhangyw
 * @date 2019-07-02 16:04
 * @email zyawei@live.com
 */
interface MxFaceClient {

    fun init(application: Context, config: MxConfig):Int

    fun free()

    fun getFaceApi(): MxFaceApi
}