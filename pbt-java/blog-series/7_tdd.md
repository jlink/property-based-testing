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
@Example
void factorizing_a_prime_returns_list_with_just_the_prime(@ForAll("primes") int prime) {
    List<Integer> factors = Primes.factorize(prime);
    Assertions.assertThat(factors).containsExactly(prime);
}
```
 
