package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.model.*;

public class EpisodeTest {

    @Test
    public void testParagrapheEtValidation() {
        Episode e = new Episode();
        Paragraphe p1 = new Paragraphe(1, 1, "Public", true);
        Paragraphe p2 = new Paragraphe(2, 2, "Secret", false);

        e.ajouterParagraphe(p1);
        e.ajouterParagraphe(p2);

        assertEquals(2, e.getParagraphes().size());

        e.validerParJoueur(new Utilisateur());
        assertTrue(e.isValideParJoueur());

        e.validerParMJ(new Utilisateur());
        assertTrue(e.isValideParMJ());
        assertTrue(e.isCompletementValide());
        assertEquals(StatutEpisode.VALIDE, e.getStatut());

        e.retirerParagraphe(p1);
        assertEquals(1, e.getParagraphes().size());
    }

    @Test
    public void testValidationPartielleEtModifiabilite() {
        Episode e = new Episode();
        // au départ non validé
        assertFalse(e.isValideParJoueur());
        assertFalse(e.isValideParMJ());
        assertEquals(StatutEpisode.BROUILLON, e.getStatut());

        // Valider seulement par le joueur
        e.validerParJoueur(new Utilisateur());
        assertTrue(e.isValideParJoueur());
        assertFalse(e.isCompletementValide());
        // le statut ne doit pas être VALIDE tant que MJ n'a pas validé
        assertEquals(StatutEpisode.BROUILLON, e.getStatut());

        // L'épisode est encore modifiable
        assertTrue(e.isModifiablePar(new Utilisateur()));
    }
}
