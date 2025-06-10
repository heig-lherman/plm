package ch.vd.ptep.mrq.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Projet de construction (Construction Project)
 */
public record Project(
    Long EPROID,          // Identificateur fédéral de projet de construction
    String PBEZ,          // Description du projet de construction
    Integer PARTBZ,       // Base de l'autorisation
    Integer PTYPAG,       // Type de maître d'ouvrage
    Integer PARTBW,       // Genre de construction
    Integer PTYPBW,       // Type d'ouvrage
    Long PKOST,           // Coût total du projet
    LocalDate PDATIN,     // Date de la demande de permis
    LocalDate PDATBB,     // Date du début des travaux
    LocalDate PDATBE,     // Date de fin des travaux
    LocalDate PDATSIST,   // Date de report
    LocalDate PDATABL,    // Date de refus de l'autorisation de construire
    LocalDate PDATANN,    // Date de non réalisation
    LocalDate PDATRZG,    // Date de retrait de la demande de permis
    Integer PVBD,         // Durée prévisionnelle des travaux
    Integer PSTAT,        // Statut du projet de construction
    List<String> PPARZ,   // Immeubles liés au projet
    Integer PGDENR        // Numéro OFS de la commune
) {

}
