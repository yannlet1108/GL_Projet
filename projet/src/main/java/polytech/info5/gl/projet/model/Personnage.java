package polytech.info5.gl.projet.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class Personnage {
    private int id;
    private String nom;
    private String dateNaissance;
    private String profession;
    private Image portrait;
    private String portraitPath;

    private Utilisateur joueur;
    private Utilisateur MJ;
    private Utilisateur mjEnAttente;
    private boolean isValide = false;
    private Univers univers;
    private Biographie biographie = new Biographie();

    public Personnage() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public Image getPortrait() { return portrait; }
    public void setPortrait(Image portrait) { this.portrait = portrait; }
    public String getPortraitPath() { return portraitPath; }
    public void setPortraitPath(String portraitPath) { this.portraitPath = portraitPath; }
    public Utilisateur getJoueur() { return joueur; }
    public void setJoueur(Utilisateur joueur) { this.joueur = joueur; }
    public Utilisateur getMJ() { return MJ; }
    public void setMJ(Utilisateur MJ) { this.MJ = MJ; }
    public Utilisateur getMjEnAttente() { return mjEnAttente; }
    public void setMjEnAttente(Utilisateur mjEnAttente) { this.mjEnAttente = mjEnAttente; }
    public Univers getUnivers() { return univers; }
    public void setUnivers(Univers univers) { this.univers = univers; }
    public Biographie getBiographie() { return biographie; }

    public boolean appartientAUnivers(Univers univers) {
        return this.univers != null && univers != null && this.univers.getNom().equals(univers.getNom());
    }

    public boolean appartientAJoueur(Utilisateur utilisateur) {
        return joueur != null && utilisateur != null && joueur.getId() == utilisateur.getId();
    }

    public boolean estDansPartieEnCours() {
        // TO DO : Not implemented yet
        return false; 
    }

    public boolean isValide() { return isValide; }

    public void setValide(boolean v) { this.isValide = v; }

    public void changerProfession(String nouvelleProfession) { this.profession = nouvelleProfession; }

    public boolean peutChangerMJ() { return !estDansPartieEnCours(); }

    public void demanderChangementMJ(Utilisateur nouveauMJ) { this.mjEnAttente = nouveauMJ; }

    public boolean accepterChangementMJ(Utilisateur utilisateur) {
        if (mjEnAttente != null && utilisateur != null && mjEnAttente.getId() == utilisateur.getId()) {
            this.MJ = mjEnAttente;
            this.mjEnAttente = null;
            return true;
        }
        return false;
    }

    public boolean validerParMJ(Utilisateur utilisateur) {
        if (mjEnAttente != null && utilisateur != null && mjEnAttente.getId() == utilisateur.getId()) {
            this.MJ = mjEnAttente;
            this.mjEnAttente = null;
            this.isValide = true;
            return true;
        }
        return false;
    }

    public boolean refuserChangementMJ(Utilisateur utilisateur) {
        if (mjEnAttente != null && utilisateur != null && mjEnAttente.getId() == utilisateur.getId()) {
            this.mjEnAttente = null;
            return true;
        }
        return false;
    }

    // Pas besoin d'acceptation de l'autre côté
    public boolean cederPersonnage(Utilisateur nouveauJoueur, Utilisateur demandeur) {
        if (demandeur != null && joueur != null && demandeur.getId() == joueur.getId()) {
            this.joueur = nouveauJoueur;
            return true;
        }
        return false;
    }

    public List<Aventure> getAventures() {
        List<Aventure> res = new ArrayList<>();
        if (biographie != null) {
            for (Episode e : biographie.getEpisodes()) {
                if (e.getAventure() != null && !res.contains(e.getAventure())) res.add(e.getAventure());
            }
        }
        return res;
    }
}
