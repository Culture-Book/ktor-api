package uk.co.culturebook.modules.culture.data.data.interfaces

sealed interface ElementsRoute {
    val route: String

    sealed interface Version : AddNewRoute {
        object V1 : Version {
            override val route = "elements/v1"
        }
    }

    object Elements : ElementsRoute {
        const val UserElements = "elements/user"
        const val FavouriteElements = "elements/favourite"
        override val route = "elements"
    }

    object Element : ElementsRoute {
        override val route = "element"
        const val Id = "element_id"
    }

    object ElementsMedia : ElementsRoute {
        override val route = "elements/media"
        const val ElementId = "element_id"
    }

    object Contributions : ElementsRoute {
        const val UserContributions = "contributions/user"
        const val FavouriteContributions = "contributions/favourite"
        override val route = "contributions"
    }

    object Contribution : ElementsRoute {
        override val route = "contribution"
        const val Id = "contribution_id"
    }

    object Cultures : ElementsRoute {
        const val UserCultures = "cultures/user"
        const val FavouriteCultures = "contributions/favourite"
        override val route = "cultures"
    }

    object ContributionsMedia : ElementsRoute {
        override val route = "contributions/media"
        const val ContributionId = "contribution_id"
    }

    object BlockedList : ElementsRoute {
        override val route = "block"
    }

    object BlockElement : ElementsRoute {
        override val route = "block/element"
    }

    object BlockContribution : ElementsRoute {
        override val route = "block/contribution"
    }

    object BlockCulture : ElementsRoute {
        override val route = "block/culture"
    }

    object FavouriteElement : ElementsRoute {
        override val route = "favourite/element"
    }

    object FavouriteContribution : ElementsRoute {
        override val route = "favourite/contribution"
    }

    object FavouriteCulture : ElementsRoute {
        override val route = "favourite/culture"
    }
}