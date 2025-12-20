package polytech.info5.gl.projet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Univers;
import polytech.info5.gl.projet.model.Utilisateur;

public class PersonnageTest {

    @Test
    public void testAppartenanceUniversEtJoueurEtChangementMJ() {
        Univers u1 = new Univers(1, "U1", "desc");
        Utilisateur joueur = new Utilisateur(10, "J", "j@example", "mdp");
        Utilisateur mj = new Utilisateur(11, "MJ", "mj@example", "mdp");
        Personnage p = new Personnage();
        p.setUnivers(u1);
        p.setJoueur(joueur);
        p.setMJ(mj);

        assertTrue(p.appartientAUnivers(u1));
        assertTrue(p.appartientAJoueur(joueur));

        p.changerProfession("Forgeron");
        assertEquals("Forgeron", p.getProfession());

        Utilisateur nouveauMJ = new Utilisateur(12, "MJ2", "mj2@example", "mdp");
        p.demanderChangementMJ(nouveauMJ);
        assertFalse(p.accepterChangementMJ(mj));
        assertTrue(p.accepterChangementMJ(nouveauMJ));
        assertEquals(nouveauMJ.getId(), p.getMJ().getId());
    }

    @Test
    public void testCederPersonnage() {
        Utilisateur joueur = new Utilisateur(20, "A", "a@e", "mdp");
        Utilisateur newJ = new Utilisateur(21, "B", "b@e", "mdp");
        Personnage p = new Personnage();
        p.setJoueur(joueur);

        assertTrue(p.cederPersonnage(newJ, joueur));
        assertEquals(newJ.getId(), p.getJoueur().getId());
    }

    @Test
    public void testCederPersonnageNonAutorise() {
        Utilisateur joueur = new Utilisateur(30, "Owner", "o@e", "mdp");
        Utilisateur autre = new Utilisateur(31, "Other", "x@e", "mdp");
        Utilisateur newJ = new Utilisateur(32, "New", "n@e", "mdp");
        Personnage p = new Personnage();
        p.setJoueur(joueur);

        // tentative de cession par un non-propriétaire
        assertFalse(p.cederPersonnage(newJ, autre));
        assertEquals(joueur.getId(), p.getJoueur().getId());
    }
}
