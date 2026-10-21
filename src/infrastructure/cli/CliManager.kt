package infrastructure.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType

// Только разбор аргументов командной строки. Ничего не печатает и не завершает процесс.
class CliManager : InputParser {
    // Флаги, после которых обязательно идёт значение: флаг -> общее имя параметра
    private val valueFlags = mapOf(
        "-l" to "login", "--login" to "login",
        "-p" to "password", "--password" to "password",
        "-a" to "action", "--action" to "action",
        "-r" to "resource", "--resource" to "resource",
        "-v" to "volume", "--volume" to "volume"
    )
    private val helpFlags = setOf("-h", "--help")

    override fun parse(args: Array<String>): ParseResult {
        if (args.isEmpty() || args.any { it in helpFlags }) {
            return ParseResult.ShowHelp
        }
        // kotlinx-cli при любой ошибке сам вызывает exitProcess(127), а по ТЗ допустимы коды 0-8.
        // Поэтому структуру аргументов проверяем сами ДО передачи библиотеке.
        if (!hasValidStructure(args)) {
            return ParseResult.ShowHelp
        }

        val parser = ArgParser("Wump Explorer")
        val loginOpt = parser.option(ArgType.String, "login", "l", "User's login.")
        val passwordOpt = parser.option(ArgType.String, "password", "p", "User's password.")
        val actionOpt = parser.option(ArgType.String, "action", "a", "User's action.")
        val resourceOpt = parser.option(ArgType.String, "resource", "r", "Requested resource name.")
        val volumeOpt = parser.option(ArgType.String, "volume", "v", "Volume of requested resource.")

        try {
            parser.parse(args)
        } catch (e: Exception) {
            return ParseResult.ShowHelp
        }

        val login = loginOpt.value ?: return ParseResult.ShowHelp
        val password = passwordOpt.value ?: return ParseResult.ShowHelp
        val action = actionOpt.value ?: return ParseResult.ShowHelp
        val resource = resourceOpt.value ?: return ParseResult.ShowHelp
        val volumeStr = volumeOpt.value ?: return ParseResult.ShowHelp

        val volume = volumeStr.toIntOrNull() ?: return ParseResult.InvalidVolume

        return ParseResult.Success(CliOptions(login, password, action, resource, volume))
    }

    // Допустимо только: "флаг значение" пары, флаги известные, без повторов.
    private fun hasValidStructure(args: Array<String>): Boolean {
        val seen = mutableSetOf<String>()
        var i = 0
        while (i < args.size) {
            val paramName = valueFlags[args[i]] ?: return false
            if (!seen.add(paramName)) {
                return false
            }
            val hasValue = i + 1 < args.size && !args[i + 1].startsWith("-")
            if (!hasValue) {
                return false
            }
            i += 2
        }
        return true
    }
}
