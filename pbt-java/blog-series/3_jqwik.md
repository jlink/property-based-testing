# Property-based Testing in Java - Jqwik

In [the previous episode](???) you've already seen jqwik in action. 
One of the interesting aspects of this PBT library is the fact that it's not 
a standalone framework but that it hooks into JUnit 5 in order to "inherit"
IDE and built-tool support.

## Test-Engine for the JUnit Platform

    Die f�nfte Generation von JUnit kommt nicht nur mit einem aktualisierten 
    Ansatz zur Testerstellung und Ausf�hrung, sondern basiert auf der Idee, 
    als Plattform f�r ein gro�es Spektrum unterschiedlicher Test-Engines dienen zu k�nnen. 
    Der gro�e Vorteil einer solchen Plattform ist zum einen, dass jede IDE und 
    jedes Build-Werkzeug lediglich die JUnit-Plattform integrieren muss. 
    Die Integration der einzelnen Engines erfolgt damit automatisch. 
    Zum anderen erlaubt die Plattform, dass beliebig viele Engines nebeneinander 
    zum Einsatz kommen k�nnen. Damit ist die Verwendung von JUnit 4 (Vintage), JUnit 5 (Jupiter) 
    und jqwik im selben Projekt m�glich, wenn man m�chte sogar in derselben Testklasse.
    
    IntelliJ, Eclipse und Maven-Surefire haben diese Idee mittlerweile aufgegriffen und integrieren in ihren aktuellen Releases die JUnit-5-Plattform. F�r Gradle stellt im Augenblick noch das JUnit-5-Team ein Plugin bereit � nativer JUnit-Plattform-Support ist jedoch bereits in Arbeit.
    Verwendung von jqwik im eigenen Projekt
    Wenn man bereits JUnit 5 im Einsatz hat, dann gen�gt es, die aktuelle Version von jqwik via Maven, Gradle oder einem Download in den test-Klassenpfad zu bef�rdern. Falls jqwik der erste Ber�hrungspunkt zur JUnit-Plattform darstellt, empfiehlt sich die Anleitung im jqwik-User-Guide [5].

## Wildcards and Type Parameters


## Many Parameters

   jqwik generiert typische und zuf�llige Eingabewerte sowie Kombinationen davon. 
   Dabei k�nnen nicht nur � wie im obigen Beispiel � einzelne Parameter generiert werden, 
   sondern mehrere, wie in Listing 3.
   
    Listing 3: Property mit mehreren Parametern
     	@Property(reporting = ReportingMode.GENERATED)
     	boolean joiningTwoLists( 
     			@ForAll List<String> list1, 
     			@ForAll List<String> list2
     	) {
     		List<String> joinedList = new ArrayList<>(list1);
     		joinedList.addAll(list2);
     		return joinedList.size() == list1.size() + list2.size();
     	}
    Ende Listing 3
   
Wenn wir diese Property ausf�hren, dann sorgt der Annotation-Wert 
reporting=Reporting.GENERATED daf�r, dass alle 1000 generierten Werte-Menge auch 
ausgegeben werden. Wer es ausprobiert, kann feststellen, dass sowohl die L�nge der 
generierten Strings als auch die L�nge der generierten Listen sehr gro� sein kann. 
Dass aber auch immer wieder ein leerer String und eine leere Liste auftauchen.

    jqwik ist n�mlich kein vollst�ndig randomisiertes Testwerkzeug, 
    das man ohne Nachzudenken auf ein Programm losl�sst. 
    Stattdessen versucht jqwik auch typische Grenzf�lle � z.B. leere Listen, leere Strings, 
    gr��te Werte � einzustreuen. Dies soll die Wahrscheinlichkeit erh�hen, L�cken 
    in der Spezifikation und Bugs in der Implementierung zu entdecken.

## Automatic Parameter Generation

   Von Haus aus ist jqwik in der Lage, die am h�ufigsten verwendeten JDK-Typen zu generieren. 
   Dazu geh�ren: 
- s�mtliche primitiven Zahlentypen von byte bis float, die zugeh�rigen Boxed-Types, BigInteger und BigDecimal
- String, Character und char
- Boolean und boolean
- Alle enum-Typen
- List<T>, Set<T>, Stream<T> und Optional<T> solange T selbst generiert werden kann
- Arrays von generierbaren Typen
- java.util.Random and Object
   
   F�r viele Typen existiert zus�tzlich die M�glichkeit auf die Generierung 
   mittels zus�tzlicher Annotationen Einfluss zu nehmen. 
   Beispielsweise schreiben wir @StringLength(min = 5, max = 10) 
   um die L�nge generierter Strings auf 5 bis 10 Zeichen einzuschr�nken, 
   und @Positive um lediglich Zahlen gr��er oder gleich Null zu erhalten.

