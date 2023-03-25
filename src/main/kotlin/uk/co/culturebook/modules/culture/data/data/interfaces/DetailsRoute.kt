package uk.co.culturebook.modules.culture.data.data.interfaces

sealed interface DetailsRoute {
    sealed interface Version {
        object V1 : Version {
            const val route = "v1"
        }
    }

    object Elements : DetailsRoute {
        const val route = "elements"
        const val elementId = "elementId"
    }

    object Contributions : DetailsRoute {
        const val route = "contributions"
        const val contributionId = "contributionId"
    }

    object Comments : DetailsRoute {
        const val route = "comments"
        const val commentId = "commentId"
        const val isContribution = "isContribution"

        object BlockComment : DetailsRoute {
            const val route = "comments/block"
        }
    }

    object Reactions : DetailsRoute {
        const val route = "reactions"
    }
}