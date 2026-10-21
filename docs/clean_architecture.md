# Перевод проекта на Чистую Архитектуру

В исходном проекте все лежало в одной папке `src`. Для перехода на чистую архитектуру код разделен на слои, чтобы внешние детали (консоль, хранилище) не влияли на бизнес-логику.

## Слои приложения

1. **Центральный слой (Entities)** — `domain/models/`: `User`, `Resource`, `Permission`, `Action`. Ничего не знают ни про консоль, ни про хранилище, ни про алгоритмы.

2. **Слой бизнес-логики (Services)** — `services/`: аутентификация, авторизация, хэширование, порядок проверок. Работает только с сущностями и интерфейсами. Не знает про `kotlinx-cli`, `println` и `exitProcess`.

3. **Внешний слой (Infrastructure)** — `infrastructure/`: разбор командной строки (`cli/`) и база в памяти (`database/`).

**Правило зависимостей:** стрелки идут только внутрь: `infrastructure → services → domain`. Сервисы объявляют интерфейсы (`UserRepository` и т.д.), инфраструктура их реализует (DIP). `Main.kt` знает обо всех слоях: он только собирает объекты вместе.

## Граница между бизнес-результатом и кодом завершения

В старом `Main.kt` каждая проверка при ошибке вызывала `exitProcess(N)`. Бизнес-логику нельзя было переиспользовать: любая ошибка убивала процесс.

Теперь граница проходит так:

* Сервисы **возвращают** `AccessResult` (перечисление: `SUCCESS`, `USER_NOT_FOUND`, `WRONG_PASSWORD`, `UNKNOWN_ACTION`, `INVALID_RESOURCE_FORMAT`, `RESOURCE_NOT_FOUND`, `ACCESS_DENIED`, `VOLUME_EXCEEDED`). Слов «код выхода» в сервисах нет.
* `Main.kt` **переводит** результат в системный код и вызывает `exitProcess`. Это единственное место с `exitProcess` во всем проекте.

| `AccessResult` | Код |
|----------------|-----|
| `SUCCESS` | 0 |
| `WRONG_PASSWORD` | 2 |
| `USER_NOT_FOUND` | 3 |
| `UNKNOWN_ACTION` | 4 |
| `ACCESS_DENIED` | 5 |
| `RESOURCE_NOT_FOUND` | 6 |
| `INVALID_RESOURCE_FORMAT` | 7 |
| `VOLUME_EXCEEDED` | 8 |

Код 1 (справка / неверные аргументы) и код 7 для нечислового `--volume` относятся к разбору командной строки: их `Main` получает от парсера в виде `ParseResult.ShowHelp` и `ParseResult.InvalidVolume`. Так парсер тоже не вызывает `exitProcess` и не печатает справку сам.

Для GUI или Web достаточно написать свой аналог `Main`: он вызовет `AccessControlService.check(...)` и переведет `AccessResult` в то, что нужно ему (окно, HTTP-статус).

## Интерфейсы

```kotlin
interface UserRepository {
    fun findByLogin(login: String): User?
}

interface ResourceRepository {
    fun findByPath(path: String): Resource?
}

interface PermissionRepository {
    fun find(login: String, resource: String, action: Action): Permission?
}
```

**Почему нет `hasPermission` в репозитории.** В первой версии плана был метод `ResourceRepository.hasPermission(login, path, action)`. Он вынуждал хранилище знать правило наследования прав (право на `A` действует на `A.B.C`). Тогда правило оказывалось в инфраструктуре, и при смене хранения (файл, БД) его пришлось бы переписывать заново. Теперь:

* **Репозиторий отдает данные.** `PermissionRepository.find` ищет право ровно на указанный ресурс и больше ничего не делает.
* **Бизнес-компонент применяет правило.** `AuthorizationService` сам поднимается по дереву `A.B.C → A.B → A`, вызывая `find` на каждом уровне.

Смена хранилища затрагивает только реализацию интерфейсов; правило наследования остается в одном месте.

Права вынесены в отдельный `PermissionRepository`, а не оставлены в `ResourceRepository`: это другие данные с другой причиной изменения (права выдаются и отзываются чаще, чем меняется набор ресурсов).

**`InputParser`** остался интерфейсом, но лежит в `infrastructure/cli/`, а не в `services/interfaces/`. Сервисам парсер не нужен, его использует только `Main`; если бы интерфейс лежал в `services`, а возвращал типы из `infrastructure`, зависимость пошла бы наружу и нарушила бы правило слоев.

```kotlin
interface InputParser {
    fun parse(args: Array<String>): ParseResult
}
```

## Сервисы и их обязанности

| Класс | Что делает | Чего не делает |
|-------|-----------|----------------|
| `SecurityService` | считает SHA-256 хэш пароля с солью | не ищет пользователей |
| `AuthenticationService` | находит пользователя, сверяет хэш | ничего не знает про ресурсы |
| `AuthorizationService` | действие, формат и существование ресурса, наследование прав, лимит объема | не проверяет пароли, не завершает процесс |
| `AccessControlService` | задает порядок: сначала аутентификация, потом авторизация | не содержит самих правил |

Правила конкретных действий вынесены в `services/rules/` (`ActionRule`, `ResourceLimitRule`), см. `solid_analysis.md`.

## Итоговая структура

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
│   │   └── PermissionRepository.kt
│   ├── rules/
│   │   ├── ActionRule.kt
│   │   └── ResourceLimitRule.kt
│   ├── AccessResult.kt
│   ├── SecurityService.kt
│   ├── AuthenticationService.kt
│   ├── AuthorizationService.kt
│   └── AccessControlService.kt
├── infrastructure/
│   ├── cli/
│   │   ├── CliManager.kt
│   │   ├── CliOptions.kt
│   │   ├── HelpPrinter.kt
│   │   ├── InputParser.kt
│   │   └── ParseResult.kt
│   └── database/
│       └── InMemoryDatabase.kt
└── Main.kt
```

Сборка по-прежнему без Gradle/Maven: `scripts/assembly.sh` передает `kotlinc` всю папку `src`, компилятор сам обходит вложенные каталоги.
