package de.vibora.viborafeed;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Dieser Unittest prüft die korrekte Verarbeitung von Datum, Doc und XML Daten, die
 * beim Auswerten der Feed-Quelle auftreten.
 */
public class FeedContractTest {

    private final String testDateStr = "Sat, 09 Jul 2016 08:30:04 +0000";
    private final String testDbFriendly = "2016-07-09 10:30:04"; // GMT+2 ?!

    private final String testTitle2 = "TP-Link Archer T2UH unter Linux";

    private final String testHtml = "<p>Diesmal nutzt der Code FLTK für 2D Bilder. Ja, ja. Ich "+
            "verstehe es doch selber nicht, was mich an diesen Mandelbrot-Fraktalen so "+
            "interessiert! Jetzt ist vorgestellt und nun sollte ... <a title=\"Schon wieder: Mand"+
            "elbrot mit FLTK\" class=\"read-more\" href=\"http://vibora.de/fltk.html\">weiterlesen</a></p>\n" +
            "<p>Der Beitrag <a rel=\"nofollow\" href=\"http://vibora.de/2016/02/mandelbrot-m"+
            "it-fltk.html\">Schon wieder: Mandelbrot mit FLTK</a> erschien zuerst "+
            "auf <a rel=\"nofollow\" href=\"http://vibora.de\">Vibora.de</a>.</p>\n";

    private final String testFeed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"\n" +
            "\txmlns:content=\"http://purl.org/rss/1.0/modules/content/\"\n" +
            "\txmlns:wfw=\"http://wellformedweb.org/CommentAPI/\"\n" +
            "\txmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
            "\txmlns:atom=\"http://www.w3.org/2005/Atom\"\n" +
            "\txmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\"\n" +
            "\txmlns:slash=\"http://purl.org/rss/1.0/modules/slash/\"\n" +
            "\t>\n" +
            "\n" +
            "<channel>\n" +
            "\t<title>Vibora.de</title>\n" +
            "\t<atom:link href=\"http://vibora.de/feed\" rel=\"self\" type=\"application/rss+xml\" />\n" +
            "\t<link>http://vibora.de</link>\n" +
            "\t<description>Die Schlange durch die OpenSource Welt</description>\n" +
            "\t<lastBuildDate>Mon, 11 Jul 2016 10:34:30 +0000</lastBuildDate>\n" +
            "\t<language>de-DE</language>\n" +
            "\t<sy:updatePeriod>hourly</sy:updatePeriod>\n" +
            "\t<sy:updateFrequency>1</sy:updateFrequency>\n" +
            "\t<generator>https://wordpress.org/?v=4.5.3</generator>\n" +
            "\t<item>\n" +
            "\t\t<title>Eigene App bei f-Droid publizieren</title>\n" +
            "\t\t<link>http://vibora.de/2016/07/eigene-app-bei-f-droid-publizieren.html</link>\n" +
            "\t\t<comments>http://vibora.de/2016/07/eigene-app-bei-f-droid-publizieren.html#respond</comments>\n" +
            "\t\t<pubDate>Sat, 09 Jul 2016 08:30:04 +0000</pubDate>\n" +
            "\t\t<dc:creator><![CDATA[Jochen]]></dc:creator>\n" +
            "\t\t\t\t<category><![CDATA[Smartphone]]></category>\n" +
            "\t\t<category><![CDATA[Android]]></category>\n" +
            "\t\t<category><![CDATA[F-Droid]]></category>\n" +
            "\n" +
            "\t\t<guid isPermaLink=\"false\">http://vibora.de/?p=566</guid>\n" +
            "\t\t<description><![CDATA[<p>Ich selber nutze nicht den Google Play Store, da sich die Free OpenSource Software (FOSS) auch im F-Droid Store befinden. Weil ich vor habe, meine Apps als OpenSource zu veröffentlichen, sollte es ja auch möglich sein, dass meine App in den F-Droid Store kommt. Ich habe nun einen Text-Editor in F-Droid veröffentlicht. Zuerst hatte er ... <a title=\"Eigene App bei f-Droid publizieren\" class=\"read-more\" href=\"http://vibora.de/2016/07/eigene-app-bei-f-droid-publizieren.html\">weiterlesen</a></p>\n" +
            "<p>Der Beitrag <a rel=\"nofollow\" href=\"http://vibora.de/2016/07/eigene-app-bei-f-droid-publizieren.html\">Eigene App bei f-Droid publizieren</a> erschien zuerst auf <a rel=\"nofollow\" href=\"http://vibora.de\">Vibora.de</a>.</p>\n" +
            "]]></description>\n" +
            "\t\t<wfw:commentRss>http://vibora.de/2016/07/eigene-app-bei-f-droid-publizieren.html/feed</wfw:commentRss>\n" +
            "\t\t<slash:comments>0</slash:comments>\n" +
            "\t\t</item>\n" +
            "\t\t<item>\n" +
            "\t\t<title>TP-Link Archer T2UH unter Linux</title>\n" +
            "\t\t<link>http://vibora.de/2016/06/tp-link-archer-t2uh-unter-linux.html</link>\n" +
            "\t\t<comments>http://vibora.de/2016/06/tp-link-archer-t2uh-unter-linux.html#respond</comments>\n" +
            "\t\t<pubDate>Sat, 25 Jun 2016 13:06:33 +0000</pubDate>\n" +
            "\t\t<dc:creator><![CDATA[Philipp Krönke]]></dc:creator>\n" +
            "\t\t\t\t<category><![CDATA[Allgemein]]></category>\n" +
            "\n" +
            "\t\t<guid isPermaLink=\"false\">http://vibora.de/?p=553</guid>\n" +
            "\t\t<description><![CDATA[<p>WLAN und Linux war in der Vergangenheit immer ein Problem. Genauso, wie Grafikkarten-Treiber und Linux. In den letzten Jahren hat das ganze allerdings durch das Linux-Wireless Projekt deutlich an Problematik verloren. Viele WLAN-Module funktionieren heute bereits OOTB. Der USB-Stick TP-Link Archer T2UH AC600  zählt allerdings nicht dazu. Ich habe ihn allerdings trotzdem mit etwas Bastelei ... <a title=\"TP-Link Archer T2UH unter Linux\" class=\"read-more\" href=\"http://vibora.de/2016/06/tp-link-archer-t2uh-unter-linux.html\">weiterlesen</a></p>\n" +
            "<p>Der Beitrag <a rel=\"nofollow\" href=\"http://vibora.de/2016/06/tp-link-archer-t2uh-unter-linux.html\">TP-Link Archer T2UH unter Linux</a> erschien zuerst auf <a rel=\"nofollow\" href=\"http://vibora.de\">Vibora.de</a>.</p>\n" +
            "]]></description>\n" +
            "\t\t<wfw:commentRss>http://vibora.de/2016/06/tp-link-archer-t2uh-unter-linux.html/feed</wfw:commentRss>\n" +
            "\t\t<slash:comments>0</slash:comments>\n" +
            "\t\t</item>\n" +
            "\t\t<item>\n" +
            "\t\t<title>Schon wieder: Mandelbrot mit FLTK</title>\n" +
            "\t\t<link>http://vibora.de/2016/02/mandelbrot-mit-fltk.html</link>\n" +
            "\t\t<comments>http://vibora.de/2016/02/mandelbrot-mit-fltk.html#respond</comments>\n" +
            "\t\t<pubDate>Thu, 18 Feb 2016 23:16:13 +0000</pubDate>\n" +
            "\t\t<dc:creator><![CDATA[Jochen]]></dc:creator>\n" +
            "\t\t\t\t<category><![CDATA[Allgemein]]></category>\n" +
            "\t\t<category><![CDATA[Kurioses]]></category>\n" +
            "\n" +
            "\t\t<guid isPermaLink=\"false\">http://vibora.de/?p=375</guid>\n" +
            "\t\t<description><![CDATA[<p>Diesmal nutzt der Code FLTK für 2D Bilder. Ja, ja. Ich verstehe es doch selber nicht, was mich an diesen Mandelbrot-Fraktalen so interessiert! Jetzt ist es schon wieder nach 23 Uhr und ich habe wieder nichts Brauchbares für die Klausuren gelernt :-S Das Projekt habe ich vor ein paar Tagen erfolgreich vorgestellt und nun sollte ... <a title=\"Schon wieder: Mandelbrot mit FLTK\" class=\"read-more\" href=\"http://vibora.de/2016/02/mandelbrot-mit-fltk.html\">weiterlesen</a></p>\n" +
            "<p>Der Beitrag <a rel=\"nofollow\" href=\"http://vibora.de/2016/02/mandelbrot-mit-fltk.html\">Schon wieder: Mandelbrot mit FLTK</a> erschien zuerst auf <a rel=\"nofollow\" href=\"http://vibora.de\">Vibora.de</a>.</p>\n" +
            "]]></description>\n" +
            "\t\t<wfw:commentRss>http://vibora.de/2016/02/mandelbrot-mit-fltk.html/feed</wfw:commentRss>\n" +
            "\t\t<slash:comments>0</slash:comments>\n" +
            "\t\t</item>\n" +
            "\t</channel>\n" +
            "</rss>";


