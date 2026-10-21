package domain.models

data class User(
    val login: String,
    val hash: String,
    val salt: String
)
