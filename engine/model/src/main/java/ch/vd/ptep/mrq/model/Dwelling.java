package ch.vd.ptep.mrq.model;

import java.time.LocalDate;

/**
 * Logement (Dwelling/Apartment)
 */
public record Dwelling(
    Integer EWID,         // Identificateur fédéral de logement
    Integer WSTAT,        // Statut du logement
    Integer WBAUJ,        // Année de construction du logement
    Integer WABBJ,        // Année de démolition du logement
    String WHGNR,         // Numéro administratif de logement
    String WEINR,         // Numéro physique de logement
    Integer WSTWK,        // Etage
    Boolean WMEHRG,       // Logement sur plusieurs étages
    String WBEZ,          // Situation sur l'étage
    Integer WAZIM,        // Nombre de pièces
    Integer WAREA,        // Surface du logement
    Boolean WKCHE,        // Equipement de cuisine
    Integer WNART,        // Affectation du logement
    Integer WNARTSCE,     // Source d'information de l'affectation
    LocalDate WNARTDAT    // Date d'actualisation de l'affectation
) {

}
