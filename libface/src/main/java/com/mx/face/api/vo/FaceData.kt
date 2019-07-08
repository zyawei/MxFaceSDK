package com.mx.face.api.vo

import android.graphics.Point
import android.graphics.Rect


/**
 *
 * @author zhangyw
 * @date 2019-07-02 16:48
 * @email zyawei@live.com
 */

open class FaceData(val trackId: Int = -1, val faceArea: Rect, val keyPoints: List<Point>)