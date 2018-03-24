# Property-based Testing in Java - From Example Tests to Properties


Testgetriebene Entwicklung in objektorientierten Sprachen setzt bislang meist auf beispielbasierte Testf�lle, wie man sie leicht mit JUnit und �hnlichen Testframeworks erstellen kann. Schaut man jedoch �ber den Tellerrand und auf funktionale Programmiersprachen wie z.B. Haskell oder F#, findet man dort etwas anderes: Property-Tests. 
Property-Tests basieren auf der Idee, die erw�nschten Eigenschaften (engl. properties) unseres Programms zu beschreiben und anschlie�end das Framework selbstst�ndig Testf�lle generieren zu lassen, die diese Eigenschaften �berpr�fen und m�glicherweise falsifizieren. Auch in Java ist das m�glich und sinnvoll. 

   Beginnen wir die Reise ins Land der Properties mit einem einfachen JUnit-5-Jupiter-Test [1] f�r eine g�ngige JDK-Funktion: java.util.Collections.reverse() dient dazu, die Reihenfolge aller Elemente einer Liste umzukehren. Ein typischer Test wird diese Funktionalit�t anhand eines konkreten Beispiels �berpr�fen � in unserem Fall einer Liste mit drei Elementen (siehe Listing 1).
   
    Listing 1: Beispieltest f�r Collections.reverse()
     import java.util.*;
     import org.assertj.core.api.Assertions;
     import org.junit.jupiter.api.Test;

     class CollectionsTests {
     	@Test
     	void reverseList() {
     		List<Integer> aList = Arrays.asList(1, 2, 3);
     		Collections.reverse(aList);
     		Assertions.assertThat(aList).containsExactly(3, 2, 1);
     	}
     }
    Ende Listing 1

   Mit solchen Tests arbeiten (hoffentlich) die meisten Entwickler seit Jahren. Doch ein Gedanke treibt mich dabei immer wieder um: Woher wei� ich, dass reverse auch mit 5 Elementen funktioniert? Und mit 5000? Und mit der leeren Menge? Und mit String-Elementen? Und, und, und...
   Diese Unsicherheit bek�mpfe ich typischerweise durch das Hinzuf�gen zus�tzlicher Beispiel-Tests und der Hoffnung, dass meine ausgesuchten Beispiele ausreichend repr�sentativ sind, um jetzt und in Zukunft m�gliche Implementierungsfehler zu entdecken und Regressionen zu vermeiden. Modelbasierte Testans�tze wie z.B. �quivalenzklassenbildung versuchen genau dieses Problem abzuschw�chen.
Was ist eine Property?
   Tats�chlich kann man sich der zu validierenden Funktionalit�t auch von einer anderen Seite n�hern, n�mlich mit der Fragestellung: Unter welchen Vorbedingungen (z.B. erlaubte Eingangsparameter) m�ssen welche allgemeinen Eigenschaften (Invarianten und Nachbedingungen) erf�llt sein? Dieses Gespann aus Vorbedingungen plus allgemeine Eigenschaften nennt man insbesondere im Bereich der funktionalen Programmierung auch Property. 
   Formulieren wir zur Anschauung eine Property f�r die reverse-Funktion in Prosa: �F�r jede eingehende Liste ergibt die zweifache Anwendung von reverse wieder die urspr�ngliche Liste�. Formuliert man diese Property nun in einer Form, die der Computer interpretieren kann � z.B. als Programmcode � und �berl�sst dann die Generierung von Beispielen, die den Vorbedingungen entsprechen, eben diesem Computer, dann sind wir beim Property-based Testing (PBT) angelangt.
QuickCheck
   Der Urvater aller PBT-Frameworks ist QuickCheck f�r Haskell [2]. Betrachten wir daher zun�chst die Umsetzung der obigen Property in QuickCheck:
     prop_reversed :: [Int] -> Bool 
     prop_reversed xs =             
         reverse (reverse xs) == xs 
   
   Als stark typisierte funktionale Sprache bietet Haskell hier den Vorteil einer sehr knappen Formulierung der Property-Funktion prop_reversed in lediglich drei Zeilen:
* Zeile 1 liefert die Typinformation: Eine Liste von Int als Eingabetyp und ein boolescher Wert als R�ckgabetyp. Dass wir uns auf Listen von ganzen Zahlen beschr�nken ist ein Zugest�ndnis an die Funktionsweise von QuickCheck. Aus Sicht der abstrakten Property ist der konkrete Typ der Listenelemente uninteressant.
* Zeile 2 und 3 sind die Implementierung: Eingangsliste xs wird zweimal der Funktion reverse �bergeben und das Ergebnis mit der Eingangsliste verglichen.

   Zur Testlaufzeit generiert QuickCheck eine gr��ere Anzahl � im Normalfall sind es 100 � Listen und ruft prop_reversed damit auf. Liefert auch nur einer dieser Aufrufe False zur�ck, gilt die Property als falsifiziert. In diesem Sinne ist eine Property meist nicht beweisbar, sondern lediglich widerlegbar. Man erkennt auch, dass diese Property zwar notwendig aber nicht hinreichend f�r eine korrekte Implementierung ist, denn auch die Trivialimplementierung von reverse � Ausgabeliste = Eingabeliste � w�re erfolgreich. 
