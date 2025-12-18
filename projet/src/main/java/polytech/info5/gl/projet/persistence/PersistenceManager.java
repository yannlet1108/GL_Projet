package polytech.info5.gl.projet.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.controller.AuthController;
import polytech.info5.gl.projet.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestion simple de la persistence JSON de l'état de l'application.
 */
public class PersistenceManager {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class ApplicationState {
        public List<PersonnageDTO> personnages = new ArrayList<>();
        public Integer utilisateurConnecteId;
        public List<UtilisateurDTO> utilisateurs = new ArrayList<>();
    }

    public static class UtilisateurDTO {
        public int id; public String nom; public String email; public String passwordHash;
    }

    public static class UniversDTO { public int id; public String nom; public String description; }

    public static class ParagrapheDTO { public int id; public int ordre; public String texte; public boolean publique; }

    public static class EpisodeDTO {
        public int id; public String titre; public String dateRelative; public String statut;
        public List<ParagrapheDTO> paragraphes = new ArrayList<>();
    }

    public static class PersonnageDTO {
        public int id; public String nom; public String dateNaissance; public String profession;
        public Integer joueurId; public UtilisateurDTO MJ; public UniversDTO univers;
        public List<EpisodeDTO> episodes = new ArrayList<>();
    }

    public static void saveState(File target, AuthController auth, PersonnageController pc) throws IOException {
        ApplicationState st = new ApplicationState();

        // utilisateur connecté
        Utilisateur u = auth.getUtilisateurConnecte();
        if (u != null) {
            st.utilisateurConnecteId = u.getId();
        }

        // personnages
        for (Personnage p : pc.listerTous()) {
            PersonnageDTO pd = new PersonnageDTO();
            pd.id = p.getId(); pd.nom = p.getNom(); pd.dateNaissance = p.getDateNaissance(); pd.profession = p.getProfession();
            if (p.getJoueur() != null) {
                pd.joueurId = p.getJoueur().getId();
            }
            if (p.getMJ() != null) {
                UtilisateurDTO md = new UtilisateurDTO(); md.id = p.getMJ().getId(); md.nom = p.getMJ().getNom(); md.email = p.getMJ().getEmail(); md.passwordHash = p.getMJ().getPasswordHash(); pd.MJ = md;
            }
            if (p.getUnivers() != null) {
                UniversDTO udto = new UniversDTO(); udto.id = p.getUnivers().getId(); udto.nom = p.getUnivers().getNom(); udto.description = p.getUnivers().getDescription(); pd.univers = udto;
            }
            if (p.getBiographie() != null) {
                for (Episode e : p.getBiographie().getEpisodes()) {
                    EpisodeDTO ed = new EpisodeDTO(); ed.id = e.getId(); ed.titre = e.getTitre(); ed.dateRelative = e.getDateRelative(); ed.statut = e.getStatut() != null ? e.getStatut().name() : null;
                    if (e.getParagraphes() != null) {
                        for (Paragraphe par : e.getParagraphes()) {
                            ParagrapheDTO pr = new ParagrapheDTO(); pr.id = par.getId(); pr.ordre = par.getOrdre(); pr.texte = par.getTexte(); pr.publique = par.isPublique(); ed.paragraphes.add(pr);
                        }
                    }
                    pd.episodes.add(ed);
                }
            }
            st.personnages.add(pd);
        }

        // utilisateurs enregistrés
        try {
            java.util.List<Utilisateur> authUsers = new java.util.ArrayList<>();
            // attempt to get users from auth controller via reflection-free API
            authUsers = (auth.getAllUsers() != null) ? auth.getAllUsers() : new java.util.ArrayList<>();
            for (Utilisateur u2 : authUsers) {
                UtilisateurDTO ud = new UtilisateurDTO();
                ud.id = u2.getId(); ud.nom = u2.getNom(); ud.email = u2.getEmail(); ud.passwordHash = u2.getPasswordHash();
                st.utilisateurs.add(ud);
            }
        } catch (Exception ignored) {}

        // ensure directory
        File dir = target.getParentFile();
        if (dir != null && !dir.exists()) dir.mkdirs();

        try (Writer w = new FileWriter(target)) {
            gson.toJson(st, w);
        }
    }

    public static ApplicationState loadState(File source) throws IOException {
        if (!source.exists()) return null;
        try (Reader r = new FileReader(source)) {
            com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
            com.google.gson.JsonElement el = parser.parse(r);
            ApplicationState st = gson.fromJson(el, ApplicationState.class);
            // migration support: if the JSON contains an object 'utilisateurConnecte', extract its id
            if (st != null && st.utilisateurConnecteId == null && el.isJsonObject()) {
                com.google.gson.JsonObject obj = el.getAsJsonObject();
                if (obj.has("utilisateurConnecte") && obj.get("utilisateurConnecte").isJsonObject()) {
                    com.google.gson.JsonObject uobj = obj.getAsJsonObject("utilisateurConnecte");
                    if (uobj.has("id") && uobj.get("id").isJsonPrimitive()) {
                        try { st.utilisateurConnecteId = uobj.get("id").getAsInt(); } catch (Exception ignored) {}
                    }
                }
            }
            return st;
        }
    }

    // reconstruction helper: convert DTOs to model objects
    public static List<Personnage> toPersonnages(List<PersonnageDTO> dtos, List<Utilisateur> users) {
        List<Personnage> res = new ArrayList<>();
        if (dtos == null) return res;
        for (PersonnageDTO pd : dtos) {
            Personnage p = new Personnage();
            p.setId(pd.id); p.setNom(pd.nom); p.setDateNaissance(pd.dateNaissance); p.setProfession(pd.profession);
            // set joueur by id lookup
            if (pd.joueurId != null && users != null) {
                for (Utilisateur u : users) {
                    if (u != null && u.getId() == pd.joueurId) { p.setJoueur(u); break; }
                }
            }
            if (pd.MJ != null) p.setMJ(new Utilisateur(pd.MJ.id, pd.MJ.nom, pd.MJ.email, pd.MJ.passwordHash));
            if (pd.univers != null) p.setUnivers(new Univers(pd.univers.id, pd.univers.nom, pd.univers.description));
            if (pd.episodes != null) {
                for (EpisodeDTO ed : pd.episodes) {
                    Episode e = new Episode(); e.setId(ed.id); e.setTitre(ed.titre); e.setDateRelative(ed.dateRelative);
                    if (ed.paragraphes != null) {
                        for (ParagrapheDTO pr : ed.paragraphes) {
                            Paragraphe pp = new Paragraphe(pr.id, pr.ordre, pr.texte, pr.publique);
                            e.ajouterParagraphe(pp);
                        }
                    }
                    p.getBiographie().ajouterEpisode(e);
                }
            }
            res.add(p);
        }
        return res;
    }

    public static Utilisateur dtoToUtilisateur(UtilisateurDTO d) {
        if (d == null) return null;
        return new Utilisateur(d.id, d.nom, d.email, d.passwordHash);
    }
}
