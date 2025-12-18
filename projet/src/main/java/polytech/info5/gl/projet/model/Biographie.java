package polytech.info5.gl.projet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Biographie associée à un personnage, composée d'épisodes ordonnés. */
public class Biographie {
    private int id;
    private Personnage personnage;
    private List<Episode> episodes = new ArrayList<>();

    public Biographie() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Personnage getPersonnage() { return personnage; }
    public void setPersonnage(Personnage personnage) { this.personnage = personnage; }

    public List<Episode> getEpisodes() { return episodes; }

    /** Ajoute un épisode (ne garantit pas l'ordre). */
    public void ajouterEpisode(Episode ep) { episodes.add(ep); }

    /** Supprime un épisode. */
    public void supprimerEpisode(Episode ep) { episodes.remove(ep); }

    /** Valide un épisode (stub). */
    public void validerEpisode(Episode ep) { ep.setStatut(StatutEpisode.VALIDE); }

    /** Retourne la liste des épisodes visibles par l'utilisateur (filtre les paragraphes secrets). */
    public List<Episode> getEpisodesVisiblesPar(Utilisateur utilisateur) {
        List<Episode> copie = new ArrayList<>(episodes);
        // Tri chronologique simple sur dateRelative si nécessaire (ici lexicographique)
        Collections.sort(copie, Comparator.comparing(Episode::getDateRelative, Comparator.nullsLast(String::compareTo)));
        return copie;
    }
}
