package services

import domain.models.Action
import services.interfaces.PermissionRepository
import services.interfaces.ResourceRepository
import services.rules.ActionRule

// Авторизация: "что тебе можно?". Здесь живут правила доступа:
// формат и существование ресурса, наследование прав по дереву A.B.C, лимит объёма.
class AuthorizationService(
    private val resources: ResourceRepository,
    private val permissions: PermissionRepository,
    private val actionRules: List<ActionRule>
) {
    private val nameRegex = Regex("^[a-zA-Z0-9_]{1,20}$")

    fun authorize(login: String, actionName: String, resourcePath: String, volume: Int): AccessResult {
        val rule = findRule(actionName) ?: return AccessResult.UNKNOWN_ACTION

        if (!isValidPath(resourcePath)) {
            return AccessResult.INVALID_RESOURCE_FORMAT
        }

        val resource = resources.findByPath(resourcePath) ?: return AccessResult.RESOURCE_NOT_FOUND

        if (!hasPermission(login, rule.action, resourcePath)) {
            return AccessResult.ACCESS_DENIED
        }

        if (!rule.isVolumeAllowed(resource, volume)) {
            return AccessResult.VOLUME_EXCEEDED
        }
        return AccessResult.SUCCESS
    }

    private fun findRule(actionName: String): ActionRule? {
        val name = actionName.uppercase()
        return actionRules.find { it.action.name == name }
    }

    private fun isValidPath(path: String): Boolean {
        return path.split(".").all { nameRegex.matches(it) }
    }

    // Право на A.B даёт доступ и к A.B.C: идём вверх по дереву, пока не найдём право.
    private fun hasPermission(login: String, action: Action, resourcePath: String): Boolean {
        var currentPath: String? = resourcePath
        while (currentPath != null) {
            if (permissions.find(login, currentPath, action) != null) {
                return true
            }
            currentPath = parentOf(currentPath)
        }
        return false
    }

    private fun parentOf(path: String): String? {
        val lastDotIndex = path.lastIndexOf('.')
        return if (lastDotIndex == -1) null else path.substring(0, lastDotIndex)
    }
}
