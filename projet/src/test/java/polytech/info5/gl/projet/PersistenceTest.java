package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.controller.PersonnageController;
import polytech.info5.gl.projet.controller.AuthController;
import polytech.info5.gl.projet.model.*;
import polytech.info5.gl.projet.persistence.PersistenceManager;

import java.io.File;
import java.util.List;

public class PersistenceTest {

    @Test
    public void testSaveAndLoadState() throws Exception {
        File tmp = new File("target/test-state.json");
        if (tmp.exists()) tmp.delete();

        PersonnageController pc = new PersonnageController();
        AuthController auth = new AuthController();

        Utilisateur u = auth.register("Saver", "saver@example.com", "pw");
        Personnage p = pc.creerPersonnage("PersistP", "00", "Rogue", "bio", u);
        Univers uni = new Univers(77, "U77", "desc");
        p.setUnivers(uni);

        // Save state
        PersistenceManager.saveState(tmp, auth, pc);

        // Restore into fresh controllers
        PersonnageController pc2 = new PersonnageController();
        AuthController auth2 = new AuthController();

        PersistenceManager.ApplicationState st = PersistenceManager.loadState(tmp);
        assertNotNull(st);

        // reconstruct users and personnages
        java.util.List<Utilisateur> users = new java.util.ArrayList<>();
        if (st.utilisateurs != null) {
            for (PersistenceManager.UtilisateurDTO ud : st.utilisateurs) users.add(PersistenceManager.dtoToUtilisateur(ud));
        }
        if (!users.isEmpty()) auth2.chargerUtilisateurs(users);

        List<Personnage> restored = PersistenceManager.toPersonnages(st.personnages, users);
        pc2.chargerPersonnages(restored);
        // set connected user by id
        if (st.utilisateurConnecteId != null) {
            for (Utilisateur uu : users) {
                if (uu.getId() == st.utilisateurConnecteId) { auth2.setUtilisateurConnecte(uu); break; }
            }
        }

        List<Personnage> all = pc2.listerTous();
        assertEquals(1, all.size());
        assertEquals("PersistP", all.get(0).getNom());
        assertNotNull(auth2.getUtilisateurConnecte());
        assertEquals("saver@example.com", auth2.getUtilisateurConnecte().getEmail());

        // cleanup
        tmp.delete();
    }
}
