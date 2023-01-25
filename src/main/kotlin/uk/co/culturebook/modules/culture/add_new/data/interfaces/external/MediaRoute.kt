package uk.co.culturebook.modules.culture.add_new.data.interfaces.external

import uk.co.culturebook.modules.culture.add_new.data.interfaces.external.MediaRoute.Version.V1_STORAGE

sealed interface MediaRoute {


    object Version : MediaRoute {
        const val V1_STORAGE = "/storage/v1"
    }

    object FileRoute {
        fun getRoute(bucketName: String, fileName: String) = "/object/$bucketName/$fileName"
    }

    object BucketRoute {
        fun getBucket(fileHost: String) = "${fileHost}/${V1_STORAGE}/bucket"
        fun getRoute(bucketName: String) = "/object/$bucketName"
        fun getMedia(fileHost: String, bucketName: String) =
            "${fileHost}/${V1_STORAGE}/object/list/$bucketName"
    }

}