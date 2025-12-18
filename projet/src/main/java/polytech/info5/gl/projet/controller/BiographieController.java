package polytech.info5.gl.projet.controller;

import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.Paragraphe;
import polytech.info5.gl.projet.model.Utilisateur;
import polytech.info5.gl.projet.view.VueBiographie;
import polytech.info5.gl.projet.view.VuePersonnage;

import java.util.List;
import java.util.Optional;

/** Contrôleur pour la visualisation des biographies. */
public class BiographieController {

    private final PersonnageController pc;
    private final EpisodeController ec;

    public BiographieController(PersonnageController pc, EpisodeController ec) {
        this.pc = pc; this.ec = ec;
    }

    /** Affiche la biographie d'un personnage en utilisant les vues. */
    public void afficherBiographie(int idPers, Utilisateur utilisateurConnecte) {
        Optional<Personnage> op = pc.findById(idPers);
        if (op.isEmpty()) { System.out.println("Personnage introuvable"); return; }
        Personnage p = op.get();
        new VuePersonnage().afficher(p);
        new VueBiographie().afficher(p.getBiographie());
    }

    /** Affiche un épisode via la console. */
    public void afficherEpisode(Episode e, Utilisateur u) {
        if (e == null) { System.out.println("Episode introuvable"); return; }
        System.out.println("--- Episode: " + (e.getTitre()!=null?e.getTitre():"(sans titre)") + " ---");
        System.out.println("Date relative: " + e.getDateRelative());
        System.out.println("Statut: " + e.getStatut());
        List<Paragraphe> pars = e.getParagraphes();
        if (pars == null || pars.isEmpty()) System.out.println("[Aucun paragraphe]");
        else {
            for (int j = 0; j < pars.size(); j++) {
                Paragraphe par = pars.get(j);
                System.out.println((j+1) + ") [" + (par.isPublique()?"public":"secret") + "] " + par.getTexte());
            }
        }
        System.out.println("--- fin épisode ---");
    }
}
