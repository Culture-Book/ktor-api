package uk.co.culturebook.modules.culture.data.data.interfaces

sealed interface AddNewRoute {
    val route: String

    sealed interface AddNewVersion : AddNewRoute {
        object V1 : AddNewVersion {
            override val route = "add_new/v1"
        }
    }

    object Location : AddNewRoute {
        override val route = "location"
    }

    object Cultures : AddNewRoute {
        override val route = "cultures"
    }

    object Culture : AddNewRoute {
        override val route = "culture"
        const val idParam = "culture_id"
    }

    object Element : AddNewRoute {
        override val route: String = "element"

        object Submit : AddNewRoute {
            override val route: String =
                "${AddNewRoute.Element.route}/submit"
        }

        object Duplicate : AddNewRoute {
            override val route: String =
                "${AddNewRoute.Element.route}/duplicate"
            const val nameParam = "name"
            const val typeParam = "type"
        }
    }

    object Contribution : AddNewRoute {
        override val route: String = "contribution"

        object Submit : AddNewRoute {
            override val route: String =
                "${AddNewRoute.Contribution.route}/submit"
        }

        object Duplicate : AddNewRoute {
            override val route: String =
                "${AddNewRoute.Contribution.route}/duplicate"
            const val nameParam = "name"
            const val typeParam = "type"
        }
    }
}