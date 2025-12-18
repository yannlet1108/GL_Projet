package polytech.info5.gl.projet;

import org.junit.Test;
import static org.junit.Assert.*;
import polytech.info5.gl.projet.model.*;

public class UniversTest {
    @Test
    public void testConstructeurEtGetters() {
        Univers u = new Univers(5, "Galaxie", "immense");
        assertEquals(5, u.getId());
        assertEquals("Galaxie", u.getNom());
        assertEquals("immense", u.getDescription());
    }
}
