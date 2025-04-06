package com.grupo1.deremate.enums

enum class NeighborhoodsCABA(private val nombre: String) {

    AGRONOMIA("Agronomía"),
    ALMAGRO("Almagro"),
    BALVANERA("Balvanera"),
    BARRACAS("Barracas"),
    BELGRANO("Belgrano"),
    BOEDO("Boedo"),
    CABALLITO("Caballito"),
    CHACARITA("Chacarita"),
    COGHLAN("Coghlan"),
    COLEGIALES("Colegiales"),
    CONSTITUCION("Constitución"),
    FLORES("Flores"),
    FLORESTA("Floresta"),
    LA_BOCA("La Boca"),
    LINIERS("Liniers"),
    MATADEROS("Mataderos"),
    MONSERRAT("Monserrat"),
    MONTE_CASTRO("Monte Castro"),
    NUEVA_POMPEYA("Nueva Pompeya"),
    NUNEZ("Núñez"),
    PALERMO("Palermo"),
    PARQUE_AVELLANEDA("Parque Avellaneda"),
    PARQUE_CHACABUCO("Parque Chacabuco"),
    PARQUE_PATRICIOS("Parque Patricios"),
    PATERNAL("Paternal"),
    PUERTO_MADERO("Puerto Madero"),
    RECOLETA("Recoleta"),
    RETIRO("Retiro"),
    SAAVEDRA("Saavedra"),
    SAN_CRISTOBAL("San Cristóbal"),
    SAN_NICOLAS("San Nicolás"),
    SAN_TELMO("San Telmo"),
    VELEZ_SARSFIELD("Vélez Sarsfield"),
    VERSALLES("Versalles"),
    VILLA_CRESPO("Villa Crespo"),
    VILLA_DEL_PARQUE("Villa del Parque"),
    VILLA_DEVOTO("Villa Devoto"),
    VILLA_GENERAL_MITRE("Villa General Mitre"),
    VILLA_LUGANO("Villa Lugano"),
    VILLA_LURO("Villa Luro"),
    VILLA_ORTUZAR("Villa Ortúzar"),
    VILLA_PUEYRREDON("Villa Pueyrredón"),
    VILLA_REAL("Villa Real"),
    VILLA_RIACHUELO("Villa Riachuelo"),
    VILLA_SANTA_RITA("Villa Santa Rita"),
    VILLA_SOLDATI("Villa Soldati"),
    VILLA_URQUIZA("Villa Urquiza");

    fun getNombre(): String {
        return nombre
    }

    companion object {
        fun getNeighborhoodNames(): List<String> {
            return entries.map { it.nombre }
        }
    }
}