PBT mit Java
   Die Umsetzung der gleichen Property in Java ist mit den Bordmitteln von JUnit oder �hnlichen Frameworks nicht einfach m�glich; daher holen wir uns eine zus�tzliche Test-Engine (siehe Kasten) an Bord: jqwik [3]. Listing 2 stellt eine Umsetzung der QuickCheck-Property als jqwik-Property dar. Da Collection.reverse ihren Parameter in-place ver�ndert, ist Testcode inhaltlich ein wenig anders, denn wir m�ssen zu Beginn eine Kopie der  �bergebenen Liste anlegen.
   
    Listing 2: Reversed-Property mit jqwik
     import java.util.*;
     import net.jqwik.api.*;
     
     class CollectionsProperties {
     	@Property
     	boolean reverseTwiceIsOriginal(@ForAll List<Integer> aList) {
     		List<Integer> copy = new ArrayList<>(aList);
     		Collections.reverse(copy);
     		Collections.reverse(copy);
     		return copy.equals(aList);
     	}
     }
     
    Ende Listing 2
   
   Nehmen wir die obige Property-Klasse genauer unter die Lupe:
* Jede Property wird als Methode einer Klasse implementiert. Ebenso wie bei beispielbasierten Testf�llen sollte der Methodenname sprechend sein: reverseTwiceIsOriginal.
* Damit die JUnit-Plattform � und damit auch die IDEs und Build-Werkzeuge � einen Property-Testfall als solchen erkennen, muss er mit @Property annotiert werden. 
* Indem man der Methode Parameter hinzuf�gt und diese mit @ForAll annotiert, teilt man jqwik mit, dass f�r diese Parameter zur Ausf�hrungszeit Beispielwerte generieren werden sollen. Dabei wird der Typ eines Parameters als grundlegende Vorbedingung interpretiert, d.h., nur g�ltige Instanzen dieses Typs sind erlaubt. Zus�tzliche Einschr�nkungen und Bedingungen sind m�glich (siehe Abschnitt �Automatische Generierung�).
* Verwendet die Property-Funktion einen booleschen R�ckgabe-Typ, dann wird das Ergebnis des Methodenaufrufs als Property-Bedingung interpretiert: false f�hrt somit zu einer falsifizierten Property. Alternativ k�nnen Property-Methoden auch void sein, in diesem Fall k�nnen beliebige Assertion-Bibliothen, z.B. aus AssertJ [4] oder JUnit selbst, zum Einsatz kommen.

   
   Die Ausf�hrung einer erfolgreichen jqwik-Property erfolgt genau so stillschweigend wie die eines �normalen� JUnit-5-Tests. Wenn nicht anders instruiert, wird jede einzelne Property-Methode 1000 mal mit unterschiedlichen Eingabewerten ausgef�hrt. Die Angst, dass eine tausendfache Ausf�hrung den Testlauf sp�rbar bremst, ist in den meisten F�llen unbegr�ndet � insbesondere wenn man sich auf feingranulare Microtests konzentriert. Im Einzelfall kann man die Zahl der durchgef�hrten Testl�ufe beliebig nach unten (oder auch oben) ver�ndern.
   Lassen wir die Property testweise scheitern � indem wir eine der beiden Collections.reverse(copy)-Zeilen auskommentieren � dann erhalten wir eine Ausgabe, die der folgenden �hnelt:
   
     timestamp = 2018-01-18T11:57:17.027, 
     tries = 2, 
     checks = 2, 
     seed = -6748802811761023649, 
     originalSample = [[0, 1, -1, -2147483648, �]], 
     sample = [[0, -1]]
     org.opentest4j.AssertionFailedError: 
         Property [reverseTwiceIsOriginal] falsified with sample [[0, -1]]
   
   Eine ganze Menge Informationen: Man sieht die Anzahl der Testversuche (tries), der tats�chlich durchgef�hrten Tests (checks), den Seed des verwendeten Random-Objekts, die urspr�nglich falsifizierten Parameter (originalSample), sowie die einfachsten gefundenen Parameter (sample). Was es mit dem Seed auf sich hat, und wie jqwik vom urspr�nglichen zum kleinsten Sample kommt, werden wir sp�ter noch betrachten.

[1] JUnit 5 User Guide. http://junit.org/junit5/docs/current/user-guide/ 
[2] QuichCheck f�r Haskell. https://hackage.haskell.org/package/QuickCheck 
