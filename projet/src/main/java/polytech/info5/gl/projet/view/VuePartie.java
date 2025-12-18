package polytech.info5.gl.projet.view;

import polytech.info5.gl.projet.model.Partie;

/** Vue console pour une partie. */
public class VuePartie {
    public void afficher(Partie p) {
        if (p == null) { System.out.println("Partie introuvable"); return; }
        System.out.println("--- Partie: " + p.getTitre() + " ---");
        System.out.println("Lieu: " + p.getLieu() + " | Date: " + p.getDate());
    }
}
