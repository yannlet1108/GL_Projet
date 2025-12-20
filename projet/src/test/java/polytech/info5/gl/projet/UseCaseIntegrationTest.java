package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;

import polytech.info5.gl.projet.controller.*;
import polytech.info5.gl.projet.model.*;

public class UseCaseIntegrationTest {

    @Test
    public void fullFlowCreateCharacterAndParty() {
        AuthController auth = new AuthController();
        PersonnageController pc = new PersonnageController();
        EpisodeController ec = new EpisodeController(pc);
        PartieController partCtrl = new PartieController();

        Utilisateur alice = auth.register("Alice", "alice@example.com", "pw");
        Utilisateur bob = auth.register("Bob", "bob@example.com", "pw");

        // Alice creates a character and proposes Bob as MJ
        Personnage pers = pc.creerPersonnageAvecMJ("Hero", "0", "Warrior", "Born in field", bob.getId(), alice);
        assertNotNull(pers);
        assertEquals((int)pers.getMjEnAttente().getId(), bob.getId());

        // Bob accepts the MJ proposal
        boolean acc = pc.accepterChangementMJ(pers.getId(), bob);
        assertTrue(acc);
        assertNotNull(pers.getMJ());
        assertEquals(pers.getMJ().getId(), bob.getId());

        // initial episode should exist and be validated by player but awaiting MJ
        assertFalse(pers.getBiographie().getEpisodes().isEmpty());
        Episode e = pers.getBiographie().getEpisodes().get(0);
        assertTrue(e.isValideParJoueur());
        assertFalse(e.isValideParMJ());
        assertEquals(StatutEpisode.EN_ATTENTE_VALIDATION, e.getStatut());

        // Bob validates the episode as MJ
        boolean okVal = ec.validerEpisode(e.getId(), bob);
        assertTrue(okVal);
        assertTrue(e.isValideParMJ());
        assertTrue(e.isCompletementValide());
        assertEquals(StatutEpisode.VALIDE, e.getStatut());

        // Bob creates a partie and adds the personnage
        Partie part = partCtrl.creerPartie("Quest", "start", "here", bob);
        assertNotNull(part);
        // partie has no universe yet; set personnage universe and add
        Univers uni = new Univers(1, "U1", "desc");
        pers.setUnivers(uni);
        boolean addOk = partCtrl.ajouterParticipant(part.getId(), pers, bob);
        assertTrue(addOk);

        // finish the partie
        boolean fin = partCtrl.terminerPartie(part.getId(), "It was fun", bob);
        assertTrue(fin);
        assertTrue(part.isTerminee());
        assertNotNull(part.getAventureGeneree());
    }
}
