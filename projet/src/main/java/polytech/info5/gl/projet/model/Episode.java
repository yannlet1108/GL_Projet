package polytech.info5.gl.projet.model;

import java.util.ArrayList;
import java.util.List;

/** Représente un épisode de la biographie d'un personnage. */
public class Episode {
    private int id;
    private String titre;
    private String dateRelative;
    private boolean isValideParMJ;
    private boolean isValideParJoueur;
    private List<Paragraphe> paragraphes = new ArrayList<>();
    private Aventure aventure;
    private StatutEpisode statut = StatutEpisode.BROUILLON;

    public Episode() {}

    // Getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDateRelative() { return dateRelative; }
    public void setDateRelative(String dateRelative) { this.dateRelative = dateRelative; }
    public boolean isValideParMJ() { return isValideParMJ; }
    public boolean isValideParJoueur() { return isValideParJoueur; }
    public List<Paragraphe> getParagraphes() { return paragraphes; }
    public Aventure getAventure() { return aventure; }
    public void setAventure(Aventure aventure) { this.aventure = aventure; }
    public StatutEpisode getStatut() { return statut; }
    public void setStatut(StatutEpisode statut) { this.statut = statut; }

    /** Vérifie si l'utilisateur peut modifier cet épisode (stub). */
    public boolean isModifiablePar(Utilisateur utilisateur) {
        // Dans une vraie app vérifier rôles et ownership
        return statut != StatutEpisode.VALIDE;
    }

    /** Valide l'épisode par le joueur. */
    public boolean validerParJoueur(Utilisateur utilisateur) {
        this.isValideParJoueur = true;
        if (isCompletementValide()) statut = StatutEpisode.VALIDE;
        return true;
    }

    /** Valide l'épisode par le MJ. */
    public boolean validerParMJ(Utilisateur utilisateur) {
        this.isValideParMJ = true;
        if (isCompletementValide()) statut = StatutEpisode.VALIDE;
        return true;
    }

    /** Retourne true si validé à la fois par MJ et joueur. */
    public boolean isCompletementValide() {
        return isValideParMJ && isValideParJoueur;
    }

    /** Ajoute un paragraphe à l'épisode. */
    public void ajouterParagraphe(Paragraphe p) { paragraphes.add(p); }

    /** Retire un paragraphe de l'épisode. */
    public void retirerParagraphe(Paragraphe p) { paragraphes.remove(p); }
}
