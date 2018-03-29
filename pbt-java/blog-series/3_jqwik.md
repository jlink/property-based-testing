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

Out of the box _jqwik_ is able to generate objects of the most common JDK types:

- All primitive numerical types, their boxed counter parts, as well as `BigInteger` and `BigDecimal`igDecimal
- `String`, `Character` and `char`
- `Boolean` and `boolean`
- All enum types
- `List<T>`, `Set<T>`, `Stream<T>` and `Optional<T>` as long as T can be generated
- Arrays of types that can be generated
- `java.util.Random` and `Object`

You might notice that `Map` and all calendar related classes are not covered (yet).
It's quite easy, though, to provide and register generators yourself. 

What's also not covered are functional types. They are on the backlog but it's not
easy to come up with a good variance of functions to try. Constant functions are obvious,
anything else not so much.

### Influencing Automatic Parameter Generation

The easiest way to influence and constrain the domain of values considered for generation
is to use additional annotations provided for many of the default types. Here are a few 
examples:

- All number types come with their respective range annotation. 
  E.g. use `@DoubleRange(min=5.0, max=10.0)` to only generate doubles between 5 and 10. 
- Strings can be constrained in both their length and the pool of character to be used.
  E.g. use `@StringLength(min = 1, max = 5) @AlphaChars` to generate strings of 1 to 6 characters
  with only upper and lower case letters.

Here is the [full list of built-in constraining annotations](http://jqwik.net/user-guide.html#constraining-default-generation).   

## Programmatic Generation

Sometimes we're dealing with classes that cannot be generated by default. 
Another time the domain-specific constraints of a primitive type is so specific
that annotations won't cut it. In that case you can delegate parameter
provision to another method in your test container class. 
The following example shows how to generate German zip codes: 

```java
@Property
void letsGenerateGermanZipCodes(@ForAll("germanZipCodes") String zipCode) {
}
 
@Provide
Arbitrary<String> germanZipCodes() {
    return Arbitraries.strings('0', '9', 5, 5);
}
```

The String value of the `@ForAll` annotation serves as a reference to a 
method within the same class (or one of its superclasses or owning classes).
This reference refers to either the method's name or the String value
of the method's `@Provide` annotation.
The providing method has to return an object of type `@Arbitrary<T>`
where `T` is the static type of the parameter to be provided. 

Parameter provision methods usually start with a static method call to `Arbitraries`, maybe followed
by one or more filtering, mapping or combining actions as described in the next section.

## Filter and Map
   
`Arbitrary` is the core type of all value generation. There are quite a few default methods that can be used to
generate values starting with one of the static basic generator functions in class `Arbitraries`. Most base
generators return a specific subtype of `Arbitrary` that gives you additional constraining possibilities.

Let's say we want to generate integers _between 1 and 300 that are multiples of 6_. Here are two
alternatives to do that:

- `Arbitraries.integers().between(1, 300).filter(anInt -> anInt % 6 == 0)`
- `Arbitraries.integers().between(1, 50).map(anInt -> anInt * 6)`

Which way is better? Sometimes it's only a matter of style or readability. Sometimes, however,
the way you choose can influence performance. When comparing the two options above, the former 
is close to the given spec but it will - through filtering - through away five sixth of all 
generated values. The latter is therefore more efficient but also less comprehensible when
coming from the spec. Usually generating primitive values is so fast that readability trumps
efficiency.


## Combining Arbitraries

Real domain objects often have several distinct and mostly unrelated parts. 
That's why when you generate them you want to start from unrelated primitive generators.

Given our domain class `Person`:

```java
class Person {
    private final String firstName, lastName;

    Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("Person(%s:%s)", firstName, lastName);
    }
}
``` 

We want to write a property which checks that any person has a full name. 
The person generator method combines three arbitraries into the needed one:

```java
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
```

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
