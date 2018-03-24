# Property-based Testing in Java - Patterns

Patterns
   Wer das erste mal versucht, Properties f�r seine eigene Software zu finden, stellt schnell fest, dass dies ein anderes Denken erfordert, als das Erstellen typischer Beispiel-Tests. Aber auch hier kann man sich auf die Schultern anderer stellen. So existiert ein kleiner Kanon von Patterns zum Finden geeigneter Properties [7]. Einige dieser Muster m�chte ich hier kurz vorstellen:
* Obvious Property: 
Manchmal besteht die Spezifikation selbst (zumindest teilweise) aus Properties.
* Fuzzying: 
Der Code soll nicht explodieren, auch wenn man ihn mit sehr unterschiedlichen Eingangsdaten beschie�t.
* Inverse Functions:
Wendet man auf den R�ckgabewert einer Funktion die Umkehrfunktion an, dann sollte wieder der urspr�ngliche Eingangswert herauskommen.
* Idempotent Functions:
Die mehrfache Anwendung mancher Funktionen ver�ndert das Ergebnis nicht.
* Test Oracle:
Manchmal existiert f�r einen Algorithmus eine alternative Implementierung, die man auf alle Beispiele anwenden kann. Mit einer solchen Alternative zur Hand, kann man leicht eine Property formulieren, welche die Ergebnisse unserer Implementierung mit den Ergebnissen des �Orakels� vergleicht. 
   
   Dies ist sicher keine vollst�ndige Liste, hilft aber dabei, einen Anfang zu finden. Je mehr man �ber Properties des eigenen Codes nachdenkt, desto mehr Gelegenheiten sieht man, bei denen man aus den altbekannten Beispiel-Tests schlie�lich Property-Tests ableiten machen kann. Manchmal als Erg�nzung, manchmal aber auch als Ersatz. 
Fazit
   Property-based Testing ist keine neue, aber eine in der OO-Welt bislang nur selten eingesetzte Technik. Sie basiert darauf, dass man f�r Funktionen, Komponenten oder auch ganze Programme allgemeing�ltige, erw�nschte Eigenschaften (Properties) finden kann. Diese Properties lassen sich h�ufig mit Hilfe teil-randomisierter Beispieldaten falsifizieren, was meist auf einen Bug in der Software oder einer L�cke in der Spezifikation hinweist.

Johannes Link besch�ftigt sich seit Ende des letzten Jahrhunderts mit Extreme Programming und anderen agilen Ans�tzen. Ein wesentlicher Schwerpunkt dabei war und ist die testgetriebene Entwicklung. Johannes war einer der K�pfe hinter der Konzeption und Umsetzung der JUnit-5-Plattform und ist Maintainer von jqwik.net.
Links & Literatur
[7] Property-based Testing Patterns. https://blog.ssanj.net/posts/2016-06-26-property-based-testing-patterns.html
