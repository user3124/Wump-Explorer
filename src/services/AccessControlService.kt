package services

// Определяет только ПОРЯДОК проверок: сначала аутентификация, потом авторизация.
// Сами правила лежат в AuthenticationService и AuthorizationService.
// Возвращает бизнес-результат; exitProcess здесь не вызывается.
class AccessControlService(
    private val authentication: AuthenticationService,
    private val authorization: AuthorizationService
) {
    fun check(login: String, password: String, action: String, resource: String, volume: Int): AccessResult {
        val authResult = authentication.authenticate(login, password)
        if (authResult != AccessResult.SUCCESS) {
            return authResult
        }
        return authorization.authorize(login, action, resource, volume)
    }
}
