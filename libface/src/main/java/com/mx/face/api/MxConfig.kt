package com.mx.face.api

import android.os.Environment
import java.io.File

/**
 * @author zhangyw
 * @date 2019-07-03 10:19
 * @email zyawei@live.com
 */
class MxConfig {
    class Build {
        private var _maxFaceCount = 10
        private var _mode = 0;

        private var _defaultThreshold = 76
        private var _rootStoragePath: String = File(Environment.getExternalStorageDirectory(), "mx-face").absolutePath

        fun maxFaceCount(count: Int): Build {
            this._maxFaceCount = count
            return this
        }

        fun defaultThreshold(threshold: Int): Build {
            this._defaultThreshold = threshold
            return this
        }

        fun rootStoragePath(absolutePath: String): Build {
            this._rootStoragePath = absolutePath
            return this
        }

        fun mode(mode: Int): Build {
            this._mode = mode
            return this
        }

        private var _modelPath: String? = null
        private var _licencePath: String? = null
        fun modelPath(path: String): Build {
            this._modelPath = path
            return this
        }

        fun licencePath(path: String): Build {
            this._licencePath = path
            return this
        }

        fun build(): MxConfig {
            return MxConfig().apply {
                this.maxFaceCount = _maxFaceCount
                this.defaultThreshold = _defaultThreshold
                this.rootStoragePath = _rootStoragePath
                this.modelPath = _modelPath
                this.licencePath = _licencePath
                this.mode = this@Build._mode
            }
        }
    }

    var modelPath: String? = null
    var licencePath: String? = null
    var mode = 0
    var maxFaceCount = 10
    var defaultThreshold = 76
    var rootStoragePath: String = File(Environment.getExternalStorageDirectory(), "mx-face").absolutePath

}
