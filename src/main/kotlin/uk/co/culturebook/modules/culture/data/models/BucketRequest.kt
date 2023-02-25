package uk.co.culturebook.modules.culture.data.models

import kotlinx.serialization.Serializable


@Serializable
data class BucketRequest(
    val id: String,
    val name: String,
    val public: Boolean = false
)
