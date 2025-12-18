package polytech.info5.gl.projet.view;

import polytech.info5.gl.projet.model.Personnage;

/** Vue basique pour afficher un personnage dans la console. */
public class VuePersonnage {
    public void afficher(Personnage p) {
        if (p == null) {
            System.out.println("Personnage introuvable");
            return;
        }
        System.out.println("--- Personnage ---");
        System.out.println("Nom: " + p.getNom());
        System.out.println("Profession: " + p.getProfession());
    }
}
