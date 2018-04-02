# Property-based Testing in Java - The Importance of Being Shrunk

One problem that comes with random generation is the very loose relation between
the randomly chosen falsifying example and the problem underlying the failing
property. A simple example illustrates the problem:

```java
@Property(shrinking = ShrinkingMode.OFF)
boolean rootOfSquareShouldBeOriginalValue(@Positive @ForAll int anInt) {
    int square = anInt * anInt;
    return Math.sqrt(square) == anInt;
}
```

The property states the trivial mathematical concept that the square root of a
squared value should be equal to the originial value.
We have switched off "shrinking" using
the `shrinking` annotation attribute.
Running this property will fail with the following message (extract):

```
originalSample = [1207764160],
sample = [1207764160]

org.opentest4j.AssertionFailedError:
    Property [rootOfSquareShouldBeOriginalValue] falsified with sample [1207764160]
```

The sample found is just random. Unless you already have an inkling of what
the issue might be, the number itself does not give you an additional hint.
Even the fact that it's rather large might be a coincidence. At this point
you will either add additional logging, introduce assertions or even
start up the debugger to get more information about the problem at hand.

Let's take a different route, switch shrinking on (`ShrinkingMode.FULL`)
and rerun the property:


Betrachten wir das folgende Beispiel:

```
originalSample = [1207764160],
sample = [46341]

org.opentest4j.AssertionFailedError:
    Property [rootOfSquareShouldBeOriginalValue] falsified with sample [46341]
```

The number `46341` is much smaller and it is different from the _original
sample_. What _jqwik_ did after finding the failing data point `1207764160`
is trying to come up with simple or even "the simplest" example that
fails for (hopefully) the same reason. This searching phase is called
_shrinking_ because it starts with the original sample and tries to
make it smaller and check the property again.

In the case of integral numbers shrinking is rather simple because _smaller_
is well defined. If you want to see which steps _jqwik_ tries inbetween
the original and the final sample, you can switch on reporting of
falsified values:

```
@Poperty(reporting = Reporting.FALSIFIED, shrinking = ShrinkingMode.FULL)
```

Looking at the reporting output will reveal that shrinking starts with
big steps (halving the number) and in the end by decreasing the number
with step size 1. In this example 46341 is indeed the smallest integer
number that falsified the property. And what's the special thing about it?
As you might have guessed the square of 46341 - 2147488281 - is larger
than `Integer.MAX_VALUE` and will thus lead to integer overflow.
Conclusion: The property above only holds for int-type numbers up to 46340.

## The Simplest Falsifying Sample

So we might agree that this time _jqwik_ was indeed able to find the simplest
example that produces the failure. In general, however, this is not always
the case. Why's that? A few reasons:

- It's not always easy to agree on what "simplest" means in a given context.
  Would you consider +5 to be simpler than -5? Maybe yes. +2 to be simpler than -1? Hmm...

  As soon as you get in the realm of combined arbitraries and more than
  one parameter there can be several shrinking targets that could all be
  considered to be "the simplest". There might even be an innumerous number of them.
  Further down I'll show an example with more than one potential best shrunk value.

- Even if we can agree on a common metric for "the simplest" shrinking
  might not find it. From a computational point of view, shrinking is a
  search problem with an almost infinite search space. That's why
  _jqwik_ - or any PBT library - has to use heuristics in order to prune the
  search space as much as possible. The heuristics of one tool might
  be superior in one scenario but fail to produce any reasonable simplification
  in another.

- For shrinking to work at all, deterministic property checking is crucial.
  As long as a property can be considered to be (practically) a pure
  function - i.e. only the generated values influence checking results - everything
  is fine. But as soon as you have indeterministic effects in falsification
  itself the whole approach breaks down. That's why concurrent property
  testing and shrinking often don't go together.

My recommendation: Don't expect any library to always find the simplest
falsifying sample for you. Instead, develop an intuition for what the
library can reasonable do in terms of simplifying randomly found samples.
And sometimes, simplifying the parameters you use in the property,
will make shrinking easier.

## Container Types




```java
static <E> List<E> brokenReverse(List<E> aList) {
    if (aList.size() < 4) {
        aList = new ArrayList<>(aList);
            reverse(aList);
        }
        return aList;
    }
 
@Property(shrinking = ShrinkingMode.OFF)
boolean reverseShouldSwapFirstAndLast(@ForAll List<Integer> aList) {
    Assume.that(!aList.isEmpty());
    List<Integer> reversed = brokenReverse(aList);
    return aList.get(0) == reversed.get(aList.size() - 1);
}
```   
   
   In diesem Beispiel testen wir eine kaputte Implementierung brokenReverse. 
   Ein erster Testlauf liefert ein Ergebnis, das in etwa so aussieht:
     org.opentest4j.AssertionFailedError: 
     Property [reverseShouldSwapFirstAndLast] falsified with sample 
         [[0, 1, -1, -2147483648, 2147483647, -247, 247, 39, -31, 39, 477784874]]

Welche Eigenschaft dieser Liste ist f�r das Fehlverhalten verantwortlich? 
Sind es die negativen Zahlen? Ist es der doppelte Eintrag? Da hilft nur Debuggen, oder?

    Geben wir jqwik noch eine Chance und schalten diesmal das �Schrumpfen� ein, 
    indem wir ShrinkingMode.ON  verwenden � oder diesen Eintrag entfernen. 
    Dann sieht das Ergebnis ein wenig anders aus:
     org.opentest4j.AssertionFailedError: 
     Property [reverseShouldSwapFirstAndLast] falsified with sample 
         [[0, 0, 0, -1]]
         
   Das ist doch ein deutlich angenehmeres Beispiel, das uns fast den Implementierungsfehler 
   erraten l�sst: Es k�nnte mit der L�nge der Liste zu tun haben � und genau so ist es ja auch.
   
   Hinter den Kulissen hat jqwik die urspr�nglich fehlschlagende Beispiel-Liste genommen, 
   und diese �geschrumpft�, um ein m�glichst einfaches und aussagekr�ftiges Sample zu erhalten. 
   Mit dieser als Shrinking bezeichneten Funktionalit�t, sind die meisten Property-Testbibliotheken 
   ausgestattet.

## Type-Based versus Integrated Shrinking



   Typischerweise findet dieses Schrumpfen ausschlie�lich auf Basis des statischen Typs statt, 
   was jedoch zu dem Problem f�hren kann, dass ein geschrumpfter Wert sich au�erhalb des 
   spezifizierten Bereichs bewegen kann. 
   Aus diesem Grund setzt jqwik auf Integrated Shrinking [6], was aufw�ndiger zu implementieren ist, 
   daf�r aber garantiert, dass geschrumpfte Werte die gleichen Eigenschaften besitzen 
   wie generierte Werte.

[6] Integrated vs type based shrinking. http://hypothesis.works/articles/integrated-shrinking/
