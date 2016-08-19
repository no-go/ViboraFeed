package de.vibora.viborafeed;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class FeedContentProviderTest {

    @Test
    public void testOnCreate() throws Exception {
        assertNotNull(FeedContentProvider.AUTHORITY);
        assertNotEquals(FeedContentProvider.AUTHORITY, "");
    }
}