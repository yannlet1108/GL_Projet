package polytech.info5.gl.projet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.model.Aventure;
import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.Partie;
import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Univers;
import polytech.info5.gl.projet.model.Utilisateur;

public class SequenceTest {

    @Test
    public void testCreationPersonnageEtPartieEtEpisode() {
        // Création utilisateur et personnage via controller
        PersonnageController pc = new PersonnageController();
        Utilisateur u = new Utilisateur(100, "Alice", "a@e", "mdp");
        Personnage pers = pc.creerPersonnage("Bob", "0", "Archer", "Intro", u);

        Univers univers = new Univers(9, "U", "desc");
        pers.setUnivers(univers);

        Partie partie = new Partie();
        partie.setUnivers(univers);
        partie.ajouterPersonnage(pers);
        assertEquals(1, partie.getPersonnages().size());

        Aventure a = partie.terminerPartie("Grande aventure");
        assertTrue(partie.isTerminee());
        assertEquals("Grande aventure", a.getResume());

        Episode ep = new Episode();
        ep.setTitre("Episode 1");
        ep.setAventure(a);
        pers.getBiographie().ajouterEpisode(ep);

        assertEquals(1, pers.getAventures().size());
        assertEquals(a.getResume(), pers.getAventures().get(0).getResume());
    }
}
