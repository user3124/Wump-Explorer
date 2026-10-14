import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlin.system.exitProcess

class CliManager {
  fun parseArguments(args: Array<String>): CliOptions? {
    // Проверка на неизвестные флаги
    val validFlags = setOf("-l", "--login", "-p", "--password", "-a", "--action", "-r", "--resource", "-v", "--volume", "-h", "--help")
    for (arg in args) {
      if (arg.startsWith("-") && arg !in validFlags) {
        showHelpAndExit()
      }
    }

    if (args.contains("-h") || args.contains("--help") || args.isEmpty()) {
      showHelpAndExit()
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
      showHelpAndExit()
    }

    val login = loginOpt.value ?: showHelpAndExit()
    val password = passwordOpt.value ?: showHelpAndExit()
    val action = actionOpt.value ?: showHelpAndExit()
    val resource = resourceOpt.value ?: showHelpAndExit()
    val volumeStr = volumeOpt.value ?: showHelpAndExit()

    val volume = volumeStr.toIntOrNull()
    if (volume == null) {
      exitProcess(7) // Ошибка формата объема
    }

    return CliOptions(login, password, action, resource, volume)
  }

  private fun showHelpAndExit(): Nothing {
    println("Usage: app.jar options")
    println("Options:")
    println("    --login, -l -> User's login. (String)")
    println("    --password, -p -> User's password. (String)")
    println("    --action, -a -> User's action. (String)")
    println("    --resource, -r -> Requested resource name. (String)")
    println("    --volume, -v -> Volume of requested resource. (Int)")
    println("    --help, -h -> Usage info")
    exitProcess(1)
  }
}

data class CliOptions(
  val login: String,
  val password: String,
  val action: String,
  val resource: String,
  val volume: Int
)
