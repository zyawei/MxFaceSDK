package com.mx.face.api.vo

/**
 *
 * @author zhangyw
 * @date 2019-07-03 09:31
 * @email zyawei@live.com
 */

class Person(id: Long, val features: MutableList<FaceFeature>){

    fun addFaceFeature(faceFeature: FaceFeature){
        features.add(faceFeature)
    }

    fun removeFaceFeature(faceFeature: FaceFeature){
        features.remove(faceFeature)
    }
}