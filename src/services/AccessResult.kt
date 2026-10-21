package services

// Бизнес-результат проверки. Сервисы возвращают его и ничего не знают
// про коды завершения процесса: перевод в код делает Main.
enum class AccessResult {
    SUCCESS,
    USER_NOT_FOUND,
    WRONG_PASSWORD,
    UNKNOWN_ACTION,
    INVALID_RESOURCE_FORMAT,
    RESOURCE_NOT_FOUND,
    ACCESS_DENIED,
    VOLUME_EXCEEDED
}
