package services.rules

import domain.models.Action
import domain.models.Resource

// Обычное правило: объём не должен превышать лимит ресурса.
class ResourceLimitRule(override val action: Action) : ActionRule {
    override fun isVolumeAllowed(resource: Resource, volume: Int): Boolean {
        return volume <= resource.maxVolume
    }
}
