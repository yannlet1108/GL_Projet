package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.model.*;

public class AventureTest {

    @Test
    public void testAjouterPersonnageEtGetters() {
        Aventure a = new Aventure();
        a.setTitre("Quete");
        Univers u = new Univers(2, "U2", "d");
        a.setUnivers(u);
        Personnage p = new Personnage();
        p.setNom("Hero");
        a.ajouterPersonnage(p);

        assertEquals("Quete", a.getTitre());
        assertEquals(u.getNom(), a.getUnivers().getNom());
        assertEquals(1, a.getPersonnages().size());
    }
}
