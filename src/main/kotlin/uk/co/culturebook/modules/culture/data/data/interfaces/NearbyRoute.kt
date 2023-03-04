package uk.co.culturebook.modules.culture.data.data.interfaces

sealed interface NearbyRoute {
    val route: String

    sealed interface Version : AddNewRoute {
        object V1 : Version {
            override val route = "nearby/v1"
        }
    }

    object Elements : NearbyRoute {
        override val route = "elements"
    }

    object ElementsMedia : NearbyRoute {
        override val route = "elements/media"
        const val ElementId = "element_id"
    }

    object Contributions : NearbyRoute {
        override val route = "contributions"
    }

    object Cultures : NearbyRoute {
        override val route = "cultures"
    }

    object ContributionsMedia : NearbyRoute {
        override val route = "contributions/media"
        const val ContributionId = "contribution_id"
    }

    object BlockElement : NearbyRoute {
        override val route = "block/element"
    }

    object BlockContribution : NearbyRoute {
        override val route = "block/contribution"
    }

    object BlockCulture : NearbyRoute {
        override val route = "block/culture"
    }
}