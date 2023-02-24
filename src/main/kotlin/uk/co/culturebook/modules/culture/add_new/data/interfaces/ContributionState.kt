package uk.co.culturebook.modules.culture.add_new.data.interfaces

import kotlinx.serialization.Serializable
import uk.co.culturebook.modules.culture.add_new.data.models.Contribution

sealed interface ContributionState {
    sealed interface Success : ContributionState {
        @Serializable
        data class AddContribution(val contribution: Contribution) : Success

        @Serializable
        data class UploadSuccess(val media: List<Pair<String, String>>) : Success
    }

    @Serializable
    enum class Error : ContributionState {
        Generic,
        DuplicateContribution,
        FailedToAddContribution,
        FailedToLinkContributions,
        FailedToUploadFiles,
        NoBucketName,
        FailedToCreateBucket
    }
}