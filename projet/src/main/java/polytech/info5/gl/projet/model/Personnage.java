package polytech.info5.gl.projet.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/** Représente un personnage joué par un utilisateur. */
public class Personnage {
    private int id;
    private String nom;
    private String dateNaissance;
    private String profession;
    private Image portrait;

    private Utilisateur joueur;
    private Utilisateur MJ;
    private Utilisateur mjEnAttente;
    private boolean isValide = false;
    private Univers univers;
    private Biographie biographie = new Biographie();

    public Personnage() {}

    // Getters / Setters
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
    public Utilisateur getJoueur() { return joueur; }
    public void setJoueur(Utilisateur joueur) { this.joueur = joueur; }
    public Utilisateur getMJ() { return MJ; }
    public void setMJ(Utilisateur MJ) { this.MJ = MJ; }
    public Utilisateur getMjEnAttente() { return mjEnAttente; }
    public void setMjEnAttente(Utilisateur mjEnAttente) { this.mjEnAttente = mjEnAttente; }
    public Univers getUnivers() { return univers; }
    public void setUnivers(Univers univers) { this.univers = univers; }
    public Biographie getBiographie() { return biographie; }

    /** Vérifie si le personnage appartient à l'univers donné. */
    public boolean appartientAUnivers(Univers univers) {
        return this.univers != null && univers != null && this.univers.getNom().equals(univers.getNom());
    }

    /** Vérifie si le personnage appartient au joueur donné. */
    public boolean appartientAJoueur(Utilisateur utilisateur) {
        return joueur != null && utilisateur != null && joueur.getId() == utilisateur.getId();
    }

    /** Compare joueur entre deux personnages. */
    public boolean aMemeJoueur(Personnage personnage) {
        return personnage != null && this.joueur != null && personnage.getJoueur() != null && this.joueur.getId() == personnage.getJoueur().getId();
    }

    /** Indique si le personnage est dans une partie en cours (stub). */
    public boolean estDansPartieEnCours() { return false; }

    public boolean isValide() { return isValide; }

    public void setValide(boolean v) { this.isValide = v; }

    /** Change la profession du personnage. */
    public void changerProfession(String nouvelleProfession) { this.profession = nouvelleProfession; }

    /** Vérifie si on peut changer de MJ (stub). */
    public boolean peutChangerMJ() { return !estDansPartieEnCours(); }

    /** Demande un changement de MJ en plaçant un MJ en attente. */
    public void demanderChangementMJ(Utilisateur nouveauMJ) { this.mjEnAttente = nouveauMJ; }

    /** Accepte le changement de MJ si l'utilisateur correspond au MJ en attente. */
    public boolean accepterChangementMJ(Utilisateur utilisateur) {
        if (mjEnAttente != null && utilisateur != null && mjEnAttente.getId() == utilisateur.getId()) {
            this.MJ = mjEnAttente;
            this.mjEnAttente = null;
            return true;
        }
        return false;
    }

    /** Valide le personnage lors d'une proposition initiale : si l'utilisateur correspond au MJ proposé, il accepte et le personnage est validé. */
    public boolean validerParMJ(Utilisateur utilisateur) {
        if (mjEnAttente != null && utilisateur != null && mjEnAttente.getId() == utilisateur.getId()) {
            this.MJ = mjEnAttente;
            this.mjEnAttente = null;
            this.isValide = true;
            return true;
        }
        return false;
    }

    /** Refuse le changement de MJ si l'utilisateur correspond (stub). */
    public boolean refuserChangementMJ(Utilisateur utilisateur) {
        if (mjEnAttente != null && utilisateur != null && mjEnAttente.getId() == utilisateur.getId()) {
            // proposed MJ refuses the proposal
            this.mjEnAttente = null;
            return true;
        }
        return false;
    }

    /** Cède le personnage à un nouveau joueur si demandé par le propriétaire (stub). */
    public boolean cederPersonnage(Utilisateur nouveauJoueur, Utilisateur demandeur) {
        if (demandeur != null && joueur != null && demandeur.getId() == joueur.getId()) {
            this.joueur = nouveauJoueur;
            return true;
        }
        return false;
    }

    /** Retourne les aventures liées via les épisodes (agrégé). */
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
