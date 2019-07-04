package com.mx.face.api

import androidx.annotation.WorkerThread
import com.miaxis.image.MxImage
import com.mx.face.api.vo.FaceData
import com.mx.face.api.vo.FaceFeature
import com.mx.face.api.vo.FaceQuality
import com.mx.face.api.vo.Person

/**
 *
 * @author zhangyw
 * @date 2019-07-01 16:21
 * @email zyawei@live.com
 */
interface MxFaceApi {

    @WorkerThread
    fun detectFace(image: MxImage, track: Boolean): Array<FaceData>

    @WorkerThread
    fun extractQuality(image: MxImage, vararg faceData: FaceData): Array<FaceQuality>

    @WorkerThread
    fun extractFeature(image: MxImage, vararg faceData: FaceData): Array<FaceFeature>

    @WorkerThread
    fun matchFeature(feature: FaceFeature, featureB: FaceFeature): Float

//    @WorkerThread
//    fun searchFeature(feature: FaceFeature, vararg persons: Person, threshold: Int): Person?

    @WorkerThread
    fun searchFeature(feature: FaceFeature, persons: Iterable<Person>, threshold: Int): Person?

}