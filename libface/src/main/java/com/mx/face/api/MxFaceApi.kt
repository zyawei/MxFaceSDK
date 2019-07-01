package com.mx.face.api

import com.miaxis.image.MxImage

/**
 *
 * @author zhangyw
 * @date 2019-07-01 16:21
 * @email zyawei@live.com
 */
interface MxFaceApi {

    fun detectFace(image: MxImage, track: Boolean)

    fun extractQuality()

    fun extractFeature()

    fun match()
}