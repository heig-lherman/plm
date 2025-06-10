package ch.vd.ptep.mrq.model;

import java.util.List;

/**
 * Entrée de bâtiment (Building Entrance)
 */
public record Entrance(
    Integer EDID,         // Identificateur fédéral de l'entrée
    String DEINR,         // Numéro d'entrée du bâtiment
    Long EGAID,           // Identificateur fédéral d'adresse de bâtiment
    Double DKODE,         // Coordonnée E de l'entrée
    Double DKODN,         // Coordonnée N de l'entrée
    Boolean DOFFADR,      // Adresse officielle
    Boolean STROFFIZIEL,  // Désignation officielle de la rue

    List<Dwelling> dwellings
) {

}
