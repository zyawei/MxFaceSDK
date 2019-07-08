package com.mx.face.api.vo

import com.miaxis.image.MxImage

/**
 *
 * @author zhangyw
 * @date 2019-07-03 09:31
 * @email zyawei@live.com
 */

open class Person(val id: Long = 0, val images: List<MxImage>, val features: List<FaceFeature>, val tag: String = "")

