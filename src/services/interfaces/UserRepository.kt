package services.interfaces

import domain.models.User

interface UserRepository {
    fun findByLogin(login: String): User?
}
