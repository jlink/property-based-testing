# Property-based Testing in Java - From Example Tests to Properties

Test-driven development in OOPLs is mostly focused on example-based test cases aka as "plain old unit tests".
Let's say we want to check if the common JDK function `java.util.Collections.reverse()` works as expected.
We use [a simple JUnit Jupiter test](http://junit.org/junit5/docs/current/user-guide/) for that purpose:

```java
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ListReverseTests {
    @Test
    void reverseList() {
        List<Integer> aList = Arrays.asList(1, 2, 3);
        Collections.reverse(aList);
        Assertions.assertThat(aList).containsExactly(3, 2, 1);
    }
}
```   

Many, hopefully most, developers have written similar tests for years or even decades. 
Usually with good success and a reasonable capability to detect common programming errors.
There is one thought, though, that's always been nagging in the back of my mind:
How can I be confident that `reverse` also works with 5 elements? With 5000? With an empty list?
With elements of different types? The amount of doubts can go as high as I allow it.

One way to fight this type of uncertainty is to add more examples and test cases. 
I am calmed down by the hope that my choice of examples is sufficiently representative to
catch bugs now and regressions in the future. When in doubt I add another test case - and yet another.
Model-based testing approaches (e.g. equivalence classes and parameter combinatorics)
address this exact problem but usually err on the side of too many tests. 
And every single test that does not reveal an error now or in the future means a waste of resources
and additional maintenance effort.

## Properties

We can approach the question of correctness from a different angle: 
> Under what preconditions and constraints (e.g. the range of input parameters)
> should the functionality under test lead to which postconditions (results of a computation)?
> And which invariants should never be violated in the course?

This combination of preconditions and qualities that are expected to be present 
is also called a _property_.

Let's formulate a property for the `reverse` function in plain English:
> For any given list of elements, applying `reverse` twice should result in the original list.
   
If we can now translate this prose into a computer-executable form, e.g. programming language code,
and also let the computer generate a wide range of examples that conform to the preconditions,
then we have arrived at _Property-based Testing_.

## QuickCheck

The mother of all modern PBT frameworks is 
[QuickCheck for Haskell](https://hackage.haskell.org/package/QuickCheck).
The property from above could be translated into QuickCheck like that:

```haskell
prop_reversed :: [Int] -> Bool 
prop_reversed xs =             
  reverse (reverse xs) == xs 
```

As a functional language with a strong type system Haskell allows this very concise property specification:
- Line 1 gives us type information and thereby the property's precondition: 
  A list of `Int` as input parameter and a boolean value to represent the property's check result.
  The fact that we only consider lists of integral numbers is a concession to how QuickCheck works internally.
  From the point of view of the abstract property the concrete type is not of interest.
- Lines 2 and 3 tell the language interpreter how to evaluate the property: Apply `reverse` twice to
  input list `xs` and compare the result with the original list.

At test runtime QuickCheck will generate a number (usually 100) of (mostly) random lists
and call `prop_reversed`. If only a single call returns `False` the property is considered to be
falsified and the test run as failed. Thus it's important to recognize that

> PBT cannot prove that a property is correct but only try to find examples to refute it!

What you can also notice is that this property _is required but not sufficient_.
A trivial implementation of `reverse` which returned the input list as output list would
also succeed this property check. This raises an important question which will be
tackled later in this series: Can property-based tests replace our good old examples tests
or are they "just a complement"?

## Let's do it in Java

Translating the above QuickCheck function into Java with just JUnit is not simple.
That's why we pull in another test engine: [jqwik](http://jqwik.net). 
We will have a closer look at _jqwik_ in the next article. 
For a start all we do is translate the Haskell from above into a jqwik test method:

```java
import java.util.*;
import net.jqwik.api.*;

class ListReverseProperties {

	@Property
	boolean reverseTwiceIsOriginal(@ForAll List<Integer> original) {
		return reverse(reverse(original)).equals(original);
	}

	private <T> List<T> reverse(List<T> original) {
		List<T> clone = new ArrayList<>(original);
		Collections.reverse(clone);
		return clone;
	}
}
```

Let's have a closer look at the code:
- We mimic Haskell's `reverse` behaviour by writing our own `reverse` method
  that clones a list before it reverts it.
- A property is a method within a container class. 
  The method should have a telling name: "reverseTwiceIsOriginal" is a reasonable summary
  of the property's intent.
- In order to mark a method as a property method it must be annotated with `@Property`
  so that IDEs and build tools will recognize it as such. At least if they support the JUnit platform.
- Adding parameters and annotating them with `@ForAll` tells _jqwik_ that you
  want the framework to generate instances for you. A parameter's type - `List<Integer>` -
  is considered to be the fundamental precondition.
- Returning a `boolean` value is the simplest form of communicating the result of checking.
  Alternatively you can use any assertion library like 
  [AssertJ](https://joel-costigliola.github.io/assertj/) or JUnit Jupiter itself. 

Running a successful jqwik property is as quiet as running a successful JUnit test.
If not instructed otherwise, jqwik will invoke each property method 1000 times with different
input parameters. As long as you write [microtests](https://www.industriallogic.com/blog/history-microtests/)
running a test so often is not a problem. If needed you can tune the number as high or as low as you need.

In order to see a falsified property, we remove one of the calls to `reverse`:
```java
@Property
boolean reverseTwiceIsOriginal(@ForAll List<Integer> original) {
    return reverse(original).equals(original);
}
```
and look at the test output:   

```
timestamp = 2018-01-18T11:57:17.027, 
tries = 2, 
checks = 2, 
seed = -6748802811761023649, 
originalSample = [[0, 1, -1, -2147483648, �]], 
sample = [[0, -1]]
org.opentest4j.AssertionFailedError: 
    Property [reverseTwiceIsOriginal] falsified with sample [[0, -1]]
```

Quite a bunch of information: You can see the number of test `tries`, 
the number of actually run `checks`, the random `seed`, the originally falsified sample,
and the simplest found falsified `sample`.

## Further Questions

In this article, I started with a simple example. Hopefully enough, to get you
thinking and raising questions, e.g.:

- Property-based Testing's promise is to relieve us from (some of) the burden of finding
  additional examples, corner cases and unknown gaps in the specification. 
  To what degree can PBT keep this promise?
- Another thing that might bother you is the flair of indeterminism that random
  generation brings into the game. Should you be bothered? Do the tools
  provide means to repeat falsifying test runs?
- Is there a fundamental reason why PBT is common in the world of functional
  programming but almost unknown in OOP? Maybe it doesn't really work with
  stateful objects and side-effects?
  

## Next Step

In the following article we will look closer at _jqwik_ and its features, but also
at alternatives for the JVM. 
