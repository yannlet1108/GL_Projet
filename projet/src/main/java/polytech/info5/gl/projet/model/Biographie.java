package polytech.info5.gl.projet.model;

import java.util.ArrayList;
import java.util.List;

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
    public void ajouterEpisode(Episode ep) { episodes.add(ep); }
    public void supprimerEpisode(Episode ep) { episodes.remove(ep); }
    public void validerEpisode(Episode ep) { ep.setStatut(StatutEpisode.VALIDE); }

    public List<Episode> getEpisodesVisiblesPar(Utilisateur utilisateur) {
        List<Episode> copie = new ArrayList<>();
        if (episodes == null) return copie;

        if (utilisateur != null && personnage != null) {
            Utilisateur joueur = personnage.getJoueur();
            Utilisateur mj = personnage.getMJ();
            if ((joueur != null && utilisateur.getId() == joueur.getId()) || (mj != null && utilisateur.getId() == mj.getId())) {
                copie.addAll(episodes);
                return copie;
            }
        }

        for (Episode e : episodes) {
            if (e == null) continue;
            if (e.getStatut() != null && e.getStatut() != StatutEpisode.VALIDE) continue;
            if (e.getParagraphes() == null) continue;
            for (Paragraphe pr : e.getParagraphes()) {
                if (pr != null && pr.isPublique()) { copie.add(e); break; }
            }
        }
        return copie;
    }
}
