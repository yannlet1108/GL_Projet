package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.model.*;
import java.util.List;

public class BiographieTest {

    @Test
    public void testAjouterEtListerEpisodesVisibles() {
        Biographie b = new Biographie();
        Episode e1 = new Episode(); e1.setDateRelative("2000"); e1.setTitre("E1");
        Episode e2 = new Episode(); e2.setDateRelative("1000"); e2.setTitre("E2");
        b.ajouterEpisode(e1);
        b.ajouterEpisode(e2);

        List<Episode> visibles = b.getEpisodesVisiblesPar(new Utilisateur());
        assertEquals(2, visibles.size());
        // tri lexicographique: 1000 avant 2000
        assertEquals("E2", visibles.get(0).getTitre());
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
        Episode e1 = new Episode(); e1.setDateRelative(null); e1.setTitre("Nulle");
        Episode e2 = new Episode(); e2.setDateRelative("0500"); e2.setTitre("T1");
        b.ajouterEpisode(e1);
        b.ajouterEpisode(e2);

        // la méthode de tri place les nulls en dernier
        java.util.List<Episode> visibles = b.getEpisodesVisiblesPar(new Utilisateur());
        assertEquals("T1", visibles.get(0).getTitre());
        assertEquals("Nulle", visibles.get(1).getTitre());

        // suppression
        b.supprimerEpisode(e1);
        assertEquals(1, b.getEpisodes().size());
    }
}
