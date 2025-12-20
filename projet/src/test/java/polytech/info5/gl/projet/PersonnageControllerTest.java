package polytech.info5.gl.projet;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Utilisateur;

public class PersonnageControllerTest {

    @Test
    public void testCreerListerEtModifierProfession() {
        PersonnageController pc = new PersonnageController();
        Utilisateur u = new Utilisateur(50, "U", "u@e", "p");
        Personnage p = pc.creerPersonnage("NomP", "NA", "Mage", "Bio init", u);

        assertNotNull(p);
        assertEquals("NomP", p.getNom());

        List<Personnage> personnages = pc.listerTous();
        assertEquals(1, personnages.size());

        List<Personnage> pourBonUtilisateur = pc.listerParUtilisateur(u);
        assertEquals(1, pourBonUtilisateur.size());

        List<Personnage> pourMauvaisUtilisateur = pc.listerParUtilisateur(new Utilisateur());
        assertEquals(0, pourMauvaisUtilisateur.size());

        boolean modif = pc.modifierProfession(p.getId(), "Sorcier", u);
        assertTrue(modif);
        assertEquals("Sorcier", pc.findById(p.getId()).get().getProfession());
    }
}
