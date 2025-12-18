package polytech.info5.gl.projet.view;

import polytech.info5.gl.projet.model.Utilisateur;

/** Vue basique pour la connexion. */
public class VueConnexion {
    public void afficherUtilisateurConnecte(Utilisateur u) {
        if (u == null) System.out.println("Aucun utilisateur connecté");
        else System.out.println("Utilisateur connecté: " + u.getNom() + " <" + u.getEmail() + ">");
    }
}
