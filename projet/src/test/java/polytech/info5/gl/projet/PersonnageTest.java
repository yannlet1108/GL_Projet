package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.model.*;

public class PersonnageTest {

    @Test
    public void testAppartenanceUniversEtJoueurEtChangementMJ() {
        Univers u1 = new Univers(1, "U1", "desc");
        Utilisateur joueur = new Utilisateur(10, "J", "j@example", "pwd");
        Utilisateur mj = new Utilisateur(11, "MJ", "mj@example", "pwd");
        Personnage p = new Personnage();
        p.setUnivers(u1);
        p.setJoueur(joueur);
        p.setMJ(mj);

        assertTrue(p.appartientAUnivers(u1));
        assertTrue(p.appartientAJoueur(joueur));

        p.changerProfession("Forgeron");
        assertEquals("Forgeron", p.getProfession());

        Utilisateur nouveauMJ = new Utilisateur(12, "MJ2", "mj2@example", "pwd");
        p.demanderChangementMJ(nouveauMJ);
        assertFalse(p.accepterChangementMJ(mj));
        assertTrue(p.accepterChangementMJ(nouveauMJ));
    }

    @Test
    public void testCederPersonnage() {
        Utilisateur joueur = new Utilisateur(20, "A", "a@e", "p");
        Utilisateur newJ = new Utilisateur(21, "B", "b@e", "p");
        Personnage p = new Personnage();
        p.setJoueur(joueur);

        assertTrue(p.cederPersonnage(newJ, joueur));
        assertEquals(newJ.getId(), p.getJoueur().getId());
    }

    @Test
    public void testCederPersonnageNonAutorise() {
        Utilisateur joueur = new Utilisateur(30, "Owner", "o@e", "p");
        Utilisateur autre = new Utilisateur(31, "Other", "x@e", "p");
        Utilisateur newJ = new Utilisateur(32, "New", "n@e", "p");
        Personnage p = new Personnage();
        p.setJoueur(joueur);

        // tentative de cession par un non-propriétaire
        assertFalse(p.cederPersonnage(newJ, autre));
        assertEquals(joueur.getId(), p.getJoueur().getId());
    }

    @Test
    public void testGetAventuresUniquementUneFois() {
        Personnage p = new Personnage();
        Aventure a = new Aventure();
        a.setResume("res");
        Episode e1 = new Episode(); e1.setAventure(a);
        Episode e2 = new Episode(); e2.setAventure(a);
        p.getBiographie().ajouterEpisode(e1);
        p.getBiographie().ajouterEpisode(e2);

        assertEquals(1, p.getAventures().size());
        assertEquals("res", p.getAventures().get(0).getResume());
    }
}
