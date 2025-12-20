package polytech.info5.gl.projet.model;

public class Paragraphe {
    private int id;
    private int ordre;
    private String texte;
    private boolean isPublique;

    public Paragraphe() {}

    public Paragraphe(int id, int ordre, String texte, boolean isPublique) {
        this.id = id;
        this.ordre = ordre;
        this.texte = texte;
        this.isPublique = isPublique;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
    public String getTexte() { return texte; }
    public void setTexte(String texte) { this.texte = texte; }
    public boolean isPublique() { return isPublique; }
    public void setPublique(boolean publique) { isPublique = publique; }

    // Il manque un lien vers l'épisode / personnage pour le moment
    public boolean isVisiblePar(Utilisateur utilisateur) {
        return isPublique;
    }

    public boolean rendrePublic(Utilisateur utilisateur) {
        if (!isPublique) {
            isPublique = true;
            return true;
        }
        return false;
    }
}