## Programmatic Generation

   Manchmal haben wir mit Klassen zu tun, die jqwik nicht von Haus aus generieren kann. 
   Ein andermal ist die fachliche Einschr�nkung eines primitiven Typs sehr stark. 
   In diesen F�llen bietet uns das Framework die M�glichkeit, die Generierung durch 
   Kombination vorhandener Generatoren selbst in die Hand zu nehmen.
   
    Listing 4: Eigene Provider-Methoden
     @Property
     void letsGeneratePostleitzahlen(@ForAll("postleitzahlen") String zipCode) {
     }
     
     @Provide
     Arbitrary<String> postleitzahlen() {
     	return Arbitraries.strings('0', '9', 5, 5);
     }
    Ende Listing 4
   
   Listing 4 zeigt, wie man mit Hilfe des Annotation-Value postleitzahlen auf eine Methode gleichen Namens verweist. Diese Methode muss zus�tzlich zum passenden Namen auch die Annotation @Provide und den R�ckgabetyp Arbitrary<TypeToGenerate> besitzen. 
   
## Filter, Map and Combine
   
    Arbitrary ist dabei der Kerntyp hinter der Generierung von Werten; konkrete Arbitrary-Instanzen entstehen jedoch fast ausschlie�lich durch das Kombinieren anderer Arbitraries. So erzeugen wir in Listing 5 eine Instanz vom Typ Arbitrary<Person> durch die Kombination von 3 anderen Arbitraries.
   
    Listing 5: Kombinieren von Arbitraries
     @Property
     boolean anyValidPersonHasAFullName(@ForAll("validPerson") Person aPerson) {
     	return aPerson.fullName().length() >= 5;
     }

     @Provide
     Arbitrary<Person> validPerson() {
     	Arbitrary<Character> initialChar = Arbitraries.chars('A', 'Z');
     	Arbitrary<String> firstName = Arbitraries.strings('a', 'z', 2, 10);
     	Arbitrary<String> lastName = Arbitraries.strings('a', 'z', 2, 20);
     	return Combinators.combine(initialChar, firstName, lastName)
     		.as((initial, first, last) -> new Person(initial + first, last));
     }
     
    Ende Listing 5

   Es existieren zahlreiche M�glichkeiten, Arbitraries zu neuen Arbitraries zu kombinieren. jqwik erlaubt Filtern, Mappen, Kombinieren und einiges mehr. Wenn man unbedingt m�chte, kann man sogar seine eigenen Arbitrary-Klassen programmieren, sowie f�r eigene Typen Default-Arbitraries registrieren, die dann bei der Generierung von Property-Method-Parametern automatisch ber�cksichtigt werden.

## Fighting Indeterminism

   Die w�hrend des Testlaufs generierten Werte sind zu einem wesentlichen Teil zuf�llig generiert. Damit m�chte man die Chance erh�hen, auf Fehler und Auslassungen zu sto�en, an die bei Konzeption und Implementierung niemand gedacht hat. Der Nachteil dieses Ansatzes ist, dass eine fehlschlagende Property bereits im n�chsten Versuch wieder erfolgreich sein k�nnte, da zuf�llig anders generierte Parameter auch zu anderen Testergebnissen f�hren k�nnten. jqwik unternimmt zwei Dinge, um dieses Problem m�glichst klein zu halten:
* L�sst man seine Tests aus einer IDE oder in der Kommandozeile ausf�hren, dann merkt sich jqwik f�r jede fehlschlagende Property den sogenannten Seed, eine Zahl, die dem Zufallsgenerator als Startwert f�r seine pseudozuf�llig erzeugten Werte dient. Und so lange, wie diese Property fehlschl�gt, wird immer wieder der gleiche Seed verwendet und damit die gleichen Werte generiert.
* M�chte man eine bestimmte Folge von generierten Werten festschreiben, dann kann man den Seed, der f�r jede Property im Testprotokoll auftaucht, fest eintragen, n�mlich so:
     	@Property(seed = 424242l, reporting = ReportingMode.GENERATED)
     	void alwaysTheSameValues(@ForAll int aNumber) { � }

## Other PBT Frameworks and Libs for Java

Mit jqwik existiert eine Test-Engine, die sich nahtlos in die JUnit-5-Plattform integriert und dadurch automatisch von aktuellen IDEs und Build-Werkzeugen unterst�tzt wird. Wer (noch) nicht auf JUnit 5 setzt, der hat auch Alternativen zur Auswahl, u.a. QuickTheories [8] und junit-quickcheck [9].

## Next Step

[5] jqwik-User-Guide. http://jqwik.net/user-guide.html#how-to-use 
[8] QuickTheories. https://github.com/ncredinburgh/QuickTheories 
[9] junit-quickcheck. http://pholser.github.io/junit-quickcheck 
