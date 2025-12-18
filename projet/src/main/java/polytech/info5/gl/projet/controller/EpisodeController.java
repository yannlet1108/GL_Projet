package polytech.info5.gl.projet.controller;

import polytech.info5.gl.projet.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Contrôleur gérant la création et modification des épisodes. */
public class EpisodeController {

    private final PersonnageController pc;
    private int nextEpisodeId = 1;
    private int nextParagrapheId = 1;

    public EpisodeController(PersonnageController pc) {
        this.pc = pc;
    }

    public Episode creerEpisode(int idPers, String titre, String dateRelative, Utilisateur utilisateurConnecte) {
        Optional<Personnage> op = pc.findById(idPers);
        if (op.isEmpty()) return null;
        Personnage p = op.get();
        Episode e = new Episode();
        e.setId(nextEpisodeId++);
        e.setTitre(titre);
        e.setDateRelative(dateRelative);
        p.getBiographie().ajouterEpisode(e);
        return e;
    }

    public boolean modifierEpisode(int idEp, String titre, String dateRelative, Utilisateur utilisateurConnecte) {
        Episode e = findEpisodeById(idEp);
        if (e == null) return false;
        if (titre != null) e.setTitre(titre);
        if (dateRelative != null) e.setDateRelative(dateRelative);
        return true;
    }

    public boolean ajouterParagraphe(int idEp, String texte, boolean estSecret, int ordre, Utilisateur utilisateurConnecte) {
        Episode e = findEpisodeById(idEp);
        if (e == null) return false;
        Paragraphe p = new Paragraphe(nextParagrapheId++, ordre, texte, !estSecret);
        e.ajouterParagraphe(p);
        return true;
    }

    public boolean supprimerParagraphe(int idPar, Utilisateur utilisateurConnecte) {
        for (Personnage pers : pc.listerTous()) {
            for (Episode e : pers.getBiographie().getEpisodes()) {
                for (Paragraphe par : new ArrayList<>(e.getParagraphes())) {
                    if (par.getId() == idPar) { e.retirerParagraphe(par); return true; }
                }
            }
        }
        return false;
    }

    public boolean validerEpisode(int idEp, Utilisateur utilisateurConnecte) {
        Episode e = findEpisodeById(idEp);
        if (e == null) return false;
        // simple behavior: mark as validated by the player
        e.validerParJoueur(utilisateurConnecte);
        return true;
    }

    public boolean supprimerEpisode(int idEp, Utilisateur utilisateurConnecte) {
        for (Personnage pers : pc.listerTous()) {
            for (Episode e : new ArrayList<>(pers.getBiographie().getEpisodes())) {
                if (e.getId() == idEp) { pers.getBiographie().getEpisodes().remove(e); return true; }
            }
        }
        return false;
    }

    public boolean revelerParagraphe(int idPar, Utilisateur utilisateurConnecte) {
        for (Personnage pers : pc.listerTous()) {
            for (Episode e : pers.getBiographie().getEpisodes()) {
                for (Paragraphe par : e.getParagraphes()) {
                    if (par.getId() == idPar) { par.setPublique(true); return true; }
                }
            }
        }
        return false;
    }

    public Episode findEpisodeById(int idEp) {
        for (Personnage pers : pc.listerTous()) {
            for (Episode e : pers.getBiographie().getEpisodes()) {
                if (e.getId() == idEp) return e;
            }
        }
        return null;
    }

    public Paragraphe findParagrapheById(int idPar) {
        for (Personnage pers : pc.listerTous()) {
            for (Episode e : pers.getBiographie().getEpisodes()) {
                for (Paragraphe par : e.getParagraphes()) if (par.getId() == idPar) return par;
            }
        }
        return null;
    }

    public List<Episode> getEpisodesVisiblesPourPersonnage(int idPers, Utilisateur u) {
        Optional<Personnage> op = pc.findById(idPers);
        if (op.isEmpty()) return new ArrayList<>();
        Personnage p = op.get();
        List<Episode> res = new ArrayList<>();
        for (Episode e : p.getBiographie().getEpisodes()) res.add(e);
        return res;
    }
}
