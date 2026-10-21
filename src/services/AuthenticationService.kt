package services

import services.interfaces.UserRepository

// Аутентификация: "кто ты?". Только логин и пароль, ничего про ресурсы.
class AuthenticationService(
    private val users: UserRepository,
    private val security: SecurityService
) {
    fun authenticate(login: String, password: String): AccessResult {
        val user = users.findByLogin(login) ?: return AccessResult.USER_NOT_FOUND

        val calculatedHash = security.hashPassword(password, user.salt)
        if (calculatedHash != user.hash) {
            return AccessResult.WRONG_PASSWORD
        }
        return AccessResult.SUCCESS
    }
}
