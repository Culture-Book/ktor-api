package uk.co.culturebook.modules.culture.data.data.interfaces

sealed interface ElementRoute {
    val route: String

    sealed interface Version : AddNewRoute {
        object V1 : Version {
            override val route = "elements/v1"
        }
    }

    object Elements : ElementRoute {
        override val route = "elements"
    }

    object ElementsMedia : ElementRoute {
        override val route = "elements/media"
        const val ElementId = "element_id"
    }

    object Contributions : ElementRoute {
        override val route = "contributions"
        const val ElementId = "element_id"
    }

    object ContributionsMedia : ElementRoute {
        override val route = "contributions/media"
        const val ContributionId = "contribution_id"
    }
}