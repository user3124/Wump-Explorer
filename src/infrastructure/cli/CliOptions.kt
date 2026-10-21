package infrastructure.cli

data class CliOptions(
    val login: String,
    val password: String,
    val action: String,
    val resource: String,
    val volume: Int
)
