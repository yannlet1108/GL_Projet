package polytech.info5.gl.projet.view;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import polytech.info5.gl.projet.controller.AuthController;
import polytech.info5.gl.projet.controller.BiographieController;
import polytech.info5.gl.projet.controller.EpisodeController;
import polytech.info5.gl.projet.controller.PartieController;
import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.Paragraphe;
import polytech.info5.gl.projet.model.Partie;
import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Utilisateur;

/** Console interactive simple pour naviguer dans l'application. (déplacée dans view) */
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
                System.out.println("\nMenu (non connecté): 1) Créer un compte  2) Se connecter  3) Quitter");
                String cmd = scanner.nextLine().trim();
                if (cmd.equals("1")) doRegister();
                else if (cmd.equals("2")) doLogin();
                else if (cmd.equals("3")) {
                    System.out.println("Au revoir");
                    try { polytech.info5.gl.projet.persistence.PersistenceManager.saveState(persistenceFile, auth, pc); } catch (Exception ex) { System.err.println("Erreur sauvegarde état: " + ex.getMessage()); }
                    break;
                }
            } else {
                System.out.println("\nMenu principal (connecté: " + u.getNom() + "):\n1) Profil joueur\n2) Profil MJ\n3) Se déconnecter\n4) Quitter");
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
            System.out.println("\nPersonnages - options:\n1) Lister mes personnages\n2) Créer personnage\n3) Consulter un personnage\n4) Retour");
            String cmd = scanner.nextLine().trim();
            switch (cmd) {
                case "1": doListPersonnages(u); break;
                case "2": doCreatePersonnage(u); break;
                case "3": doVoirPersonnage(u); break;
                case "4": return;
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
            System.out.println("\nPersonnages (MJ) - options:\n1) Personnages à valider\n2) Épisodes à valider\n3) Lister les personnages\n4) Consulter un personnage\n5) Retour");
            String cmd = scanner.nextLine().trim();
            switch (cmd) {
                case "1": {
                    // lister personnages avec MJ en attente
                    List<Personnage> all = pc.listerTous();
                    List<Personnage> pending = new java.util.ArrayList<>();
                    for (Personnage p : all) if (p.getMjEnAttente() != null) pending.add(p);
                    if (!pending.isEmpty()) {
                        System.out.println("Demandes de MJ en attente:");
                        for (Personnage p : pending) System.out.println("- id=" + p.getId() + " | " + p.getNom() + " | MJ actuel=" + (p.getMJ()!=null? p.getMJ().getNom():"(aucun)") + " | nouveauMJId=" + p.getMjEnAttente().getId());
                        System.out.print("Id du personnage à traiter (ou 'b' pour annuler): ");
                        String sid = scanner.nextLine().trim();
                        if (!sid.equalsIgnoreCase("b")) {
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
                        }
                    } else {
                        System.out.println("Aucune demande de changement de MJ en attente.");
                    }
                    break;
                }
                case "2": {
                    // lister épisodes en attente de validation pour lesquels l'utilisateur est MJ
                    List<Personnage> all = pc.listerTous();
                    List<Episode> epsPending = new java.util.ArrayList<>();
                    for (Personnage p : all) {
                        if (p.getMJ() != null && u != null && p.getMJ().getId() == u.getId()) {
                            for (Episode e : p.getBiographie().getEpisodes()) {
                                if (e.getStatut() == polytech.info5.gl.projet.model.StatutEpisode.EN_ATTENTE_VALIDATION) epsPending.add(e);
                            }
                        }
                    }
                    if (epsPending.isEmpty()) { System.out.println("Aucun épisode en attente de validation."); break; }
                    System.out.println("Épisodes en attente de validation:");
                    for (Episode e : epsPending) System.out.println("- id=" + e.getId() + " | titre=" + (e.getTitre()!=null?e.getTitre():"(sans titre)") + " | statut=" + e.getStatut());
                    System.out.print("Id de l'épisode à valider (ou 'b' pour revenir): ");
                    String seid = scanner.nextLine().trim();
                    if (seid.equalsIgnoreCase("b")) break;
                    try {
                        int ide = Integer.parseInt(seid);
                        boolean ok = episodeCtrl.validerEpisode(ide, u);
                        System.out.println(ok ? "Épisode validé" : "Échec validation");
                    } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                    break;
                }
                case "3": {
                    doListPersonnagesEnTantQueMJ(u);
                    break;
                }
                case "4": doVoirPersonnage(u); break;
                case "5": return;
                default: System.out.println("Commande inconnue");
            }
        }
    }

    private void mjPartieMenu(Utilisateur u) {
        while (true) {
            System.out.println("\nParties (MJ) - options:\n1) Lister les parties en cours\n2) Créer une partie\n3) Consulter une partie\n4) Retour");
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
                            System.out.println("Gestion partie:\n1) Lister participants\n2) Ajouter participant\n3) Retirer participant\n4) Terminer\n5) Supprimer\n6) Retour");
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
        // Allow interactive selection of a proposed MJ: user can type 'l' to list users
        System.out.print("Id du MJ proposé (laisser vide si aucun, 'l' pour lister utilisateurs): ");
        String smj = scanner.nextLine().trim();
        // if user asked to list, show users and re-prompt until blank or numeric id
        while (smj != null && smj.equalsIgnoreCase("l")) {
            java.util.List<Utilisateur> users = auth.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Aucun utilisateur enregistré.");
            } else {
                System.out.println("Utilisateurs disponibles :");
                for (Utilisateur uu : users) {
                    System.out.println("- id=" + uu.getId() + " | " + uu.getNom() + " | " + uu.getEmail());
                }
            }
            System.out.print("Id du MJ proposé (laisser vide si aucun, 'l' pour relister): ");
            smj = scanner.nextLine().trim();
        }

        Personnage p;
        if (smj == null || smj.isBlank()) {
            p = pc.creerPersonnage(nom, date, prof, bio, u);
        } else {
            try {
                int idmj = Integer.parseInt(smj);
                p = pc.creerPersonnageAvecMJ(nom, date, prof, bio, idmj, u);
                System.out.println("Proposition de MJ envoyée (MJ id=" + idmj + ")");
            } catch (NumberFormatException ex) {
                System.out.println("Identifiant MJ invalide, création sans MJ proposé.");
                p = pc.creerPersonnage(nom, date, prof, bio, u);
            }
        }
        System.out.println("Personnage créé (id=" + p.getId() + ")");
    }

    private void doListPersonnages(Utilisateur u) {
        List<Personnage> list = pc.listerParUtilisateur(u);
        if (list.isEmpty()) System.out.println("Aucun personnage trouvé pour " + u.getNom());
        else {
            System.out.println("Mes personnages:");
            for (Personnage p : list) {
                String statut;
                if (p.getMjEnAttente() != null) statut = "Proposé";
                else if (p.getMJ() == null) statut = "SansMJ";
                else statut = "MJ=" + p.getMJ().getNom();
                System.out.println("- id=" + p.getId() + " | " + p.getNom() + " (" + p.getProfession() + ") [" + statut + "]");
            }
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
            // Header: basic infos
            System.out.println("--- Personnage : " + p.getNom() + " | " + (p.getProfession()!=null?p.getProfession():"(profession inconnue)") + " ---");

            // Menu interactif pour consulter et gérer épisodes / MJ / transfert
            while (true) {
                boolean isMJ = (p.getMJ() != null && u != null && p.getMJ().getId() == u.getId());
                if (isMJ) {
                    System.out.println("\nGestion personnage (" + p.getNom() + ") :\n1) Consulter les épisodes validés\n2) Modifier un épisode brouillon\n3) Créer un nouvel épisode\n4) Lister les épisodes à valider\n5) Consulter un épisode à valider\n6) Retour");
                } else {
                    System.out.println("\nGestion personnage (" + p.getNom() + ") :\n1) Consulter les épisodes validés\n2) Modifier un épisode brouillon\n3) Créer un nouvel épisode\n4) Lister les épisodes à valider\n5) Consulter un épisode à valider\n6) Transférer à un autre utilisateur\n7) Changer de MJ\n8) Retour");
                }
                String cmd = scanner.nextLine().trim();
                if (cmd.equals("1")) {
                    // Consulter épisodes validés
                    java.util.List<Episode> eps = p.getBiographie().getEpisodes();
                    boolean found = false;
                    for (Episode e : eps) {
                        if (e.getStatut() == polytech.info5.gl.projet.model.StatutEpisode.VALIDE) {
                            found = true;
                            System.out.println("--- id=" + e.getId() + " | " + (e.getTitre()!=null?e.getTitre():"(sans titre)") + " ---");
                            System.out.println("Date: " + e.getDateRelative());
                            for (Paragraphe par : e.getParagraphes()) {
                                if (par.isPublique()) System.out.println("- " + par.getTexte());
                            }
                            System.out.println("---");
                        }
                    }
                    if (!found) System.out.println("Aucun épisode validé.");
                } else if (cmd.equals("2")) {
                    // Modifier épisode brouillon
                    java.util.List<Episode> eps = p.getBiographie().getEpisodes();
                    java.util.List<Episode> brouillons = new java.util.ArrayList<>();
                    for (Episode e : eps) if (e.getStatut() == polytech.info5.gl.projet.model.StatutEpisode.BROUILLON) brouillons.add(e);
                    if (brouillons.isEmpty()) { System.out.println("Aucun brouillon disponible."); continue; }
                    System.out.println("Brouillons disponibles :");
                    for (Episode e : brouillons) System.out.println("- id=" + e.getId() + " | " + (e.getTitre()!=null?e.getTitre():"(sans titre)") );
                    System.out.print("Id de l'épisode à modifier: "); String sid = scanner.nextLine().trim();
                    try {
                        int ide = Integer.parseInt(sid);
                        // Afficher le brouillon sélectionné avant modification (permet de le consulter)
                        Episode selected = episodeCtrl.findEpisodeById(ide);
                        if (selected == null) { System.out.println("Épisode introuvable"); continue; }
                        System.out.println("--- Episode id=" + selected.getId() + " ---");
                        System.out.println("Titre: " + (selected.getTitre()!=null?selected.getTitre():"(sans titre)"));
                        System.out.println("Date: " + selected.getDateRelative());
                        System.out.println("Statut: " + selected.getStatut());
                        for (Paragraphe par : selected.getParagraphes()) System.out.println((par.isPublique()?"[pub] ":"[sec] ") + par.getTexte());

                        System.out.print("Souhaitez-vous modifier cet épisode? (o/N): "); String modify = scanner.nextLine().trim();
                        if (!modify.equalsIgnoreCase("o")) continue;

                        System.out.print("Nouveau titre (laisser vide pour ne pas changer): "); String nt = scanner.nextLine().trim();
                        System.out.print("Nouvelle date relative (laisser vide pour ne pas changer): "); String nd = scanner.nextLine().trim();
                        boolean ok = episodeCtrl.modifierEpisode(ide, nt.isBlank()?null:nt, nd.isBlank()?null:nd, u);
                        System.out.println(ok ? "Épisode modifié" : "Échec modification");
                        // option pour ajouter paragraphe
                        System.out.print("Ajouter un paragraphe? (o/N): "); String rep = scanner.nextLine().trim();
                        if (rep.equalsIgnoreCase("o")) {
                            System.out.print("Texte du paragraphe: "); String txt = scanner.nextLine().trim();
                            System.out.print("Public? (o/N): "); String sp = scanner.nextLine().trim(); boolean estSecret = !sp.equalsIgnoreCase("o");
                            episodeCtrl.ajouterParagraphe(ide, txt, estSecret, 1, u);
                            System.out.println("Paragraphe ajouté (ordre 1)");
                        }
                    } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                } else if (cmd.equals("3")) {
                    // Créer nouvel épisode
                    System.out.print("Titre de l'épisode: "); String titre = scanner.nextLine().trim();
                    System.out.print("Date relative: "); String dateRel = scanner.nextLine().trim();
                    Episode e = episodeCtrl.creerEpisode(p.getId(), titre, dateRel, u);
                    System.out.println(e == null ? "Échec création" : "Épisode créé (id=" + e.getId() + ")");
                } else if (cmd.equals("4")) {
                    // Lister épisodes à valider
                    boolean any = false;
                    for (Episode e : p.getBiographie().getEpisodes()) {
                        if (e.getStatut() == polytech.info5.gl.projet.model.StatutEpisode.EN_ATTENTE_VALIDATION) {
                            any = true;
                            System.out.println("- id=" + e.getId() + " | " + (e.getTitre()!=null?e.getTitre():"(sans titre)") + " | statut=" + e.getStatut());
                        }
                    }
                    if (!any) System.out.println("Aucun épisode en attente de validation.");
                } else if (cmd.equals("5")) {
                    // Consulter un épisode à valider (et permettre validation si MJ)
                    System.out.print("Id de l'épisode à consulter: "); String sid = scanner.nextLine().trim();
                    try {
                        int ide = Integer.parseInt(sid);
                        Episode e = episodeCtrl.findEpisodeById(ide);
                        if (e == null) { System.out.println("Épisode introuvable"); continue; }
                        System.out.println("--- Episode id=" + e.getId() + " ---");
                        System.out.println("Titre: " + (e.getTitre()!=null?e.getTitre():"(sans titre)"));
                        System.out.println("Date: " + e.getDateRelative());
                        System.out.println("Statut: " + e.getStatut());
                        for (Paragraphe par : e.getParagraphes()) System.out.println((par.isPublique()?"[pub] ":"[sec] ") + par.getTexte());
                        // si utilisateur est MJ du personnage, proposer validation
                        if (p.getMJ() != null && u != null && p.getMJ().getId() == u.getId()) {
                            System.out.print("Valider cet épisode? (o/N): "); String rep = scanner.nextLine().trim();
                            if (rep.equalsIgnoreCase("o")) {
                                boolean ok = episodeCtrl.validerEpisode(ide, u);
                                System.out.println(ok ? "Épisode validé" : "Échec validation");
                            }
                        }
                    } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
                } else {
                    if (isMJ) {
                        if (cmd.equals("6")) return;
                        else System.out.println("Commande inconnue");
                    } else {
                        if (cmd.equals("6")) {
                            // Transférer à un autre utilisateur
                            System.out.print("Id du nouvel utilisateur (ou 'l' pour lister): "); String sid = scanner.nextLine().trim();
                            while (sid.equalsIgnoreCase("l")) {
                                java.util.List<Utilisateur> users = auth.getAllUsers();
                                if (users.isEmpty()) System.out.println("Aucun utilisateur."); else for (Utilisateur uu : users) System.out.println("- id="+uu.getId()+" | "+uu.getNom());
                                System.out.print("Id du nouvel utilisateur (laisser vide pour annuler, 'l' pour relister): "); sid = scanner.nextLine().trim();
                            }
                            try {
                                int idNew = Integer.parseInt(sid);
                                boolean ok = pc.cederPersonnage(p.getId(), idNew, u);
                                System.out.println(ok ? "Transfert effectué" : "Échec du transfert");
                                if (ok) return; // cédé, sortir
                            } catch (NumberFormatException ex) { System.out.println("Identifiant invalide ou opération annulée"); }
                        } else if (cmd.equals("7")) {
                            // Changer de MJ (demande)
                            System.out.print("Id du MJ souhaité (ou 'l' pour lister): "); String sid = scanner.nextLine().trim();
                            while (sid.equalsIgnoreCase("l")) {
                                java.util.List<Utilisateur> users = auth.getAllUsers();
                                if (users.isEmpty()) System.out.println("Aucun utilisateur."); else for (Utilisateur uu : users) System.out.println("- id="+uu.getId()+" | "+uu.getNom());
                                System.out.print("Id du MJ souhaité (laisser vide pour annuler, 'l' pour relister): "); sid = scanner.nextLine().trim();
                            }
                            try {
                                int idmj = Integer.parseInt(sid);
                                boolean ok = pc.demanderChangementMJ(p.getId(), idmj, u);
                                System.out.println(ok ? "Demande de changement de MJ enregistrée" : "Échec de la demande");
                            } catch (NumberFormatException ex) { System.out.println("Identifiant invalide ou opération annulée"); }
                        } else if (cmd.equals("8")) { return; }
                        else System.out.println("Commande inconnue");
                    }
                }
            }
        } catch (NumberFormatException ex) { System.out.println("Identifiant invalide"); }
    }

    public static void main(String[] args) {
        new ConsoleApp().run();
    }
}
