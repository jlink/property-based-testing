# PBT and Test-driven Development

It's been a few months that I wrote about
[stateful testing]({% post_url 2018-09-06-stateful-testing %}).
Since then I've given a few workshops about property-based testing and
jqwik has reached [version 1.1.3](https://jqwik.net/release-notes.html#113).
So, I'm getting more and more committed to PBT but, to be frank, when developing
software for clients my main approach is still Test-Driven Development with
mostly example-based test cases.
Wouldn't it be nice if both my favourite topics could be merged in
some sort of grand unified development approach?
Turns out that they do go together quite well, at least sometimes.

## Prime Factorization - Property-Test-Driven

To get the discussion going I'll demonstrate one possible approach by tackling
a heavily used example: prime factorization. The goal of this 
[code kata](https://en.wikipedia.org/wiki/Kata_(programming))
is to compute the prime factors of a given natural number for natural numbers from 2 up.
I usually start all my TDD work with collecting test ideas in an "inbox". 
I'll then grab the ideas from this inbox one by one until it is either empty 
or none of the remaining test ideas seems to provide any further value.

In a conventional example-driven TDD session my test ideas often come in the form of
concrete examples. Having properties as an additional means of expression I tend to
mix concrete examples with properties in my initial inbox. As for the kata at hand
I came up with the following test ideas:

```text
factorize(2) -> [2] 
factorize(prime) -> [prime]
factorize(prime^2) -> [prime, prime] 
factorize(prime^n) -> [prime, ..., prime]
factorize(prime1 * prime2) -> [prime1, prime2]
factorize(prime1 * .... * primeN) -> [prime1, ..., primeN]
factorize(n < 2) -> IllegalArgumentException
factorize(2 <= number <= Integer.MAX_VALUE) -> no exception  
product of all returned numbers must be equal to input number
all numbers in produced list must be primes
```

The items did not come to my mind in this order but I tried to sort them by assumed
implementation complexity, appending a few generic properties at the end.


### Starting with an Example

More often than not I start with an easy example test, just to figure out the interface.
Luckily the first item in our inbox covers just that:

```java
class PrimeFactorizationTests {
	@Example
	void factorizing_2_returns_list_with_just_2() {
		List<Integer> factors = Primes.factorize(2);
		Assertions.assertThat(factors).containsExactly(2);
	}
}
```

and a trivial implementation:

```java
public class Primes {
	public static List<Integer> factorize(int number) {
		return Collections.singletonList(2);
	}
}
```

Et voilà, the first inbox item can be ticked off:

```text
✓ factorize(2) -> [2] 
factorize(prime) -> [prime]
factorize(prime^2) -> [prime, prime] 
factorize(prime^n) -> [prime, ..., prime]
factorize(prime1 * prime2) -> [prime1, prime2]
factorize(prime1 * .... * primeN) -> [prime1, ..., primeN]
factorize(n < 2) -> IllegalArgumentException
factorize(2 <= number <= Integer.MAX_VALUE) -> no exception  
product of all returned numbers must be equal to input number
all numbers in produced list must be primes
```

### A first Property

At this point a test-driven developer will often ask the question:
Is the current test enough to justify a more generic implementation of 
`factorize()` or do we need additional examples from which to _triangulate_.
With property-based testing in mind we have reason to hope that the generic
nature of properties will enforce generic implementation more or less
by itself. Let's see how that works out:

```java
@Property
void factorizing_a_prime_returns_list_with_just_the_prime(@ForAll("primes") int prime) {
    List<Integer> factors = Primes.factorize(prime);
    Assertions.assertThat(factors).containsExactly(prime);
}
```

Running this property will yield an exception:

```text
net.jqwik.api.CannotFindArbitraryException: 
    Cannot find an Arbitrary [primes] for Parameter of type 
        [@net.jqwik.api.ForAll(value=primes) int]
```

What the error message tells us is that we have to implement a provider method
for primes. We could now search our brain for algorithms to detect or 
generate prime numbers. We could also import a maths library. 
Or we can be pragmatic and just enumerate a few primes and let jqwik
choose among them:

```java
@Provide
Arbitrary<Integer> primes() {
    return Arbitraries.of(2, 3, 5, 7, 23, 101);
}
```

Trying just 6 different prime numbers might look to you like too much of a simplification, 
especially since one of the promises of PBT is to provide a wide coverage of the problem space.
However, the six examples are enough to drive generality into our implementation.
We can always add more thorough prime number generation later - and we will.

Rerunning the property will still fail, but now with a domain-specific error:

```
jaa.lang.AssertionError: 
    Expecting:
      <[2]>
    to contain exactly (and in same order):
      <[3]>
```

This can be fixed by a simple change:

```java
public static List<Integer> factorize(int number) {
    return Collections.singletonList(number);
}
```

Knowing that in the end we'll sometimes have to add more than a single factor
I start refactoring towards this capability:

```java
public static List<Integer> factorize(int number) {
    List<Integer> factors = new ArrayList<>();
    factors.add(number);
    return factors;
}
```

All tests and properties still succeed and the first property on our list is done:

```text
✓ factorize(2) -> [2] 
✓ factorize(prime) -> [prime]
factorize(prime^2) -> [prime, prime] 
factorize(prime^n) -> [prime, ..., prime]
factorize(prime1 * prime2) -> [prime1, prime2]
factorize(prime1 * .... * primeN) -> [prime1, ..., primeN]
factorize(n < 2) -> IllegalArgumentException
factorize(2 <= number <= Integer.MAX_VALUE) -> no exception  
product of all returned numbers must be equal to input number
all numbers in produced list must be primes
```

If we wanted we could now get rid of the initial example test since
it's fully covered by the property.


### From Example to Property - Again

Tackling the next property - squared primes - gives us the opportunity to
revisit a tactic that we've already seen earlier. Let's start with the
property test method:

```java
@Property
void factorizing_squared_prime_returns_prime_twice(@ForAll("primes") int prime) {
    List<Integer> factors = Primes.factorize(prime * prime);
    Assertions.assertThat(factors).containsExactly(prime, prime);
}
```

Run and see it fail as expected! When I tried to think of simple way to
make it work for all primes I couldn't. My trick to tackle this too big of a
step is to scale down from property to single example. One way to do that
without having to write yet another (example) test is to temporarily 
fix the incoming value of a property test:

```java
@Property
void factorizing_squared_prime_returns_prime_twice(@ForAll("primes") int prime) {
	prime = 2;
    List<Integer> factors = Primes.factorize(prime * prime);
    Assertions.assertThat(factors).containsExactly(prime, prime);
}
```

This will result in the same test failure but it can easily be repaired:

```java
public static List<Integer> factorize(int number) {
    List<Integer> factors = new ArrayList<>();
    if (number == 4) {
        factors.add(2);
        number /= 2;
    }
    factors.add(number);
    return factors;
}
```

Extracting `2` into a variable seems a natural refactoring in this case:

```java
public static List<Integer> factorize(int number) {
    List<Integer> factors = new ArrayList<>();
    int candidate = 2;
    if (number == candidate * candidate) {
        factors.add(candidate);
        number /= candidate;
    }
    factors.add(number);
    return factors;
}
```

And now the general property seems achievable:

```java
@Property
void factorizing_squared_prime_returns_prime_twice(@ForAll("primes") int prime) {
	prime = 2;
    List<Integer> factors = Primes.factorize(prime * prime);
    Assertions.assertThat(factors).containsExactly(prime, prime);
}
```

fails with message:

```text
java.lang.AssertionError: 
    Expecting:
      <[9]>
    to contain exactly (and in same order):
      <[3, 3]>
```

Fixing this is a small step now:

```java
public static List<Integer> factorize(int number) {
    List<Integer> factors = new ArrayList<>();
    int candidate = 2;
    while (number % candidate != 0) {
        candidate++;
    }
    if (number == candidate * candidate) {
        factors.add(candidate);
        number /= candidate;
    }
    factors.add(number);
    return factors;
}
```

and can further be simplified to:

```java
public static List<Integer> factorize(int number) {
    List<Integer> factors = new ArrayList<>();
    int candidate = 2;
    while (number % candidate != 0) {
        candidate++;
    }
    if (number == candidate * candidate) {
        factors.add(candidate);
    }
    factors.add(candidate);
    return factors;
}
```

The code's not pretty (yet); in TDD you have to be patient with your design.
At least one more item to check off:

```text
✓ factorize(2) -> [2] 
✓ factorize(prime) -> [prime]
✓ factorize(prime^2) -> [prime, prime] 
factorize(prime^n) -> [prime, ..., prime]
factorize(prime1 * prime2) -> [prime1, prime2]
factorize(prime1 * .... * primeN) -> [prime1, ..., primeN]
factorize(n < 2) -> IllegalArgumentException
factorize(2 <= number <= Integer.MAX_VALUE) -> no exception  
product of all returned numbers must be equal to input number
all numbers in produced list must be primes
```

