package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.controller.AuthController;
import polytech.info5.gl.projet.model.Utilisateur;

public class AuthControllerTest {

    @Test
    public void testLoginRegisterLogout() {
        AuthController ac = new AuthController();
        // register then login
        Utilisateur r = ac.register("Nom", "x@y", "pwd");
        assertNotNull(r);
        assertEquals("x@y", r.getEmail());
        assertEquals(r, ac.getUtilisateurConnecte());

        ac.logout();
        assertNull(ac.getUtilisateurConnecte());

        Utilisateur u = ac.login("x@y", "pwd");
        assertNotNull(u);
        assertEquals("x@y", u.getEmail());
        assertEquals(u, ac.getUtilisateurConnecte());
    }
}
