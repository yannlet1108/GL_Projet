package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.model.*;

public class SequenceTest {

    @Test
    public void testCreationPersonnageEtPartieEtEpisode() {
        // Création utilisateur et personnage via controller
        PersonnageController pc = new PersonnageController();
        Utilisateur u = new Utilisateur(100, "Alice", "a@e", "pw");
        Personnage pers = pc.creerPersonnage("Heroine", "0", "Archer", "Intro", u);

        // Création d'une partie et ajout du personnage
        Univers univers = new Univers(9, "Terres", "desc");
        pers.setUnivers(univers);

        Partie partie = new Partie();
        partie.setUnivers(univers);
        partie.ajouterPersonnage(pers);
        assertEquals(1, partie.getPersonnages().size());

        // Terminer la partie transforme en Aventure
        Aventure a = partie.terminerPartie("Grande aventure");
        assertTrue(partie.isTerminee());
        assertEquals("Grande aventure", a.getResume());

        // Créer un épisode lié à l'aventure et l'ajouter à la biographie
        Episode ep = new Episode();
        ep.setTitre("Episode 1");
        ep.setAventure(a);
        pers.getBiographie().ajouterEpisode(ep);

        // Le personnage doit maintenant référencer l'aventure via getAventures
        assertEquals(1, pers.getAventures().size());
        assertEquals(a.getResume(), pers.getAventures().get(0).getResume());
    }

    @Test
    public void testFlowChangementMJEchecEtSuccesEtCession() {
        PersonnageController pc = new PersonnageController();
        Utilisateur owner = new Utilisateur(200, "Owner", "o@e", "p");
        Utilisateur mj = new Utilisateur(201, "MJ", "m@e", "p");
        Utilisateur newMJ = new Utilisateur(202, "MJ2", "m2@e", "p");
        Utilisateur attacker = new Utilisateur(203, "Att", "a@e", "p");

        Personnage pers = pc.creerPersonnage("PersX", "X", "Clerc", "bio", owner);
        pers.setMJ(mj);

        // demander changement de MJ
        pers.demanderChangementMJ(newMJ);
        // tentative d'accepter par un utilisateur non autorisé
        assertFalse(pers.accepterChangementMJ(attacker));
        // acceptation correcte
        assertTrue(pers.accepterChangementMJ(newMJ));

        // tentative de cession par non-propriétaire
        Utilisateur v = new Utilisateur(300, "V", "v@e", "p");
        assertFalse(pers.cederPersonnage(v, attacker));
        // cession par propriétaire réussit
        assertTrue(pers.cederPersonnage(v, owner));
        assertEquals(v.getId(), pers.getJoueur().getId());
    }
}
