package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;

public class ConsoleIntegrationTest {

    @Test
    public void testConsoleFlowRegisterCreateListExit() throws Exception {
        String input = "1\n" +            // Register
                   "TestUser\n" +
                   "test@example.com\n" +
                   "pwd\n" +
                   // Navigate new menu: Profil joueur -> Personnages -> Créer personnage
                   "1\n" + // main menu: Profil joueur
                   "1\n" + // player menu: Personnages
                   "2\n" + // personnages menu: Créer personnage
                   "PC1\n" +
                   "0\n" +
                   "Mage\n" +
                   "Init bio\n" +
                   "1\n" + // personnages menu: Lister mes personnages
                   // return back to main and exit
                   "6\n" + // return from personnages menu
                   "2\n" + // return from player menu
                   "4\n";  // Exit

        InputStream sysInBackup = System.in;
        PrintStream sysOutBackup = System.out;
        String prev = System.getProperty("app.state.path");
        System.setProperty("app.state.path", "target/test-state-console.json");

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        System.setIn(in);
        System.setOut(new PrintStream(out));

        try {
            // Lancer l'application console
            polytech.info5.gl.projet.console.ConsoleApp.main(new String[]{});

            String consoleOutput = out.toString("UTF-8");

            // Vérifier que le nom utilisateur et le nom du personnage apparaissent dans la sortie
            assertTrue(consoleOutput.contains("TestUser"));
            assertTrue(consoleOutput.contains("PC1"));
            // Vérifier que l'application affiche la sortie
            assertTrue(consoleOutput.contains("Au revoir") || consoleOutput.contains("Au revoir"));
        } finally {
            System.setIn(sysInBackup);
            System.setOut(sysOutBackup);
            if (prev == null) System.clearProperty("app.state.path"); else System.setProperty("app.state.path", prev);
            // cleanup test file if created
            try { new File("target/test-state-console.json").delete(); } catch (Exception ignored) {}
        }
    }
}
