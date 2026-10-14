# Перевод проекта на Чистую Архитектуру

В текущем проекте все лежит в одной папке `src`. Для перехода на чистую архитектуру нужно разделить код на логические слои, чтобы внешние детали не влияли на бизнес-логику.

## Выделение слоев приложения

1. **Центральный слой (Бизнес-сущности / Entities)**
    * Сюда относятся наши структуры данных из `Models.kt`: `User`, `Resource`, `Permission`. Они ничего не знают про консоль или алгоритмы.

2. **Слой бизнес-логики (Use Cases / Services)**
    * Сюда идет файл `Main.kt` (проверки аутентификации, хэширования и проверки прав доступа) и `Security.kt` (утилита хэширования). Этот слой работает только с сущностями.

3. **Внешний слой (Инфраструктура / DAO / CLI)**
    * Сюда относится наш `CliManager.kt`, который работает с внешней библиотекой `kotlinx-cli` и принимает аргументы из терминала.

## План реструктуризации

Чтобы слои были независимыми, они должны общаться через интерфейсы (DIP). Сама логика не должна знать, откуда берутся данные и какая библиотека парсит консоль.

### Необходимые интерфейсы и абстракции

* `UserRepository` — чтобы искать пользователя.
* `ResourceRepository` — чтобы проверять существование папок и права доступа.
* `InputParser` — чтобы принимать аргументы командной строки в обход конкретной библиотеки.

```kotlin
interface UserRepository {
    fun findByLogin(login: String): User?
}

interface ResourceRepository {
    fun findByPath(path: String): Resource?
    fun hasPermission(login: String, path: String, action: Action): Boolean
}

interface InputParser {
    fun parse(args: Array<String>): CliOptions?
}
```

### Итоговая структура пакетов проекта

* В `domain/models/` перенесем чистые структуры данных: `User`, `Resource`, `Permission`, `Action`.
* В `services/` создадим подпапку `interfaces/` для контрактов выше, а рядом положим `SecurityService` (код хэширования) и `AccessControlService` (вся цепочка проверок из `Main.kt`).
* В `infrastructure/` перенесем все внешние детали: работу с консолью через `kotlinx-cli` (в `cli/`) и наш объект `Database`, который станет просто базой в памяти (в `database/`).
* Сам `Main.kt` останется в корне: он будет просто собирать эти папки вместе и запускать программу.

После рефакторинга структура каталогов в папке `src/` примет следующий вид:

```text
src/
├── domain/
│   └── models/
│       ├── User.kt
│       ├── Resource.kt
│       ├── Permission.kt
│       └── Action.kt
├── services/
│   ├── interfaces/
│   │   ├── UserRepository.kt
│   │   ├── ResourceRepository.kt
│   │   └── InputParser.kt
│   ├── SecurityService.kt
│   └── AccessControlService.kt
├── infrastructure/
│   ├── cli/
│   │   ├── CliManager.kt
│   │   └── HelpPrinter.kt
│   └── database/
│       └── InMemoryDatabase.kt
└── Main.kt
```
