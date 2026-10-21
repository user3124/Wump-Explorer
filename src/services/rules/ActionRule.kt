package services.rules

import domain.models.Action
import domain.models.Resource

// Правило конкретного действия. Новое действие со своим правилом = новый класс
// и одна строка регистрации в Main; AuthorizationService при этом не меняется.
interface ActionRule {
    val action: Action
    fun isVolumeAllowed(resource: Resource, volume: Int): Boolean
}
