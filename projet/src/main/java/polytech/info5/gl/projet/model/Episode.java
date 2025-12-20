package polytech.info5.gl.projet.model;

import java.util.ArrayList;
import java.util.List;

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

    // Il manque un lien vers la biographie / personnage pour le moment
    public boolean isModifiablePar(Utilisateur utilisateur) {
        return statut != StatutEpisode.VALIDE;
    }

    public boolean validerParJoueur(Utilisateur utilisateur) {
        this.isValideParJoueur = true;
        if (isCompletementValide()) statut = StatutEpisode.VALIDE;
        return true;
    }

    public boolean validerParMJ(Utilisateur utilisateur) {
        this.isValideParMJ = true;
        if (isCompletementValide()) statut = StatutEpisode.VALIDE;
        return true;
    }

    public boolean isCompletementValide() {
        return isValideParMJ && isValideParJoueur;
    }

    public void ajouterParagraphe(Paragraphe p) { paragraphes.add(p); }
    public void retirerParagraphe(Paragraphe p) { paragraphes.remove(p); }
}
