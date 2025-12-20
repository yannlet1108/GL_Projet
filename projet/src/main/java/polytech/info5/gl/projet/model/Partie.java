package polytech.info5.gl.projet.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Partie {
    private int id;
    private String titre;
    private String situationInitiale;
    private Date date;
    private String lieu;
    private boolean terminee = false;
    private Univers univers;
    private Utilisateur MJ;
    private List<Personnage> personnages = new ArrayList<>();
    private Aventure aventureGeneree;

    public Partie() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getSituationInitiale() { return situationInitiale; }
    public void setSituationInitiale(String situationInitiale) { this.situationInitiale = situationInitiale; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public boolean isTerminee() { return terminee; }
    public void setTerminee(boolean terminee) { this.terminee = terminee; }
    public Univers getUnivers() { return univers; }
    public void setUnivers(Univers univers) { this.univers = univers; }
    public Utilisateur getMJ() { return MJ; }
    public void setMJ(Utilisateur MJ) { this.MJ = MJ; }
    public List<Personnage> getPersonnages() { return personnages; }
    public Aventure getAventureGeneree() { return aventureGeneree; }

    public boolean isPersonnageAjoutable(Personnage personnage) {
        return personnage != null && univers != null && personnage.getUnivers() != null && univers.getNom().equals(personnage.getUnivers().getNom());
    }

    public void ajouterPersonnage(Personnage p) { if (!personnages.contains(p)) personnages.add(p); }
    public void retirerPersonnage(Personnage p) { personnages.remove(p); }

    public Aventure terminerPartie(String resume) {
        this.terminee = true;
        Aventure a = new Aventure();
        a.setResume(resume);
        a.setTitre(this.titre + " - Aventure");
        a.setUnivers(this.univers);
        this.aventureGeneree = a;
        return a;
    }

    public boolean isModifiable() { return !terminee; }

    public void supprimerProposition() {
        personnages.clear();
    }
}
