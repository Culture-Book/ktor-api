package uk.co.culturebook.modules.culture.data.models

import kotlinx.serialization.Serializable


@Serializable
data class MediaFileRequest(
    val limit: Int = 5,
    val offset: Int = 0,
    val prefix: String, // Folder name
    val search: String = "",
    val sortBy: SortBy = SortBy()
) {
    @Serializable
    data class SortBy(val column: String = "name", val order: String = "asc")
}
