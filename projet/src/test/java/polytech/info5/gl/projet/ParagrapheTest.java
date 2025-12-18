package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.model.*;

public class ParagrapheTest {

    @Test
    public void testVisibiliteEtRendrePublic() {
        Utilisateur u = new Utilisateur();
        Paragraphe p = new Paragraphe(5, 1, "Secret texte", false);
        assertFalse(p.isPubliqueVisible());
        assertFalse(p.isVisiblePar(u));

        assertTrue(p.rendrePublic(u));
        assertTrue(p.isPubliqueVisible());
        assertTrue(p.isVisiblePar(u));

        // rendre public déjà public retourne false
        assertFalse(p.rendrePublic(u));
    }

    @Test
    public void testVisibiliteAvantApres() {
        Paragraphe secret = new Paragraphe(6, 1, "S", false);
        Paragraphe pub = new Paragraphe(7, 2, "P", true);
        Utilisateur u = new Utilisateur();

        assertFalse(secret.isVisiblePar(u));
        assertTrue(pub.isVisiblePar(u));

        // rendre secret public
        assertTrue(secret.rendrePublic(u));
        assertTrue(secret.isVisiblePar(u));
    }
}
