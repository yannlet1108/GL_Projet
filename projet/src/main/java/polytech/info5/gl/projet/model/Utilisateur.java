package polytech.info5.gl.projet.model;

public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String passwordHash;

    public Utilisateur() {}

    public Utilisateur(int id, String nom, String email, String passwordHash) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public boolean connecter(String mdp) {
        return passwordHash != null && passwordHash.equals(mdp);
    }
}
