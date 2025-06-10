package ch.vd.ptep.mrq.model;

/**
 * Immeuble (Real Estate / Property)
 */
public record RealEstate(
    String EGRID,         // Identificateur fédéral d'immeuble
    String LPARZ,         // Numéro d'immeuble
    Integer LGBKR,        // Numéro de secteur du registre foncier
    Integer LTYP          // Type d'immeuble
) {

}
