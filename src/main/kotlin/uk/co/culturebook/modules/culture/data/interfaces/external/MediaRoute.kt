package uk.co.culturebook.modules.culture.data.interfaces.external

import uk.co.culturebook.modules.culture.data.interfaces.external.MediaRoute.Version.V1_STORAGE

sealed interface MediaRoute {


    object Version : MediaRoute {
        const val V1_STORAGE = "/storage/v1"
    }

    object FileRoute {
        fun getRoute(bucketName: String, fileName: String) = "/object/$bucketName/$fileName"
        fun getParentRoute(bucketName: String, parent: String, fileName: String) =
            "/object/$bucketName/$parent/$fileName"
    }

    object BucketRoute {
        fun getBucket(fileHost: String) = "${fileHost}/${V1_STORAGE}/bucket"
        fun getBucket(fileHost: String, id: String) = "${fileHost}/${V1_STORAGE}/bucket/$id"
        fun getMedia(fileHost: String, bucketName: String) =
            "${fileHost}/${V1_STORAGE}/object/list/$bucketName"
    }

}