# Property-based Testing in Java - Jqwik

In [the previous episode]({% post_url 2018-03-26-from-examples-to-properties %}) 
you've already seen [jqwik](http://jqwik.net) in action. 
One of the interesting aspects of this PBT library is the fact that it's not 
a standalone framework but that it hooks into JUnit 5 in order to "inherit"
IDE and built-tool support.

## Jqwik and the JUnit Platform

The fifth generation of JUnit does not only come with a modernized approach to write
and execute tests, but it is based on the idea of providing a platform for a large spectrum
of different _test engines_. An engine provides two entry points: One entry point is for
discovering tests and test suites - e.g. through scanning parts of the classpath for methods with
a certain annotation. The other entry point is used to run tests and test suites, usually
the ones you have discovered during the discovery step. Most everything else, like
filtering or selecting subsets of tests, will be performed by the platform itself.

The big advantage of such an approach is that any IDE and any
build tool only has to integrate the platform and not the individual engines. It's also
a big plus for engine developers who don't have to bother with aspects like
public APIs for discovering and running their test specifications.

Moreover, the platform allows to have any number of engines in parallel. That's how
JUnit 5 provides full backwards-compatibility to JUnit 4 and how a smooth migration
path can be realized. Using JUnit 4 (called Vintage), JUnit 5 (called Jupiter) and
_jqwik_ in a single project is not only possible, it's really simple.

IntelliJ has been an early platform adopter for over a year now. As of March 2018,
we also see native support from Eclipse, Gradle and Maven-Surefire. If you're already
using JUnit 5, using _jqwik_ as additional engine requires a single additional dependency.
If _jqwik_ is your first contact with the platform you should check out 
[this part in jqwik's user guide](http://jqwik.net/user-guide.html#how-to-use).


## Wildcards and Type Parameters

Let's get back to the concrete property test of the previous episode:

```java
@Property(reporting = Reporting.GENERATED)
boolean reverseWithWildcardType(@ForAll List<?> original) {
    return reverse(reverse(original)).equals(original);
}
```

You might miss [the tiny change]({% post_url 2018-03-26-from-examples-to-properties %#lets-do-it-in-java): 
Instead of a concretely typed `List<Integer>` 
I used the _wildcard_ variant: `List<?>`. Actually, this reflects the precondition better,
since the method under test - `Collections.reverse()` - should work with any element type.
Under the hood _jqwik_ will create instances of an anonymous subtype of `Object`. 
Just run the property with reporting switched on.

This would, by the way, also work with a _type variable_ instead of a wildcard. 
Upper or lower bounds, however, are not supported yet.

## Many Parameters

You might have guessed that parameter generation is not restricted to a single one
but works for as many as you need:

```java
@Property(reporting = ReportingMode.GENERATED)
boolean joiningTwoLists( 
    @ForAll List<String> list1, 
    @ForAll List<String> list2
) {
    List<String> joinedList = new ArrayList<>(list1);
    joinedList.addAll(list2);
    return joinedList.size() == list1.size() + list2.size();
}
```
   
If you look at the generated lists you will notice that the variance in list size
and string length is quite high. You can also see that now and then an empty list
and an empty string will be generated. This is due to the fact that value generation
is not purely random and not equally distributed across the allowed domain. 
Instead, _jqwik_ tries to be "smarter":

- Smaller values (for numbers, sizes and lengths) 
  are generated more frequently than higher values. The exact distribution depends
  on an internal `genSize` parameter which by default is set to the number
  of tries. The more often a property is tried, the larger the generated values are - on average.

- _jqwik_ will also routinely inject typical border cases like empty lists, empty strings,
  maximum and minimum values and others depending on the type of the values to generate
  and additional constraints.

This _smart generation approach_ aims at raising the probability to detect not so obvious
specification gaps and implementation bugs.

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
