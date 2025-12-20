package polytech.info5.gl.projet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;

import polytech.info5.gl.projet.controller.AuthController;
import polytech.info5.gl.projet.model.Utilisateur;

public class AuthControllerTest {

    @Test
    public void testLoginRegisterLogout() {
        AuthController ac = new AuthController();
        // register then login
        Utilisateur r = ac.register("Nom", "x@y", "mdp");
        assertNotNull(r);
        assertEquals("x@y", r.getEmail());
        assertEquals(r, ac.getUtilisateurConnecte());

        ac.logout();
        assertNull(ac.getUtilisateurConnecte());

        Utilisateur u = ac.login("x@y", "mdp");
        assertNotNull(u);
        assertEquals("x@y", u.getEmail());
        assertEquals(u, ac.getUtilisateurConnecte());
    }
}
