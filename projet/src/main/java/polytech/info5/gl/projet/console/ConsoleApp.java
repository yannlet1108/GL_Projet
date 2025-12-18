package polytech.info5.gl.projet.console;

import polytech.info5.gl.projet.controller.AuthController;
import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.controller.PartieController;
import polytech.info5.gl.projet.controller.EpisodeController;
import polytech.info5.gl.projet.controller.BiographieController;
import polytech.info5.gl.projet.view.VuePartie;
import polytech.info5.gl.projet.model.Partie;
import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Utilisateur;
import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.Paragraphe;
import polytech.info5.gl.projet.view.VueBiographie;
import polytech.info5.gl.projet.view.VuePersonnage;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** Console interactive simple pour naviguer dans l'application. */
public class ConsoleApp {

    private final AuthController auth = new AuthController();
    private final PersonnageController pc = new PersonnageController();
    private final PartieController partieCtrl = new PartieController();
    private final EpisodeController episodeCtrl = new EpisodeController(pc);
    private final BiographieController biographieCtrl = new BiographieController(pc, episodeCtrl);
    private final VuePersonnage vueP = new VuePersonnage();
    private final VueBiographie vueB = new VueBiographie();
    private final VuePartie vuePartie = new VuePartie();

    private final Scanner scanner = new Scanner(System.in);
    private final java.io.File persistenceFile = new java.io.File(System.getProperty("app.state.path", "data/state.json"));

