package uk.co.culturebook.modules.culture.data.interfaces

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.culture.data.models.Element

sealed interface ElementState {
    sealed interface Success : ElementState {
        @Serializable
        data class AddElement(val element: Element) : Success

        @Serializable
        data class UploadSuccess(val media: List<Pair<String, String>>) : Success
    }

    @Serializable
    enum class Error : ElementState {
        Generic,
        DuplicateElement,
        FailedToAddElement,
        FailedToLinkElements,
        FailedToUploadFiles,
        NoBucketName,
        FailedToCreateBucket
    }
}