It's been 3 months that I tried to shed some light on the importance of
[shrinking failed propertie]({% post_url 2018-04-20-the-importance-of-being-shrunk %}).
Now that we've covered many of the technicalities of the approach it's
time to occupy with the how to use PBT in practice.

# Property-based Testing in Java - Patterns

When you're taking your early steps with PBT finding suitable properties can
feel like an almost impossible task. Whereas examples often appear naturally
when thinking and talking about the functionality of a system, properties
are often more abstract. They require a somewhat different kind
of thinking to be discovered. A set of useful _patterns_ for finding
properties would come in handy.

Luckily, we do not have to discover all things on our own. PBT has
been around for a while and there is a small but well known collection
of [Property-based Testing Patterns](https://blog.ssanj.net/posts/2016-06-26-property-based-testing-patterns.html).
Some of those are worth being discussed here:

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

