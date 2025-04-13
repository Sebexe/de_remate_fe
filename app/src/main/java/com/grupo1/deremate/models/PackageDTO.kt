package com.grupo1.deremate.models

data class PackageDTO(
    val id: Long,
    val status: String,
    val packageLocation: String,
    val createdDate: String? = null // o OffsetDateTime si querés usar formato de fecha
)
