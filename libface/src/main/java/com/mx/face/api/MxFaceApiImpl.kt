package com.mx.face.api

import android.graphics.Point
import android.graphics.Rect
import com.miaxis.image.MxImage
import com.mx.face.api.vo.FaceData
import com.mx.face.api.vo.FaceFeature
import com.mx.face.api.vo.FaceQuality
import com.mx.face.api.vo.Person
import org.zz.api.MXFaceInfoEx
import org.zz.jni.JustouchFaceApi
import java.util.*

/**
 *
 * @author zhangyw
 * @date 2019-07-03 10:37
 * @email zyawei@live.com
 */
class MxFaceApiImpl(private val justTouchFaceApi: JustouchFaceApi) : MxFaceApi {

    override fun detectFace(image: MxImage, track: Boolean): Array<FaceData> {
        val faceNumberResult = intArrayOf(1)
        val intsFaceData = IntArray(1024)
        if (track) {
            justTouchFaceApi.trackFace(image.data, image.width, image.height, faceNumberResult, intsFaceData)
        } else {
            justTouchFaceApi.detectFace(image.data, image.width, image.height, faceNumberResult, intsFaceData)
        }
        return int2javaFaceData(intsFaceData, faceNumberResult[0])
    }

    override fun extractQuality(image: MxImage, vararg faceData: FaceData): Array<FaceQuality> {
        val intArray = java2intFaceData(faceData)
        justTouchFaceApi.faceQuality(image.data, image.width, image.height, faceData.size, intArray)
        return int2javaQuality(intArray, faceData.size)
    }


    override fun extractFeature(image: MxImage, vararg faceData: FaceData): Array<FaceFeature> {
        val featureSize = justTouchFaceApi.featureSize
        val intArray = java2intFaceData(faceData)
        val byteArray = ByteArray(featureSize * faceData.size)
        justTouchFaceApi.featureExtract(image.data, image.width, image.height, faceData.size, intArray, byteArray)
        return Array(faceData.size) {
            val featureArray =
                if (featureSize > 1)
                    Arrays.copyOfRange(byteArray, it * featureSize, (it + 1) * featureSize)
                else
                    byteArray
            FaceFeature(featureArray)
        }
    }

    override fun matchFeature(feature: FaceFeature, featureB: FaceFeature): Float {
        val result = FloatArray(1)
        justTouchFaceApi.featureMatch(feature.bytes, featureB.bytes, result)
        return result[0]
    }

    override fun searchFeature(feature: FaceFeature,  persons: Iterable<Person>, threshold: Int): Person? {
        return persons.find {
            /*合并这个人的人脸数据*/
            val arrays = when (it.features.size) {
                0 -> ByteArray(0)
                1 -> it.features[0].bytes
                else -> {
                    ByteArray(it.features.size * justTouchFaceApi.featureSize).apply {
                        it.features.forEachIndexed { index, faceFeature ->
                            System.arraycopy(faceFeature.bytes, 0, this, index, feature.bytes.size)
                        }
                    }
                }
            }
            val resultIndex = IntArray(1)
            val result =
                justTouchFaceApi.searchNFeature(arrays, 1, intArrayOf(1), 1, threshold, feature.bytes, resultIndex)
            return@find result == 0
        }
    }

