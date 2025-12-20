package polytech.info5.gl.projet.persistence;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import polytech.info5.gl.projet.controller.AuthController;
import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.model.Episode;
import polytech.info5.gl.projet.model.Paragraphe;
import polytech.info5.gl.projet.model.Personnage;
import polytech.info5.gl.projet.model.Univers;
import polytech.info5.gl.projet.model.Utilisateur;

/**
 * Gestion simple de la persistence JSON de l'état de l'application.
 */
public class PersistenceManager {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class ApplicationState {
        public List<PersonnageDTO> personnages = new ArrayList<>();
        public Integer utilisateurConnecteId;
        public List<UtilisateurDTO> utilisateurs = new ArrayList<>();
        public List<UniversDTO> universes = new ArrayList<>();
    }

    public static class UtilisateurDTO {
        public int id; public String nom; public String email; public String passwordHash;
    }

    public static class UniversDTO { public int id; public String nom; public String description; }

    public static class ParagrapheDTO { public int id; public int ordre; public String texte; public boolean publique; }

    public static class EpisodeDTO {
        public int id; public String titre; public String dateRelative; public String statut;
        public boolean isValideParMJ = false; public boolean isValideParJoueur = false;
        public List<ParagrapheDTO> paragraphes = new ArrayList<>();
    }

    public static class PersonnageDTO {
        public int id; public String nom; public String dateNaissance; public String profession;
        public Integer joueurId; public Integer mjId; public Integer mjEnAttenteId; public UtilisateurDTO MJ; public UniversDTO univers;
        public Integer universId;
        public String portraitPath;
        public List<EpisodeDTO> episodes = new ArrayList<>();
        public boolean isValide = false;
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
                // store only MJ id to avoid duplicating full user objects
                pd.mjId = p.getMJ().getId();
            }
            if (p.getMjEnAttente() != null) {
                pd.mjEnAttenteId = p.getMjEnAttente().getId();
            }
            pd.isValide = p.isValide();
            if (p.getUnivers() != null) {
                // store only the universe id reference on the personnage DTO; univers full objects are saved at top-level
                pd.universId = p.getUnivers().getId();
            }
            if (p.getPortraitPath() != null) pd.portraitPath = p.getPortraitPath();
            if (p.getBiographie() != null) {
                for (Episode e : p.getBiographie().getEpisodes()) {
                    EpisodeDTO ed = new EpisodeDTO(); ed.id = e.getId(); ed.titre = e.getTitre(); ed.dateRelative = e.getDateRelative(); ed.statut = e.getStatut() != null ? e.getStatut().name() : null;
                    ed.isValideParMJ = e.isValideParMJ();
                    ed.isValideParJoueur = e.isValideParJoueur();
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

        // save known universes (unique)
        java.util.Map<Integer, UniversDTO> uniMap = new java.util.HashMap<>();
        for (Personnage p : pc.listerTous()) {
            if (p.getUnivers() != null) {
                int id = p.getUnivers().getId();
                if (!uniMap.containsKey(id)) {
                    UniversDTO udto = new UniversDTO(); udto.id = p.getUnivers().getId(); udto.nom = p.getUnivers().getNom(); udto.description = p.getUnivers().getDescription();
                    uniMap.put(id, udto);
                }
            }
        }
        if (!uniMap.isEmpty()) st.universes.addAll(uniMap.values());

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
    public static List<Personnage> toPersonnages(List<PersonnageDTO> dtos, List<Utilisateur> users, List<UniversDTO> universes) {
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
            // prefer restoring MJ by mjId (newer format), fallback to nested MJ DTO (legacy)
            if (pd.mjId != null && users != null) {
                for (Utilisateur u : users) {
                    if (u != null && u.getId() == pd.mjId) { p.setMJ(u); break; }
                }
            } else if (pd.MJ != null) {
                p.setMJ(new Utilisateur(pd.MJ.id, pd.MJ.nom, pd.MJ.email, pd.MJ.passwordHash));
            }
            // restore mjEnAttente if present
            if (pd.mjEnAttenteId != null && users != null) {
                for (Utilisateur u : users) {
                    if (u != null && u.getId() == pd.mjEnAttenteId) { p.setMjEnAttente(u); break; }
                }
            }
            // restore validation status
            p.setValide(pd.isValide);
            // restore univers by id reference if available in DTO (backwards compatible if nested univers was present)
            try {
                java.lang.reflect.Field fid = PersonnageDTO.class.getDeclaredField("universId");
                fid.setAccessible(true);
                Object val = fid.get(pd);
                if (val instanceof Integer) {
                    Integer uid = (Integer) val;
                    if (uid != null && universes != null) {
                        for (UniversDTO ud : universes) {
                            if (ud != null && ud.id == uid) { p.setUnivers(new Univers(ud.id, ud.nom, ud.description)); break; }
                        }
                    }
                }
            } catch (NoSuchFieldException nsf) {
                // fallback to legacy nested univers
                if (pd.univers != null) p.setUnivers(new Univers(pd.univers.id, pd.univers.nom, pd.univers.description));
            } catch (Exception ignored) {}
            // restore portrait path if present
            try { if (pd.portraitPath != null) p.setPortraitPath(pd.portraitPath); } catch (Exception ignored) {}
            if (pd.episodes != null) {
                for (EpisodeDTO ed : pd.episodes) {
                    Episode e = new Episode(); e.setId(ed.id); e.setTitre(ed.titre); e.setDateRelative(ed.dateRelative);
                    if (ed.isValideParJoueur) e.validerParJoueur(null);
                    if (ed.isValideParMJ) e.validerParMJ(null);
                    // ensure statut reflects restored flags if provided
                    if (ed.statut != null) {
                        try { e.setStatut(polytech.info5.gl.projet.model.StatutEpisode.valueOf(ed.statut)); } catch (Exception ignored) {}
                    }
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
