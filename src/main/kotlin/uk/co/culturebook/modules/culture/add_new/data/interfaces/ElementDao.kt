package uk.co.culturebook.modules.culture.add_new.data.interfaces

import uk.co.culturebook.modules.culture.add_new.data.models.Element
import java.util.*

interface ElementDao {

    suspend fun getElement(id: UUID): Element?
    suspend fun getDuplicateElement(name: String, type: String): List<Element>

//    suspend fun uploadMedia(files: List<MediaFile>): List<MediaFile>

    suspend fun insertElement(element: Element): Element?
    suspend fun deleteElement(elementId: UUID): Boolean
    suspend fun updateElement(element: Element): Boolean
}