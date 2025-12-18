package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.model.*;
import java.util.Date;

public class PartieTest {

    @Test
    public void testAjouterPersonnageEtTerminer() {
        Univers u = new Univers(1, "U", "d");
        Utilisateur mj = new Utilisateur(1, "MJ", "mj@e", "p");
        Personnage pers = new Personnage();
        pers.setUnivers(u);

        Partie partie = new Partie();
        partie.setUnivers(u);
        partie.setMJ(mj);
        assertTrue(partie.isPersonnageAjoutable(pers));

        partie.ajouterPersonnage(pers);
        assertEquals(1, partie.getPersonnages().size());

        Aventure a = partie.terminerPartie("Résumé");
        assertTrue(partie.isTerminee());
        assertNotNull(a);
        assertEquals(partie.getUnivers().getNom(), a.getUnivers().getNom());
    }

    @Test
    public void testSupprimerProposition() {
        Partie p = new Partie();
        Personnage pers = new Personnage();
        p.ajouterPersonnage(pers);
        p.supprimerProposition();
        assertEquals(0, p.getPersonnages().size());
    }
}
