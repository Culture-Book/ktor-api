package uk.co.culturebook.modules.culture.add_new.logic

import uk.co.culturebook.modules.culture.data.database.repositories.CultureRepository
import uk.co.culturebook.modules.culture.data.database.repositories.ElementRepository
import uk.co.culturebook.modules.culture.data.interfaces.ContributionState
import uk.co.culturebook.modules.culture.data.interfaces.CultureState
import uk.co.culturebook.modules.culture.data.interfaces.ElementState
import uk.co.culturebook.modules.culture.data.models.*
import uk.co.culturebook.utils.fuzzySearchStrings

internal suspend fun addCulture(userId: String, culture: Culture, location: Location): CultureState {
    val nearbyCultures = CultureRepository.getCulturesByLocation(userId, location)
    val isNotDuplicate = nearbyCultures.map { it.name }.fuzzySearchStrings(culture.name, 0.5).isEmpty()
    return if (isNotDuplicate) {
        CultureRepository.insertCulture(culture, userId)?.let { CultureState.Success.AddCulture(culture) }
            ?: CultureState.Error.Generic
    } else {
        CultureState.Error.DuplicateCulture
    }
}

internal suspend fun addElement(
    apiKey: String,
    bearer: String,
    fileHost: String,
    element: Element,
    userId: String
): ElementState {
    val isDuplicate = ElementRepository.getDuplicateElement(element.name, element.type.name).isNotEmpty()

    if (isDuplicate) return ElementState.Error.DuplicateElement

    val insertElement = ElementRepository.insertElement(element, userId) ?: return ElementState.Error.FailedToAddElement

    val linkedElements = ElementRepository.linkElements(insertElement.id, element.linkElements)

    if (!linkedElements) return ElementState.Error.FailedToLinkElements

    val bucketCreated = ElementRepository.createBucketForElement(
        apiKey = apiKey,
        bearer = bearer,
        fileHost = fileHost,
        request = BucketRequest(id = element.id.toString(), name = element.id.toString())
    )

    if (!bucketCreated) {
        return ElementState.Error.FailedToCreateBucket
    }
    return ElementState.Success.AddElement(insertElement)
}

internal suspend fun uploadElementMedia(
    apiKey: String,
    bearer: String,
    fileHost: String,
    mediaFiles: List<MediaFile>
): ElementState {
    val files = ElementRepository.uploadMedia(
        apiKey = apiKey,
        bearer = bearer,
        fileHost = fileHost,
        files = mediaFiles,
    )
    if (files.isEmpty()) return ElementState.Error.FailedToUploadFiles
    return ElementState.Success.UploadSuccess(files.map { it.getUri(fileHost).toString() to it.contentType })
}

internal suspend fun addContribution(
    apiKey: String,
    bearer: String,
    fileHost: String,
    contribution: Contribution,
    userId: String
): ContributionState {
    val isDuplicate =
        uk.co.culturebook.modules.culture.data.database.repositories.ContributionRepository.getDuplicateContribution(
            contribution.name,
            contribution.type.name
        ).isNotEmpty()

    if (isDuplicate) return ContributionState.Error.DuplicateContribution

    val insertedContribution =
        uk.co.culturebook.modules.culture.data.database.repositories.ContributionRepository.insertContribution(
            contribution, userId
        )
            ?: return ContributionState.Error.FailedToAddContribution

    val linkedElements =
        uk.co.culturebook.modules.culture.data.database.repositories.ContributionRepository.linkContributions(
            insertedContribution.id,
            contribution.linkElements
        )

    if (!linkedElements) return ContributionState.Error.FailedToLinkContributions

    val bucketCreated =
        uk.co.culturebook.modules.culture.data.database.repositories.ContributionRepository.createBucketForContribution(
            apiKey = apiKey,
            bearer = bearer,
            fileHost = fileHost,
            request = BucketRequest(id = contribution.id.toString(), name = contribution.id.toString())
        )

    if (!bucketCreated) {
        return ContributionState.Error.FailedToCreateBucket
    }
    return ContributionState.Success.AddContribution(insertedContribution)
}

internal suspend fun uploadContributionMedia(
    parentElement: String,
    apiKey: String,
    bearer: String,
    fileHost: String,
    mediaFiles: List<MediaFile>
): ContributionState {
    val files = uk.co.culturebook.modules.culture.data.database.repositories.ContributionRepository.uploadMedia(
        parent = parentElement,
        apiKey = apiKey,
        bearer = bearer,
        fileHost = fileHost,
        files = mediaFiles,
    )
    if (files.isEmpty()) return ContributionState.Error.FailedToUploadFiles
    return ContributionState.Success.UploadSuccess(files.map { it.getUri(fileHost).toString() to it.contentType })
}