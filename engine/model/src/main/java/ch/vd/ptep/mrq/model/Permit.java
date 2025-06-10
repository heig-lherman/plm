package ch.vd.ptep.mrq.model;

import java.time.LocalDate;

/**
 * Permis de construire (Building Permit)
 */
public record Permit(
    String PBDNR,         // Numéro officiel de dossier de construction
    Integer PBDNRSX,      // Suffixe du numéro officiel de dossier de construction
    LocalDate PDATOK      // Date du permis de construire
) {

}
