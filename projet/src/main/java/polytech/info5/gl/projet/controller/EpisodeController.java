package polytech.info5.gl.projet.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.Paragraphe;
import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.StatutEpisode;
import polytech.info5.gl.projet.model.Utilisateur;

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
        e.setStatut(StatutEpisode.EN_ATTENTE_VALIDATION);
        p.getBiographie().ajouterEpisode(e);
        return e;
    }

    public boolean modifierEpisode(int idEp, String titre, String dateRelative, Utilisateur utilisateurConnecte) {
        Episode e = findEpisodeById(idEp);
        if (e == null || utilisateurConnecte == null) return false;
        Personnage owner = null;
        for (Personnage pers : pc.listerTous()) {
            for (Episode ep : pers.getBiographie().getEpisodes()) {
                if (ep.getId() == idEp) { owner = pers; break; }
            }
            if (owner != null) break;
        }
        if (owner == null) return false;
        boolean isOwner = owner.getJoueur()!=null && owner.getJoueur().getId()==utilisateurConnecte.getId();
        boolean isMJ = owner.getMJ()!=null && owner.getMJ().getId()==utilisateurConnecte.getId();
        if (!isOwner && !isMJ) return false;
        if (e.getStatut() == StatutEpisode.VALIDE) return false;
        if (titre != null) e.setTitre(titre);
        if (dateRelative != null) e.setDateRelative(dateRelative);
        return true;
    }

    public boolean ajouterParagraphe(int idEp, String texte, boolean estSecret, int ordre, Utilisateur utilisateurConnecte) {
        Episode e = findEpisodeById(idEp);
        if (e == null) return false;
        // only allow owner or MJ of the personnage to add a paragraph and only if episode not VALIDE
        Personnage owner = null;
        for (Personnage pers : pc.listerTous()) {
            for (Episode ep : pers.getBiographie().getEpisodes()) if (ep.getId() == idEp) { owner = pers; break; }
            if (owner != null) break;
        }
        if (owner == null || utilisateurConnecte == null) return false;
        if (e.getStatut() == StatutEpisode.VALIDE) return false;
        boolean isOwner = owner.getJoueur() != null && owner.getJoueur().getId() == utilisateurConnecte.getId();
        boolean isMJ = owner.getMJ() != null && owner.getMJ().getId() == utilisateurConnecte.getId();
        if (!isOwner && !isMJ) return false;
        Paragraphe p = new Paragraphe(nextParagrapheId++, ordre, texte, !estSecret);
        e.ajouterParagraphe(p);
        return true;
    }

    public boolean supprimerParagraphe(int idPar, Utilisateur utilisateurConnecte) {
        for (Personnage pers : pc.listerTous()) {
            for (Episode e : pers.getBiographie().getEpisodes()) {
                for (Paragraphe par : new ArrayList<>(e.getParagraphes())) {
                    if (par.getId() == idPar) {
                        // only owner or MJ can delete a paragraph and only if episode not VALIDE
                        if (e.getStatut() == StatutEpisode.VALIDE) return false;
                        if (pers.getJoueur() != null && utilisateurConnecte != null && (pers.getJoueur().getId() == utilisateurConnecte.getId() || (pers.getMJ()!=null && pers.getMJ().getId()==utilisateurConnecte.getId()))) {
                            e.retirerParagraphe(par); return true;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    public boolean validerEpisode(int idEp, Utilisateur utilisateurConnecte) {
        Episode e = findEpisodeById(idEp);
        if (e == null) return false;
        Personnage owner = null;
        for (Personnage pers : pc.listerTous()) {
            for (Episode ep : pers.getBiographie().getEpisodes()) {
                if (ep.getId() == idEp) { owner = pers; break; }
            }
            if (owner != null) break;
        }
        if (owner == null || utilisateurConnecte == null) return false;
        if (owner.getMJ() != null && owner.getMJ().getId() == utilisateurConnecte.getId()) {
            e.validerParMJ(utilisateurConnecte);
            return true;
        }
        if (owner.getJoueur() != null && owner.getJoueur().getId() == utilisateurConnecte.getId()) {
            e.validerParJoueur(utilisateurConnecte);
            return true;
        }
        return false;
    }

    public boolean supprimerEpisode(int idEp, Utilisateur utilisateurConnecte) {
        for (Personnage pers : pc.listerTous()) {
            for (Episode e : new ArrayList<>(pers.getBiographie().getEpisodes())) {
                if (e.getId() == idEp) {
                    // only owner or MJ can delete and only if not VALIDE
                    if (utilisateurConnecte == null) return false;
                    boolean isOwner = pers.getJoueur()!=null && pers.getJoueur().getId()==utilisateurConnecte.getId();
                    boolean isMJ = pers.getMJ()!=null && pers.getMJ().getId()==utilisateurConnecte.getId();
                    if (!isOwner && !isMJ) return false;
                    if (e.getStatut() == StatutEpisode.VALIDE) return false;
                    pers.getBiographie().getEpisodes().remove(e); return true;
                }
            }
        }
        return false;
    }

    public boolean revelerParagraphe(int idPar, Utilisateur utilisateurConnecte) {
        for (Personnage pers : pc.listerTous()) {
            for (Episode e : pers.getBiographie().getEpisodes()) {
                for (Paragraphe par : e.getParagraphes()) {
                    if (par.getId() == idPar) {
                        // only owner or MJ can reveal
                        if (utilisateurConnecte == null) return false;
                        boolean isOwner = pers.getJoueur()!=null && pers.getJoueur().getId()==utilisateurConnecte.getId();
                        boolean isMJ = pers.getMJ()!=null && pers.getMJ().getId()==utilisateurConnecte.getId();
                        if (!isOwner && !isMJ) return false;
                        par.setPublique(true);
                        return true;
                    }
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
