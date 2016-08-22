package de.vibora.viborafeed;

import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ViboraAppTest {

    /**
     * Test soll die Default URL der ViboraApp testen.
     * Dieser Test liefert leider manchmal einen Fehler. Es wird ein 404 (Seite nicht erreichbar)
     * zurückgegeben, obwohl die URL erreichbar ist.
     *
     * @throws Exception
     */
    @Test
    public void testFeedUrl() throws Exception {
        assertEquals("http://vibora.de/feed/", ViboraApp.Source1.path);
    }

    @Test
    public void testOnCreate() throws Exception {
        Alarm actual;

        actual = ViboraApp.alarm;
        assertNull(actual);

    }

    @Test
    public void testGetContextOfApplication() throws Exception {
        Context expected, actual;

        actual = ViboraApp.getContextOfApplication();

        expected = null;
        assertEquals(expected, actual);
    }
}