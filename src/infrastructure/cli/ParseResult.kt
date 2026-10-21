package infrastructure.cli

// Результат разбора аргументов. Парсер не печатает и не завершает процесс:
// он сообщает, что получилось, а Main решает, что с этим делать.
sealed class ParseResult {
    data class Success(val options: CliOptions) : ParseResult()
    object ShowHelp : ParseResult()      // -h, нет аргументов, неизвестный флаг, не хватает значений
    object InvalidVolume : ParseResult() // --volume не является целым числом
}
