package infrastructure.cli

// Лежит рядом с CliManager, а не в services: сервисам парсер не нужен,
// его использует только Main.
interface InputParser {
    fun parse(args: Array<String>): ParseResult
}
