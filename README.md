# ViboraFeed

![Logo](app/src/main/res/mipmap-mdpi/ic_launcher.png) ViboraFeed is the Vibora Feed Reader of the http://vibora.de opensource blog homepage.
The Images in the screenshots are from testing-Feeds.

Optional get a signed APK from here: [ViboraFeed.apk](https://github.com/no-go/ViboraFeed/blob/master/app/app-release.apk?raw=true)

![Screenshots v1.5 beta](img/v15.jpg)

## Seit Version 1.2 (quasi fertig)

Javadoc ist aus meiner Sicht hinreichend gefüllt. Außerdem werden bei gelesenen Feeds die Bilder auch ausgegraut.

![Screenshots v1.2](img/v12.jpg)

## Seit Version 1.1

- Logo und App-Name kleben nicht mehr beieinander
- Optional kann die App nun auch Bilder aus den Feeds extrahieren
- beginn mit JavaDoc

![Screenshots v1.1](img/v11.jpg)

## Seit Version 1.0 (Mitte August 2016)

- Beim Booten wird Alarm automatisch gestartet
- Modified HTTP wird ausgewertet um Traffic zu vermeiden
- ContentProvider wird für Liste genutzt
- bisher keine Probleme mit Strict Mode
- Netzwerk Verbindung wird geprüft und nach ein paar Sekunden ein neuer Syncr. Alarm gestartet
- Feeds, die man als gelöscht markiert hat werden nach 90 Tagen aus der Datenbank entfernt
- Bei neuen Nachrichten wird ein Sound gespielt
- Man kann die Farbe und Blink-Intervall der Notifikations-LED einstellen
- Klick auf Notifikation öffnet die App
- Komplett auch in englischer Sprache

### weitere Features

Einstellen, wie oft nach neuen Feeds geschaut werden soll

![Screenshot Feature 1](img/feature_alarmmanager-sync-intervall.jpg)

Abfrage, ob man die App verlassen will

![Screenshot Feature 2](img/feature_dialog.jpg)

Anzeige, wenn noch keine Feeds geladen wurden

![Screenshot Feature 3](img/feature_empty.jpg)

Möglichkeit, dem Link eines Feeds zur Webseite zu öffnen sowie andere Optionen

![Screenshot Feature 4](img/feature_feed-context-menu.jpg)

Andere Darstellung der Notifikation innerhalb der App

![Screenshot Feature 5](img/feature_inApp-Notifications.jpg)

Jeder neue Feed bekommt eine eigene Notifikation

![Screenshot Feature 7](img/feature_notifications.jpg)

Options-Menü der App

![Screenshot Feature 8](img/feature_options.jpg)

Einstellungen der App (URL war nur zu Testzwecken anpassbar!)

![Screenshot Feature 9](img/feature_preferences.jpg)

Als gelesen markierte Feeds werden grau dargestellt

![Screenshot Feature 10](img/feature_readed.jpg)

Hinweis, wenn man einen Feed gelöscht oder aus der Datenbank entfernt/zerstört hat

![Screenshot Feature 11](img/feature_toast.jpg)
