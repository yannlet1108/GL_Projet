package polytech.info5.gl.projet.model;

/** Paragraphe d'un épisode, peut être public ou secret. */
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

    /** Indique si le paragraphe est public. */
    public boolean isPubliqueVisible() { return isPublique; }

    /** Vérifie si le paragraphe est visible par l'utilisateur (stub).
     *  Dans la vraie application, on vérifierait droits du joueur/MJ. */
    public boolean isVisiblePar(Utilisateur utilisateur) {
        return isPublique;
    }

    /** Rend le paragraphe public si l'utilisateur est autorisé (stub). */
    public boolean rendrePublic(Utilisateur utilisateur) {
        // Demander confirmation et vérifier droits dans une vraie app
        if (!isPublique) {
            isPublique = true;
            return true;
        }
        return false;
    }
}
