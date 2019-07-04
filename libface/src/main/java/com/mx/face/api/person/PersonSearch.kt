package com.mx.face.api.person

import com.mx.face.api.vo.FaceFeature
import com.mx.face.api.vo.Person

/**
 *
 * @author zhangyw
 * @date 2019-07-01 16:21
 * @email zyawei@live.com
 */
interface PersonSearch {

    fun search(faceFeature: FaceFeature): Person?

}