package services.interfaces

import domain.models.Action
import domain.models.Permission

// Хранилище только отдаёт данные. Правило наследования прав здесь НЕ применяется:
// метод ищет право ровно на указанный ресурс, а "подниматься" по A.B.C -> A.B -> A
// умеет AuthorizationService.
interface PermissionRepository {
    fun find(login: String, resource: String, action: Action): Permission?
}
