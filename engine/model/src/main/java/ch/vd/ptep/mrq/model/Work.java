package ch.vd.ptep.mrq.model;

/**
 * Travaux (Construction Work)
 */
public record Work(
    Long ARBID,           // Identificateur des travaux
    Integer PARTAB,       // Genre de travaux
    Boolean PENSAN,       // Assainissement énergétique
    Boolean PHEIZSAN,     // Assainissement du système de chauffage
    Boolean PINNUMB,      // Transformations / rénovations intérieures
    Boolean PUMNUTZ,      // Changement d'affectation
    Boolean PERWMHZ,      // Agrandissement chauffé
    Boolean PERWOHZ,      // Agrandissement non chauffé
    Boolean PTHERSOL,     // Installation solaire thermique
    Boolean PPHOTSOL,     // Installation solaire photovoltaïque
    Boolean PANDUMB       // Autres travaux de transformation
) {

}
