package polytech.info5.gl.projet;

import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import polytech.info5.gl.projet.model.Biographie;
import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.StatutEpisode;
import polytech.info5.gl.projet.model.Utilisateur;

public class BiographieTest {

    @Test
    public void testAjouterEtListerEpisodesVisibles() {
        Biographie b = new Biographie();

        Episode e1 = new Episode();
        e1.setDateRelative("2000");
        e1.setTitre("E1");

        Episode e2 = new Episode();
        e2.setDateRelative("1000");
        e2.setTitre("E2");

        b.ajouterEpisode(e1);
        b.ajouterEpisode(e2);

        assertEquals(2, b.getEpisodes().size());
        List<Episode> visibles = b.getEpisodesVisiblesPar(new Utilisateur());
        assertEquals(0, visibles.size());
    }

    @Test
    public void testValiderEpisodeChangeStatut() {
        Biographie b = new Biographie();
        Episode e = new Episode();
        b.ajouterEpisode(e);
        b.validerEpisode(e);
        assertEquals(StatutEpisode.VALIDE, e.getStatut());
    }

    @Test
    public void testSupprimerEpisodeEtDatesNull() {
        Biographie b = new Biographie();

        Episode e1 = new Episode();
        e1.setDateRelative(null);
        e1.setTitre("Nulle");

        Episode e2 = new Episode();
        e2.setDateRelative("0500");
        e2.setTitre("T1");

        b.ajouterEpisode(e1);
        b.ajouterEpisode(e2);

        b.supprimerEpisode(e1);
        assertEquals(1, b.getEpisodes().size());
    }
}
