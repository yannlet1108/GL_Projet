package polytech.info5.gl.projet.view;

import polytech.info5.gl.projet.model.Episode;

/** Vue console pour afficher un épisode. */
public class VueEpisode {
    public void afficher(Episode e) {
        if (e == null) { System.out.println("Episode introuvable"); return; }
        System.out.println("--- Episode ---");
        System.out.println("Titre: " + e.getTitre());
        System.out.println("Date relative: " + e.getDateRelative());
    }
}
