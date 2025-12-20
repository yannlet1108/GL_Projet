package polytech.info5.gl.projet.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import polytech.info5.gl.projet.model.Utilisateur;

public class AuthController {
    private Utilisateur utilisateurConnecte;

    private final java.util.List<Utilisateur> users = new java.util.ArrayList<>();
    private int nextId = 1;

    public Utilisateur login(String email, String mdp) {
        if (email == null || mdp == null) return null;
        String hashed = hashPassword(mdp);
        for (Utilisateur u : users) {
            if (email.equals(u.getEmail()) && hashed.equals(u.getPasswordHash())) {
                this.utilisateurConnecte = u;
                return u;
            }
        }
        return null;
    }

    public void logout() { utilisateurConnecte = null; }

    public Utilisateur register(String nom, String email, String mdp) {
        if (nom == null || nom.isBlank() || email == null || email.isBlank() || mdp == null) return null;

        for (Utilisateur u : users) {
            if (email.equals(u.getEmail()) || nom.equals(u.getNom())) return null;
        }

        Utilisateur u = new Utilisateur(nextId++, nom, email, hashPassword(mdp));
        users.add(u);
        this.utilisateurConnecte = u;
        return u;
    }

    public Utilisateur getUtilisateurConnecte() { return utilisateurConnecte; }

    public void setUtilisateurConnecte(Utilisateur u) { this.utilisateurConnecte = u; }

    public java.util.List<Utilisateur> getAllUsers() { return new java.util.ArrayList<>(users); }
    public void clearUsers() { users.clear(); nextId = 1; }

    /** Charge une liste d'utilisateurs (utilisé pour la restauration depuis persistence). */
    public void chargerUtilisateurs(java.util.List<Utilisateur> liste) {
        users.clear();
        if (liste != null) users.addAll(liste);
        int max = 0; for (Utilisateur u : users) if (u.getId() > max) max = u.getId();
        this.nextId = max + 1;
    }

    private String hashPassword(String mdp) {
        if (mdp == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(mdp.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return mdp;
        }
    }
}
