package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.model.*;

public class UtilisateurTest {

    @Test
    public void testConnecterEtGettersSetters() {
        Utilisateur u = new Utilisateur(7, "Nom", "e@e", "secret");
        assertTrue(u.connecter("secret"));
        assertFalse(u.connecter("wrong"));

        u.setEmail("new@e");
        assertEquals("new@e", u.getEmail());
    }
}
