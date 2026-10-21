package services

import java.security.MessageDigest

class SecurityService {
    // Берёт пароль и соль, соединяет их и возвращает SHA-256 хэш в виде строки
    fun hashPassword(password: String, salt: String): String {
        val input = password + salt
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))

        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