    @Test
    public void testRawToDate() throws Exception {
        Date date = FeedContract.rawToDate(testDateStr);
        assertNotNull(date);
        assertNotEquals(date.getTime(), 0);
        assertTrue(date.before(new Date()));
    }

    @Test
    public void testDbFriendlyDate() throws Exception {
        Date date = FeedContract.rawToDate(testDateStr);
        String dbdate = FeedContract.dbFriendlyDate(date);
        assertEquals(testDbFriendly, dbdate);
    }

    @Test
    public void testRemoveHtml() throws Exception {
        String result = new FeedContract().removeHtml(testHtml, false);

        assertTrue(result.length() > 0);
        assertFalse(result.contains("<p>"));
        assertTrue(result.contains("Diesmal nutzt der Code FLTK für 2D Bilder"));
        assertFalse(result.contains(ViboraApp.Config.DEFAULT_lastRssWord));
    }

    @Test
    public void testExtract() throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        InputStream is = new ByteArrayInputStream(testFeed.getBytes());
        Document doc = db.parse(is);
        NodeList nodeList = doc.getElementsByTagName("item");

        Node n = nodeList.item(0);
        String body = FeedContract.extract(n, "description");
        String dateStr = FeedContract.extract(n, "pubDate");
        assertEquals(testDateStr, dateStr);
        assertTrue(body.length() > 0);

        n = nodeList.item(1);
        String title = FeedContract.extract(n, "title");
        assertEquals(testTitle2, title);
    }
}