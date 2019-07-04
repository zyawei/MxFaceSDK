package com.mx.face.api.person

import com.mx.face.api.vo.Person

/**
 *
 * @author zhangyw
 * @date 2019-07-03 15:28
 * @email zyawei@live.com
 */

interface EditablePersonLibrary : PersonLibrary {

    fun add(person: Person)
    fun remove(person: Person)

}