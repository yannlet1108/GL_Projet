package polytech.info5.gl.projet.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import polytech.info5.gl.projet.model.Partie;
import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Utilisateur;

public class PartieController {

    private final List<Partie> parties = new ArrayList<>();
    private int nextId = 1;

    public Partie creerPartie(String titre, String situationInitiale, String lieu, Utilisateur mj) {
        Partie p = new Partie();
        p.setId(nextId++);
        p.setTitre(titre);
        p.setSituationInitiale(situationInitiale);
        p.setLieu(lieu);
        p.setMJ(mj);
        parties.add(p);
        return p;
    }

    public List<Partie> listerPartiesEnCours() {
        List<Partie> res = new ArrayList<>();
        for (Partie p : parties) if (!p.isTerminee()) res.add(p);
        return res;
    }

    public List<Partie> listerParMJ(Utilisateur mj) {
        List<Partie> res = new ArrayList<>();
        if (mj == null) return res;
        for (Partie p : parties) if (p.getMJ() != null && p.getMJ().getId() == mj.getId()) res.add(p);
        return res;
    }

    public Optional<Partie> findById(int id) {
        return parties.stream().filter(p -> p.getId() == id).findFirst();
    }

    public List<Partie> listerToutes() { return new ArrayList<>(parties); }

    public void chargerParties(List<Partie> liste) {
        this.parties.clear();
        if (liste != null) this.parties.addAll(liste);
        int max = 0; for (Partie p : parties) if (p.getId() > max) max = p.getId();
        this.nextId = max + 1;
    }

    public boolean ajouterParticipant(int idPartie, Personnage personnage, Utilisateur demandeur) {
        Optional<Partie> op = findById(idPartie);
        if (op.isEmpty() || personnage == null || demandeur == null) return false;
        Partie p = op.get();

        // only MJ can add participants
        if (p.getMJ() == null || p.getMJ().getId() != demandeur.getId()) return false;
        if (personnage.getMJ() == null || personnage.getMJ().getId() != demandeur.getId()) return false;

        // if partie has no universe yet, adopt the personnage's universe
        if (p.getUnivers() == null && personnage.getUnivers() != null) p.setUnivers(personnage.getUnivers());
        if (p.isPersonnageAjoutable(personnage)) { p.ajouterPersonnage(personnage); return true; }
        return false;
    }

    public boolean retirerParticipant(int idPartie, Personnage personnage, Utilisateur demandeur) {
        Optional<Partie> op = findById(idPartie);
        if (op.isEmpty() || personnage == null || demandeur == null) return false;
        Partie p = op.get();
        if (p.getMJ() == null || p.getMJ().getId() != demandeur.getId()) return false;
        p.retirerPersonnage(personnage);
        return true;
    }

    public boolean terminerPartie(int idPartie, String resume, Utilisateur demandeur) {
        Optional<Partie> op = findById(idPartie);
        if (op.isEmpty()) return false;
        Partie p = op.get();
        if (p.getMJ() == null || p.getMJ().getId() != demandeur.getId()) return false;
        p.terminerPartie(resume);
        return true;
    }

    public boolean supprimerPartie(int idPartie, Utilisateur demandeur) {
        Optional<Partie> op = findById(idPartie);
        if (op.isEmpty()) return false;
        Partie p = op.get();
        // only MJ can delete and if not terminated
        if (p.isTerminee()) return false;
        if (p.getMJ() == null || p.getMJ().getId() != demandeur.getId()) return false;
        parties.remove(p);
        return true;
    }
}
