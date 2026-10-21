package services.interfaces

import domain.models.Resource

interface ResourceRepository {
    fun findByPath(path: String): Resource?
}
