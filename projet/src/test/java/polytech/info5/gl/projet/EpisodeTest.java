package polytech.info5.gl.projet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.Paragraphe;
import polytech.info5.gl.projet.model.StatutEpisode;
import polytech.info5.gl.projet.model.Utilisateur;

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
    public void testValidationPartielle() {
        Episode e = new Episode();
        assertFalse(e.isValideParJoueur());
        assertFalse(e.isValideParMJ());
        assertEquals(StatutEpisode.BROUILLON, e.getStatut());

        e.validerParJoueur(new Utilisateur());
        assertTrue(e.isValideParJoueur());
        assertFalse(e.isCompletementValide());
        assertEquals(StatutEpisode.BROUILLON, e.getStatut());
    }
}
