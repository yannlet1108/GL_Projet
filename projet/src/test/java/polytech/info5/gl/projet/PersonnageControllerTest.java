package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.model.*;
import java.util.List;

public class PersonnageControllerTest {

    @Test
    public void testCreerListerEtModifierProfession() {
        PersonnageController pc = new PersonnageController();
        Utilisateur u = new Utilisateur(50, "U", "u@e", "p");
        Personnage p = pc.creerPersonnage("NomP", "NA", "Mage", "Bio init", u);

        assertNotNull(p);
        assertEquals("NomP", p.getNom());

        List<Personnage> tous = pc.listerTous();
        assertEquals(1, tous.size());

        List<Personnage> parUser = pc.listerParUtilisateur(u);
        assertEquals(1, parUser.size());

        boolean modif = pc.modifierProfession(p.getId(), "Paladin", u);
        assertTrue(modif);
        assertEquals("Paladin", pc.findById(p.getId()).get().getProfession());
    }

    @Test
    public void testCasAuxLimitesEtAcces() {
        PersonnageController pc = new PersonnageController();
        Utilisateur u = new Utilisateur(60, "U2", "u2@e", "p");

        // findById absent
        assertFalse(pc.findById(999).isPresent());

        // créer sans biographie initiale
        Personnage p2 = pc.creerPersonnage("P2", "NA", "Voleur", null, u);
        // pas d'épisode initial
        assertTrue(p2.getBiographie().getEpisodes().isEmpty());

        // modifier profession non autorisé
        Utilisateur autre = new Utilisateur(999, "X", "x@e", "p");
        boolean ok = pc.modifierProfession(p2.getId(), "Barde", autre);
        assertFalse(ok);

        // listerParUtilisateur avec null retourne liste vide
        assertTrue(pc.listerParUtilisateur(null).isEmpty());
    }
}
