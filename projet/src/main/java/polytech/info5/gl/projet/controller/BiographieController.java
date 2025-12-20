package polytech.info5.gl.projet.controller;

import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Biographie;
import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.Utilisateur;

import java.util.Optional;

/** Contrôleur pour la visualisation des biographies. */
public class BiographieController {

    private final PersonnageController pc;
    private final EpisodeController ec;

    public BiographieController(PersonnageController pc, EpisodeController ec) {
        this.pc = pc; this.ec = ec;
    }

    /** Retourne le personnage correspondant (délégué au PersonnageController). */
    public Optional<Personnage> getPersonnage(int idPers) {
        return pc.findById(idPers);
    }

    /** Retourne la biographie du personnage identifié, ou vide si introuvable. */
    public Optional<Biographie> getBiographiePourPersonnage(int idPers) {
        Optional<Personnage> op = pc.findById(idPers);
        if (op.isEmpty()) return Optional.empty();
        return Optional.ofNullable(op.get().getBiographie());
    }

    /** Retourne la liste des épisodes visibles pour le personnage et l'utilisateur donné. */
    public java.util.List<Episode> getEpisodesVisiblesPour(int idPers, Utilisateur utilisateur) {
        Optional<Personnage> op = pc.findById(idPers);
        if (op.isEmpty()) return java.util.Collections.emptyList();
        Biographie b = op.get().getBiographie();
        if (b == null) return java.util.Collections.emptyList();
        return b.getEpisodesVisiblesPar(utilisateur);
    }
}
