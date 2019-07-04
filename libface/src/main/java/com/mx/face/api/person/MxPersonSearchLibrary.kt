package com.mx.face.api.person

import com.mx.face.api.MxFaceApi
import com.mx.face.api.vo.FaceFeature
import com.mx.face.api.vo.Person

/**
 * @author zhangyw
 * @date 2019-07-03 15:36
 * @email zyawei@live.com
 */
class MxPersonSearchLibrary : EditablePersonLibrary, PersonSearch {
    private val personLibraries = HashSet<PersonLibrary>()
    private val mPersons = ArrayList<Person>()

    init {
        personLibraries.add(this)
    }

    override fun remove(person: Person) {
        mPersons.add(person)
    }

    override fun add(person: Person) {
        mPersons.add(person)
    }

    override fun persons(): List<Person> {
        return mPersons
    }

    fun regitserLibrary(personLibrary: PersonLibrary) {
        personLibraries.add(personLibrary)
    }

    fun unRegitserLibrary(personLibrary: PersonLibrary) {
        personLibraries.remove(personLibrary)
    }

    var mFaceApi: MxFaceApi? = null

    override fun search(faceFeature: FaceFeature): Person? {
        mFaceApi?.let {
            personLibraries.forEach { lib ->
                val person = it.searchFeature(faceFeature, lib.persons(), 76)
                if (person != null) {
                    return person
                }
            }
        }
        return null
    }
}