    public ConsoleApp() {
        // charger l'état si présent
        try {
            polytech.info5.gl.projet.persistence.PersistenceManager.ApplicationState st = polytech.info5.gl.projet.persistence.PersistenceManager.loadState(persistenceFile);
            if (st != null) {
                // restore users list first
                java.util.List<polytech.info5.gl.projet.model.Utilisateur> users = new java.util.ArrayList<>();
                try {
                    if (st.utilisateurs != null) {
                        for (polytech.info5.gl.projet.persistence.PersistenceManager.UtilisateurDTO ud : st.utilisateurs) {
                            users.add(polytech.info5.gl.projet.persistence.PersistenceManager.dtoToUtilisateur(ud));
                        }
                    }
                    if (!users.isEmpty()) auth.chargerUtilisateurs(users);
                } catch (Exception ex) { /* ignore */ }

                // restore personnages using users map for owner lookup
                pc.chargerPersonnages(polytech.info5.gl.projet.persistence.PersistenceManager.toPersonnages(st.personnages, users));

                // restore utilisateur connecté by id
                if (st.utilisateurConnecteId != null) {
                    for (polytech.info5.gl.projet.model.Utilisateur uu : users) {
                        if (uu != null && uu.getId() == st.utilisateurConnecteId) { auth.setUtilisateurConnecte(uu); break; }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Échec du chargement de l'état: " + ex.getMessage());
        }

        // enregistrement à la fermeture
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                polytech.info5.gl.projet.persistence.PersistenceManager.saveState(persistenceFile, auth, pc);
            } catch (Exception e) {
                System.err.println("Erreur sauvegarde état: " + e.getMessage());
            }
        }));
    }

    public void run() {
        System.out.println("Bienvenue dans l'application de gestion des parties (console)");
        while (true) {
            Utilisateur u = auth.getUtilisateurConnecte();
            if (u == null) {
                System.out.println("\nMenu (non connecté): 1) Register  2) Login  3) Exit");
                String cmd = scanner.nextLine().trim();
                if (cmd.equals("1")) doRegister();
                else if (cmd.equals("2")) doLogin();
                else if (cmd.equals("3")) {
                    System.out.println("Au revoir");
                    try { polytech.info5.gl.projet.persistence.PersistenceManager.saveState(persistenceFile, auth, pc); } catch (Exception ex) { System.err.println("Erreur sauvegarde état: " + ex.getMessage()); }
                    break;
                }
            } else {
                System.out.println("\nMenu principal (connecté: " + u.getNom() + "):\n1) Profil joueur\n2) Profil MJ\n3) Logout\n4) Exit");
                String cmd = scanner.nextLine().trim();
                switch (cmd) {
                    case "1": playerMenu(u); break;
                    case "2": mjMenu(u); break;
                    case "3": auth.logout(); System.out.println("Déconnecté"); break;
                    case "4":
                        System.out.println("Au revoir");
                        try { polytech.info5.gl.projet.persistence.PersistenceManager.saveState(persistenceFile, auth, pc); } catch (Exception ex) { System.err.println("Erreur sauvegarde état: " + ex.getMessage()); }
                        return;
                    default: System.out.println("Commande inconnue");
                }
            }
        }
    }

    // --- Player profile menu ---
    private void playerMenu(Utilisateur u) {
        while (true) {
            System.out.println("\nProfil Joueur (" + u.getNom() + "):\n1) Personnages\n2) Retour");
            String cmd = scanner.nextLine().trim();
            if (cmd.equals("1")) playerPersonnageMenu(u);
            else if (cmd.equals("2")) break;
            else System.out.println("Commande inconnue");
        }
    }

    private void playerPersonnageMenu(Utilisateur u) {
        while (true) {
            System.out.println("\nPersonnages - options:\n1) Lister mes personnages\n2) Créer personnage\n3) Consulter un personnage\n4) Transférer un personnage (stub)\n5) Changer de MJ (stub)\n6) Retour");
            String cmd = scanner.nextLine().trim();
            switch (cmd) {
                case "1": doListPersonnages(u); break;
                case "2": doCreatePersonnage(u); break;
                case "3": doVoirPersonnage(u); break;
                case "4": {
                    System.out.print("Id du personnage à transférer: ");
                    String sid = scanner.nextLine().trim();
                    try {
                        int idp = Integer.parseInt(sid);
                        System.out.print("Id du nouveau joueur: ");
                        String sn = scanner.nextLine().trim();
                        int idnew = Integer.parseInt(sn);
                        boolean ok = pc.cederPersonnage(idp, idnew, u);
                        System.out.println(ok ? "Transfert effectué" : "Échec du transfert");
                    } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                    break;
                }
                case "5": {
                    System.out.print("Id du personnage pour changement de MJ: ");
                    String sid2 = scanner.nextLine().trim();
                    try {
                        int idp = Integer.parseInt(sid2);
                        System.out.print("Id du MJ souhaité: ");
                        String sm = scanner.nextLine().trim();
                        int idmj = Integer.parseInt(sm);
                        boolean ok = pc.demanderChangementMJ(idp, idmj, u);
                        System.out.println(ok ? "Demande envoyée" : "Échec de la demande");
                    } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                    break;
                }
                case "6": return;
                default: System.out.println("Commande inconnue");
            }
        }
    }

    // --- MJ profile menu ---
    private void mjMenu(Utilisateur u) {
        while (true) {
            System.out.println("\nProfil MJ (" + u.getNom() + "):\n1) Personnages\n2) Parties\n3) Retour");
            String cmd = scanner.nextLine().trim();
            if (cmd.equals("1")) mjPersonnageMenu(u);
            else if (cmd.equals("2")) mjPartieMenu(u);
            else if (cmd.equals("3")) break;
            else System.out.println("Commande inconnue");
        }
    }

    private void mjPersonnageMenu(Utilisateur u) {
        while (true) {
            System.out.println("\nPersonnages (MJ) - options:\n1) Personnages à valider (stub)\n2) Lister les personnages\n3) Consulter un personnage\n4) Retour");
            String cmd = scanner.nextLine().trim();
            switch (cmd) {
                case "1": {
                    // lister personnages avec MJ en attente
                    List<Personnage> all = pc.listerTous();
                    List<Personnage> pending = new java.util.ArrayList<>();
                    for (Personnage p : all) if (p.getMjEnAttente() != null) pending.add(p);
                    if (pending.isEmpty()) { System.out.println("Aucune demande de changement de MJ en attente."); break; }
                    System.out.println("Demandes en attente:");
                    for (Personnage p : pending) System.out.println("- id=" + p.getId() + " | " + p.getNom() + " | MJ actuel=" + (p.getMJ()!=null? p.getMJ().getNom():"(aucun)") + " | nouveauMJId=" + p.getMjEnAttente().getId());
                    System.out.print("Id du personnage à traiter (ou 'b' pour revenir): ");
                    String sid = scanner.nextLine().trim();
                    if (sid.equalsIgnoreCase("b")) break;
                    try {
                        int idp = Integer.parseInt(sid);
                        System.out.print("Tapez 'a' pour accepter, 'r' pour refuser: ");
                        String act = scanner.nextLine().trim();
                        if (act.equalsIgnoreCase("a")) {
                            boolean ok = pc.accepterChangementMJ(idp, u);
                            System.out.println(ok ? "Changement de MJ accepté" : "Échec de l'acceptation");
                        } else if (act.equalsIgnoreCase("r")) {
                            boolean ok = pc.refuserChangementMJ(idp, u);
                            System.out.println(ok ? "Changement de MJ refusé" : "Échec du refus");
                        } else System.out.println("Action inconnue");
                    } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                    break;
                }
                case "2": {
                    doListPersonnagesEnTantQueMJ(u);
                    break;
                }
                case "3": doVoirPersonnage(u); break;
                case "4": return;
                default: System.out.println("Commande inconnue");
            }
        }
    }

    private void mjPartieMenu(Utilisateur u) {
        while (true) {
            System.out.println("\nParties (MJ) - options:\n1) Lister les parties en cours\n2) Créer une partie\n3) Consulter une partie (stub)\n4) Retour");
            String cmd = scanner.nextLine().trim();
            switch (cmd) {
                case "1": doListPartiesEnCours(); break;
                case "2": doCreatePartie(u); break;
                case "3": {
                    System.out.print("Id de la partie: ");
                    String sid = scanner.nextLine().trim();
                    try {
                        int idp = Integer.parseInt(sid);
                        Optional<Partie> op = partieCtrl.findById(idp);
                        if (op.isEmpty()) { System.out.println("Partie introuvable"); break; }
                        Partie p = op.get();
                        vuePartie.afficher(p);
                        // sous-menu de gestion
                        while (true) {
                            System.out.println("Gestion partie: 1) Lister participants 2) Ajouter participant 3) Retirer participant 4) Terminer 5) Supprimer 6) Retour");
                            String sc = scanner.nextLine().trim();
                            if (sc.equals("1")) {
                                if (p.getPersonnages().isEmpty()) System.out.println("Aucun participant");
                                else for (Personnage pp : p.getPersonnages()) System.out.println("- id=" + pp.getId() + " | " + pp.getNom());
                            } else if (sc.equals("2")) {
                                System.out.print("Id du personnage à ajouter: "); String sPar = scanner.nextLine().trim();
                                try {
                                    int idPers = Integer.parseInt(sPar);
                                    Optional<Personnage> opp = pc.findById(idPers);
                                    if (opp.isEmpty()) { System.out.println("Personnage introuvable"); }
                                    else {
                                        boolean ok = partieCtrl.ajouterParticipant(p.getId(), opp.get(), u);
                                        System.out.println(ok ? "Participant ajouté" : "Impossible d'ajouter le participant (vérifier univers/MJ)");
                                    }
                                } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                            } else if (sc.equals("3")) {
                                System.out.print("Id du personnage à retirer: "); String sPar = scanner.nextLine().trim();
                                try {
                                    int idPers = Integer.parseInt(sPar);
                                    Optional<Personnage> opp = pc.findById(idPers);
                                    if (opp.isEmpty()) { System.out.println("Personnage introuvable"); }
                                    else {
                                        boolean ok = partieCtrl.retirerParticipant(p.getId(), opp.get(), u);
                                        System.out.println(ok ? "Participant retiré" : "Échec du retrait");
                                    }
                                } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                            } else if (sc.equals("4")) {
                                System.out.print("Résumé de la partie (court texte): "); String resume = scanner.nextLine().trim();
                                boolean ok = partieCtrl.terminerPartie(p.getId(), resume, u);
                                System.out.println(ok ? "Partie terminée" : "Échec lors de la clôture");
                                if (ok) break;
                            } else if (sc.equals("5")) {
                                boolean ok = partieCtrl.supprimerPartie(p.getId(), u);
                                System.out.println(ok ? "Proposition supprimée" : "Échec de suppression (peut-être terminée ou droits insuffisants)");
                                if (ok) break;
                            } else if (sc.equals("6")) break;
                            else System.out.println("Commande inconnue");
                        }
                    } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                    break;
                }
                case "4": return;
                default: System.out.println("Commande inconnue");
            }
        }
    }

    private void doRegister() {
        System.out.print("Nom: "); String nom = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Mot de passe: "); String mdp = scanner.nextLine().trim();
        Utilisateur u = auth.register(nom, email, mdp);
        System.out.println("Utilisateur créé et connecté: " + u.getNom());
    }

    private void doLogin() {
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Mot de passe: "); String mdp = scanner.nextLine().trim();
        Utilisateur u = auth.login(email, mdp);
        if (u != null) System.out.println("Connecté: " + u.getNom());
        else System.out.println("Échec de connexion");
    }

    private void doCreatePersonnage(Utilisateur u) {
        System.out.print("Nom du personnage: "); String nom = scanner.nextLine().trim();
        System.out.print("Date de naissance (texte): "); String date = scanner.nextLine().trim();
        System.out.print("Profession: "); String prof = scanner.nextLine().trim();
        System.out.print("Biographie initiale (laisser vide si none): "); String bio = scanner.nextLine().trim();
        Personnage p = pc.creerPersonnage(nom, date, prof, bio, u);
        System.out.println("Personnage créé (id=" + p.getId() + ")");
    }

    private void doListPersonnages(Utilisateur u) {
        List<Personnage> list = pc.listerParUtilisateur(u);
        if (list.isEmpty()) System.out.println("Aucun personnage trouvé pour " + u.getNom());
        else {
            System.out.println("Mes personnages:");
            for (Personnage p : list) System.out.println("- id=" + p.getId() + " | " + p.getNom() + " (" + p.getProfession() + ")");
        }
    }

    private void doListPersonnagesEnTantQueMJ(Utilisateur u) {
        List<Personnage> all = pc.listerTous();
        List<Personnage> res = new java.util.ArrayList<>();
        for (Personnage p : all) if (p.getMJ() != null && u != null && p.getMJ().getId() == u.getId()) res.add(p);
        if (res.isEmpty()) System.out.println("Vous n'êtes MJ d'aucun personnage.");
        else {
            System.out.println("Personnages dont vous êtes MJ:");
            for (Personnage p : res) System.out.println("- id=" + p.getId() + " | " + p.getNom() + " (" + p.getProfession() + ")");
        }
    }

    private void doCreatePartie(Utilisateur u) {
        System.out.print("Titre de la partie: "); String titre = scanner.nextLine().trim();
        System.out.print("Situation initiale (court texte): "); String sit = scanner.nextLine().trim();
        System.out.print("Lieu: "); String lieu = scanner.nextLine().trim();
        Partie p = partieCtrl.creerPartie(titre, sit, lieu, u);
        System.out.println("Partie créée (id=" + p.getId() + ")");
    }

    private void doListPartiesEnCours() {
        List<Partie> list = partieCtrl.listerPartiesEnCours();
        if (list.isEmpty()) System.out.println("Aucune partie en cours.");
        else {
            System.out.println("Parties en cours:");
            for (Partie p : list) System.out.println("- id=" + p.getId() + " | " + p.getTitre() + " | MJ=" + (p.getMJ()!=null? p.getMJ().getNom():"(aucun)") + " | lieu=" + p.getLieu());
        }
    }

    private void doVoirPersonnage(Utilisateur u) {
        System.out.print("Id du personnage: ");
        String s = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(s);
            Optional<Personnage> op = pc.findById(id);
            if (op.isEmpty()) { System.out.println("Personnage introuvable"); return; }
            Personnage p = op.get();
            // Afficher via vues
            vueP.afficher(p);
            vueB.afficher(p.getBiographie());
            
            // Navigation interactive des épisodes
            java.util.List<Episode> episodes = p.getBiographie().getEpisodesVisiblesPar(u);
            if (episodes == null || episodes.isEmpty()) {
                System.out.println("Aucun épisode disponible pour ce personnage.");
                return;
            }

            System.out.println("Parcourir les épisodes : tapez 'l' pour lister, un numéro pour afficher, 'q' pour revenir");
            while (true) {
                System.out.print("Episode> ");
                String cmd = scanner.nextLine().trim();
                if (cmd.equalsIgnoreCase("q")) break;
                if (cmd.equalsIgnoreCase("l")) {
                    for (int i = 0; i < episodes.size(); i++) {
                        Episode e = episodes.get(i);
                        System.out.println((i+1) + ") " + (e.getTitre() != null ? e.getTitre() : "(sans titre)") + " - date: " + e.getDateRelative());
                    }
                    continue;
                }
                try {
                    int idx = Integer.parseInt(cmd);
                    if (idx < 1 || idx > episodes.size()) { System.out.println("Index d'épisode invalide"); continue; }
                    Episode e = episodes.get(idx-1);
                    System.out.println("--- Episode: " + (e.getTitre() != null ? e.getTitre() : "(sans titre)") + " ---");
                    System.out.println("Date relative: " + e.getDateRelative());
                    System.out.println("Statut: " + e.getStatut());
                    java.util.List<Paragraphe> pars = e.getParagraphes();
                    if (pars == null || pars.isEmpty()) System.out.println("[Aucun paragraphe]");
                    else {
                        for (int j = 0; j < pars.size(); j++) {
                            Paragraphe par = pars.get(j);
                            System.out.println((j+1) + ") [" + (par.isPublique() ? "public" : "secret") + "] " + par.getTexte());
                        }
                    }
                    System.out.println("--- fin épisode ---");
                } catch (NumberFormatException ex) {
                    System.out.println("Commande inconnue — tapez 'l', un numéro, ou 'q'");
                }
            }
        } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
    }

    public static void main(String[] args) {
        new ConsoleApp().run();
    }
}
