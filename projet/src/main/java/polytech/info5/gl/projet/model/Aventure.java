package polytech.info5.gl.projet.model;

import java.util.ArrayList;
import java.util.List;

/** Représente une aventure, qui peut regrouper plusieurs personnages. */
public class Aventure {
    private int id;
    private String titre;
    private String resume;
    private Univers univers;
    private List<Personnage> personnages = new ArrayList<>();

    public Aventure() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
    public Univers getUnivers() { return univers; }
    public void setUnivers(Univers univers) { this.univers = univers; }
    public List<Personnage> getPersonnages() { return personnages; }
    public void ajouterPersonnage(Personnage p) { personnages.add(p); }
}
