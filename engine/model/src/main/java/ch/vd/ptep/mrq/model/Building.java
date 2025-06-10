package ch.vd.ptep.mrq.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Bâtiment (Building)
 */
public record Building(
    Long EGID,                // Identificateur fédéral de bâtiment
    Integer GGDENR,           // Numéro OFS de la commune
    String GPARZ,             // Parcelle principale
    String GEBNR,             // Numéro officiel de bâtiment
    String GBEZ,              // Nom du bâtiment
    Double GKODE,             // Coordonnée E du bâtiment
    Double GKODN,             // Coordonnée N du bâtiment
    Integer GKSCE,            // Provenance des coordonnées
    Integer GSTAT,            // Statut du bâtiment
    Integer GKAT,             // Catégorie de bâtiment
    Integer GKLAS,            // Classe de bâtiment
    Integer GAREA,            // Surface du bâtiment
    Integer GVOL,             // Volume du bâtiment
    Integer GVOLNORM,         // Volume du bâtiment : norme
    Integer GVOLSCE,          // Volume du bâtiment : indication sur la donnée
    Integer GASTW,            // Nombre de niveaux
    Integer GAZZI,            // Nombre de pièces d'habitation indépendantes
    Boolean GSCHUTZR,         // Abri de protection civile
    Integer GEBF,             // Surface de référence énergétique
    Integer GBAUJ,            // Année de construction du bâtiment
    Integer GBAUM,            // Mois de construction du bâtiment
    Integer GABBJ,            // Année de démolition du bâtiment
    Integer GABBM,            // Mois de démolition du bâtiment
    Integer GWAERZH1,         // Générateur de chaleur pour le chauffage 1
    Integer GENH1,            // Source d'énergie / de chaleur du chauffage 1
    Integer GWAERSCEH1,       // Source d'information pour le chauffage 1
    LocalDate GWAERDATH1,     // Date de mise à jour pour le chauffage 1
    Integer GWAERZH2,         // Générateur de chaleur pour le chauffage 2
    Integer GENH2,            // Source d'énergie / de chaleur du chauffage 2
    Integer GWAERSCEH2,       // Source d'information pour le chauffage 2
    LocalDate GWAERDATH2,     // Date de mise à jour pour le chauffage 2
    Integer GWAERZW1,         // Générateur de chaleur pour l'eau chaude 1
    Integer GENW1,            // Source d'énergie / de chaleur de l'eau chaude 1
    Integer GWAERSCEW1,       // Source d'information pour l'eau chaude 1
    LocalDate GWAERDATW1,     // Date de mise à jour pour l'eau chaude 1
    Integer GWAERZW2,         // Générateur de chaleur pour l'eau chaude 2
    Integer GENW2,            // Source d'énergie / de chaleur de l'eau chaude 2
    Integer GWAERSCEW2,       // Source d'information pour l'eau chaude 2
    LocalDate GWAERDATW2,     // Date de mise à jour pour l'eau chaude 2

    List<Entrance> entrances,
    List<RealEstate> realEstates
) {

}
