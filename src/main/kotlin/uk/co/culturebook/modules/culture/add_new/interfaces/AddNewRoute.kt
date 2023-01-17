package uk.co.culturebook.modules.culture.add_new.interfaces

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
        val idParam = "culture_id"
    }
}