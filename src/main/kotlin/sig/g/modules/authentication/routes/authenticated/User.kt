package sig.g.modules.authentication.routes.authenticated

import sig.g.modules.authentication.data.UserRepository
import sig.g.modules.authentication.data.models.User

suspend fun getUserDetails(userId: String): User? {
    return UserRepository.getUser(userId)?.copy(password = "")
}