    private fun int2javaQuality(faceIntArray: IntArray, faceNumber: Int): Array<FaceQuality> {
        return Array(faceNumber) {
            val quality = faceIntArray[it * MXFaceInfoEx.SIZE + 8 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            FaceQuality(quality)
        }
    }

    private fun int2javaFaceData(faceIntArray: IntArray, faceNumber: Int): Array<FaceData> {
        return Array(faceNumber) {
            val rect = Rect().apply {
                left = faceIntArray[it * MXFaceInfoEx.SIZE]
                top = faceIntArray[it * MXFaceInfoEx.SIZE + 1]
                right = faceIntArray[it * MXFaceInfoEx.SIZE + 2] + left
                bottom = faceIntArray[it * MXFaceInfoEx.SIZE + 3] + top
            }
            val keyPointNumber = faceIntArray[it * MXFaceInfoEx.SIZE + 4]
            val points = Array(keyPointNumber) { index ->
                Point(
                    faceIntArray[it * MXFaceInfoEx.SIZE + 5 + index],
                    faceIntArray[it * MXFaceInfoEx.SIZE + 5 + index + MXFaceInfoEx.MAX_KEY_POINT_NUM]
                )
            }
            val trackId = faceIntArray[it * MXFaceInfoEx.SIZE + 12 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            FaceData(trackId, rect, points)
        }
    }

    private fun java2intFaceData(faces: Array<out FaceData>): IntArray {
        return IntArray(1024).apply {
            faces.forEachIndexed { i, faceDt ->
                this[i * MXFaceInfoEx.SIZE] = faceDt.faceArea.left
                this[i * MXFaceInfoEx.SIZE + 1] = faceDt.faceArea.top
                this[i * MXFaceInfoEx.SIZE + 2] = faceDt.faceArea.width()
                this[i * MXFaceInfoEx.SIZE + 3] = faceDt.faceArea.height()
                val keySize = faceDt.keyPoints.size
                this[i * MXFaceInfoEx.SIZE + 4] = keySize
                for (j in 0 until keySize) {
                    val point = faceDt.keyPoints[j]
                    this[i * MXFaceInfoEx.SIZE + 5 + j] = point.x
                    this[i * MXFaceInfoEx.SIZE + 5 + j + MXFaceInfoEx.MAX_KEY_POINT_NUM] = point.y
                }
            }
        }
    }

    fun Int2MXFaceInfoEx(iFaceNum: Int, iFaceInfo: IntArray, pMXFaceInfoEx: Array<MXFaceInfoEx>): Int {
        for (i in 0 until iFaceNum) {
            pMXFaceInfoEx[i].x = iFaceInfo[i * MXFaceInfoEx.SIZE]
            pMXFaceInfoEx[i].y = iFaceInfo[i * MXFaceInfoEx.SIZE + 1]
            pMXFaceInfoEx[i].width = iFaceInfo[i * MXFaceInfoEx.SIZE + 2]
            pMXFaceInfoEx[i].height = iFaceInfo[i * MXFaceInfoEx.SIZE + 3]
            pMXFaceInfoEx[i].keypt_num = iFaceInfo[i * MXFaceInfoEx.SIZE + 4]
            for (j in 0 until pMXFaceInfoEx[i].keypt_num) {
                pMXFaceInfoEx[i].keypt_x[j] = iFaceInfo[i * MXFaceInfoEx.SIZE + 5 + j]
                pMXFaceInfoEx[i].keypt_y[j] = iFaceInfo[i * MXFaceInfoEx.SIZE + 5 + j + MXFaceInfoEx.MAX_KEY_POINT_NUM]
            }
            pMXFaceInfoEx[i].age = iFaceInfo[i * MXFaceInfoEx.SIZE + 5 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].gender = iFaceInfo[i * MXFaceInfoEx.SIZE + 6 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].expression = iFaceInfo[i * MXFaceInfoEx.SIZE + 7 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].quality = iFaceInfo[i * MXFaceInfoEx.SIZE + 8 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].eyeDistance = iFaceInfo[i * MXFaceInfoEx.SIZE + 9 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].liveness = iFaceInfo[i * MXFaceInfoEx.SIZE + 10 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].detected = iFaceInfo[i * MXFaceInfoEx.SIZE + 11 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].trackId = iFaceInfo[i * MXFaceInfoEx.SIZE + 12 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].idmax = iFaceInfo[i * MXFaceInfoEx.SIZE + 13 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].reCog = iFaceInfo[i * MXFaceInfoEx.SIZE + 14 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].reCogId = iFaceInfo[i * MXFaceInfoEx.SIZE + 15 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].reCogScore = iFaceInfo[i * MXFaceInfoEx.SIZE + 16 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].reserved1 = iFaceInfo[i * MXFaceInfoEx.SIZE + 17 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].reserved2 = iFaceInfo[i * MXFaceInfoEx.SIZE + 18 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].pitch = iFaceInfo[i * MXFaceInfoEx.SIZE + 19 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].yaw = iFaceInfo[i * MXFaceInfoEx.SIZE + 20 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
            pMXFaceInfoEx[i].roll = iFaceInfo[i * MXFaceInfoEx.SIZE + 21 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM]
        }
        return 0
    }

    fun MXFaceInfoEx2Int(iFaceNum: Int, iFaceInfo: IntArray, pMXFaceInfoEx: Array<MXFaceInfoEx>): Int {
        for (i in 0 until iFaceNum) {
            iFaceInfo[i * MXFaceInfoEx.SIZE] = pMXFaceInfoEx[i].x
            iFaceInfo[i * MXFaceInfoEx.SIZE + 1] = pMXFaceInfoEx[i].y
            iFaceInfo[i * MXFaceInfoEx.SIZE + 2] = pMXFaceInfoEx[i].width
            iFaceInfo[i * MXFaceInfoEx.SIZE + 3] = pMXFaceInfoEx[i].height
            iFaceInfo[i * MXFaceInfoEx.SIZE + 4] = pMXFaceInfoEx[i].keypt_num
            for (j in 0 until pMXFaceInfoEx[i].keypt_num) {
                iFaceInfo[i * MXFaceInfoEx.SIZE + 5 + j] = pMXFaceInfoEx[i].keypt_x[j]
                iFaceInfo[i * MXFaceInfoEx.SIZE + 5 + j + MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].keypt_y[j]
            }
            iFaceInfo[i * MXFaceInfoEx.SIZE + 5 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].age
            iFaceInfo[i * MXFaceInfoEx.SIZE + 6 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].gender
            iFaceInfo[i * MXFaceInfoEx.SIZE + 7 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].expression
            iFaceInfo[i * MXFaceInfoEx.SIZE + 8 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].quality
            iFaceInfo[i * MXFaceInfoEx.SIZE + 9 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].eyeDistance
            iFaceInfo[i * MXFaceInfoEx.SIZE + 10 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].liveness
            iFaceInfo[i * MXFaceInfoEx.SIZE + 11 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].detected
            iFaceInfo[i * MXFaceInfoEx.SIZE + 12 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].trackId
            iFaceInfo[i * MXFaceInfoEx.SIZE + 13 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].idmax
            iFaceInfo[i * MXFaceInfoEx.SIZE + 14 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reCog
            iFaceInfo[i * MXFaceInfoEx.SIZE + 15 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reCogId
            iFaceInfo[i * MXFaceInfoEx.SIZE + 16 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reCogScore
            iFaceInfo[i * MXFaceInfoEx.SIZE + 17 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reserved1
            iFaceInfo[i * MXFaceInfoEx.SIZE + 18 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].reserved2
            iFaceInfo[i * MXFaceInfoEx.SIZE + 19 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].pitch
            iFaceInfo[i * MXFaceInfoEx.SIZE + 20 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].yaw
            iFaceInfo[i * MXFaceInfoEx.SIZE + 21 + 2 * MXFaceInfoEx.MAX_KEY_POINT_NUM] = pMXFaceInfoEx[i].roll
        }
        return 0
    }

}