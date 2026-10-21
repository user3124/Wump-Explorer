import domain.models.Action
import infrastructure.cli.CliManager
import infrastructure.cli.HelpPrinter
import infrastructure.cli.InputParser
import infrastructure.cli.ParseResult
import infrastructure.database.InMemoryDatabase
import services.AccessControlService
import services.AccessResult
import services.AuthenticationService
import services.AuthorizationService
import services.SecurityService
import services.rules.ResourceLimitRule
import kotlin.system.exitProcess

// Единственное место, где вызывается exitProcess.
fun main(args: Array<String>) {
    exitProcess(runApplication(args))
}

// Собирает компоненты, запускает проверку и возвращает код завершения.
private fun runApplication(args: Array<String>): Int {
    val parser: InputParser = CliManager()

    return when (val parsed = parser.parse(args)) {
        is ParseResult.ShowHelp -> {
            HelpPrinter().print()
            1 // Справка / неверные аргументы
        }
        is ParseResult.InvalidVolume -> 7 // Ошибка формата объема
        is ParseResult.Success -> {
            val options = parsed.options
            val result = createAccessControl().check(
                options.login, options.password, options.action, options.resource, options.volume
            )
            toExitCode(result)
        }
    }
}

private fun createAccessControl(): AccessControlService {
    val database = InMemoryDatabase()
    val authentication = AuthenticationService(database, SecurityService())
    val authorization = AuthorizationService(
        database,
        database,
        listOf(
            ResourceLimitRule(Action.READ),
            ResourceLimitRule(Action.WRITE),
            ResourceLimitRule(Action.EXECUTE)
        )
    )
    return AccessControlService(authentication, authorization)
}

// Перевод бизнес-результата в системный код завершения (по ТЗ: 0-8).
private fun toExitCode(result: AccessResult): Int = when (result) {
    AccessResult.SUCCESS -> 0
    AccessResult.WRONG_PASSWORD -> 2
    AccessResult.USER_NOT_FOUND -> 3
    AccessResult.UNKNOWN_ACTION -> 4
    AccessResult.ACCESS_DENIED -> 5
    AccessResult.RESOURCE_NOT_FOUND -> 6
    AccessResult.INVALID_RESOURCE_FORMAT -> 7
    AccessResult.VOLUME_EXCEEDED -> 8
}
