package polytech.info5.gl.projet.view;

import polytech.info5.gl.projet.model.Biographie;

/** Vue pour afficher une biographie (console). */
public class VueBiographie {
    public void afficher(Biographie b) {
        if (b == null) { System.out.println("Biographie introuvable"); return; }
        System.out.println("--- Biographie du personnage ---");
        b.getEpisodes().forEach(e -> System.out.println(e.getTitre() + " (" + e.getDateRelative() + ")"));
    }
}
