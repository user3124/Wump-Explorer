package domain.models

// Право пользователя на действие над конкретным ресурсом (без учёта наследования)
data class Permission(
    val login: String,
    val resource: String,
    val action: Action
)
