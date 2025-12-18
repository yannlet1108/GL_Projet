package polytech.info5.gl.projet.controller;

import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Utilisateur;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Contrôleur pour les opérations sur les personnages (stockage en mémoire). */
public class PersonnageController {

    private final List<Personnage> personnages = new ArrayList<>();
    private int nextId = 1;

    /** Crée un personnage, l'ajoute au stockage en mémoire et le retourne. */
    public Personnage creerPersonnage(String nom, String dateNaissance, String profession, String bioInitiale, Utilisateur utilisateurConnecte) {
        Personnage p = new Personnage();
        p.setId(nextId++);
        p.setNom(nom);
        p.setDateNaissance(dateNaissance);
        p.setProfession(profession);
        p.setJoueur(utilisateurConnecte);
        if (p.getBiographie() != null && bioInitiale != null && !bioInitiale.isBlank()) {
            // créer un épisode initial minimal contenant le paragraphe de biographie
            polytech.info5.gl.projet.model.Episode e = new polytech.info5.gl.projet.model.Episode();
            e.setDateRelative("0");
            e.setTitre("Biographie initiale");
            polytech.info5.gl.projet.model.Paragraphe par = new polytech.info5.gl.projet.model.Paragraphe();
            par.setOrdre(1);
            par.setTexte(bioInitiale);
            par.setPublique(true);
            e.ajouterParagraphe(par);
            p.getBiographie().ajouterEpisode(e);
        }
        personnages.add(p);
        return p;
    }

    /** Retourne la liste de tous les personnages stockés. */
    public List<Personnage> listerTous() { return new ArrayList<>(personnages); }

    /** Retourne la liste des personnages appartenant à un utilisateur. */
    public List<Personnage> listerParUtilisateur(Utilisateur u) {
        List<Personnage> res = new ArrayList<>();
        if (u == null) return res;
        for (Personnage p : personnages) {
            if (p.getJoueur() != null && p.getJoueur().getId() == u.getId()) res.add(p);
        }
        return res;
    }

    /** Trouve un personnage par son id. */
    public Optional<Personnage> findById(int id) {
        return personnages.stream().filter(p -> p.getId() == id).findFirst();
    }

    /** Remplace la liste interne de personnages (utilisé pour le chargement). */
    public void chargerPersonnages(List<Personnage> liste) {
        this.personnages.clear();
        if (liste != null) this.personnages.addAll(liste);
        // recalculer nextId
        int max = 0; for (Personnage p : personnages) if (p.getId() > max) max = p.getId();
        this.nextId = max + 1;
    }

    /** Affiche le personnage via la vue (stub) */
    public void afficherPersonnage(int idPers, Utilisateur utilisateurConnecte) {
        Optional<Personnage> p = findById(idPers);
        p.ifPresent(personnage -> {
            new polytech.info5.gl.projet.view.VuePersonnage().afficher(personnage);
            new polytech.info5.gl.projet.view.VueBiographie().afficher(personnage.getBiographie());
        });
    }

    /** Modifie la profession si autorisé. */
    public boolean modifierProfession(int idPers, String nvProfession, Utilisateur utilisateurConnecte) {
        Optional<Personnage> p = findById(idPers);
        if (p.isPresent() && utilisateurConnecte != null && p.get().getJoueur() != null && p.get().getJoueur().getId() == utilisateurConnecte.getId()) {
            p.get().changerProfession(nvProfession);
            return true;
        }
        return false;
    }

    public boolean cederPersonnage(int idPers, int idNewJoueur, Utilisateur utilisateurConnecte) { return false; }
    public boolean demanderChangementMJ(int idPers, int idNewMJ, Utilisateur utilisateurConnecte) { return false; }
    public boolean accepterChangementMJ(int idPers, Utilisateur utilisateurConnecte) { return false; }
    public boolean refuserChangementMJ(int idPers, Utilisateur utilisateurConnecte) { return false; }
}
