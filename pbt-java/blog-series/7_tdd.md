# PBT and Test-driven Development

It's been a few months that I wrote about
[stateful testing]({% post_url 2018-09-06-stateful-testing %}).
Since then I've given a couple of workshops about property-based testing and
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
    if (number > candidate) {
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

### Generalizing an Existing Property

The next point on our list is nothing more than a generalized version of
the previous one. Thus, we first introduce the new parameter and rename
the property appropriately:

```java
@Property
void factorizing_prime_raised_to_n_returns_n_times_prime(
        @ForAll("primes") int prime,
        @ForAll @IntRange(min = 1, max = 2) int n
) {
    List<Integer> factors = Primes.factorize((int) Math.pow(prime, n));
    Assertions.assertThat(factors).containsOnly(prime);
    Assertions.assertThat(factors).hasSize(n);
}
```

This works out of the box, so we can increase the upper limit of `n`:

```java
@Property
void factorizing_prime_raised_to_n_returns_n_times_prime(
        @ForAll("primes") int prime,
        @ForAll @IntRange(min = 1, max = 5) int n
) { ... }
```

This will fail with a sample of `[2, 3]`. But again, it's a rather
smallish change that can make all of our tests succeed:

```java
public static List<Integer> factorize(int number) {
    List<Integer> factors = new ArrayList<>();
    int candidate = 2;
    while (number % candidate != 0) {
        candidate++;
    }
    while (number >= candidate) {
        factors.add(candidate);
        number /= candidate;
    }
    return factors;
}
```

Well, at least I thought it would work. In reality I got the following
error message:

```text
sample = [101, 5]

java.lang.AssertionError: 
    Expecting:
      <[2147483647]>
    to contain only:
      <[101]>
```

What's happening here is that 101^5 is bigger than the maximum representable
value of type `int`. Java's policy to just overflow and be quiet makes that
not obvious. What we have to do is make sure that the number we try will
_not_ overflow. To reach that goal we have several choices:

- Generate only primes that are below `Integer.MAX_VALUE` when raised 
  to the power of 5 
- Reduce the max value of n to 4
- Filter out those combinations of `prime` and `n` that exceed `Integer.MAX_VALUE`

I choose to take the latter option here because it results in more different
tests. And exploring the possible range of values to its limits is one of
the promised virtues of PBT. Since the filter criteria covers more than
one parameter we have to use assumptions:

```java
@Property
void factorizing_prime_raised_to_n_returns_n_times_prime(
        @ForAll("primes") int prime,
        @ForAll @IntRange(min = 1, max = 5) int n
) {
    BigInteger numberToFactorize = BigInteger.valueOf(prime).pow(n);
    Assume.that(numberToFactorize.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0);
    List<Integer> factors = Primes.factorize(numberToFactorize.intValueExact());
    Assertions.assertThat(factors).containsOnly(prime);
    Assertions.assertThat(factors).hasSize(n);
}
```

Now the property will succeed and it's interesting to look at the report:

```
PrimeFactorizationTests:factorizing prime raised to n returns n times prime = 
                              |-----------------------jqwik-----------------------
tries = 30                    | # of calls to property
checks = 29                   | # of not rejected calls
generation-mode = EXHAUSTIVE  | parameters are exhaustively generated
after-failure = SAMPLE_FIRST  | try previously failed sample, then previous seed
seed = 1584749916605677180    | random seed to reproduce generated values
```

The values of `tries` and `checks` reveals two things: Only a single
combination was filtered out. And only 30 sets of input values were created
at all! Why not 1000, which is the default number of tries? Well,
in cases where _jqwik_ can figure out that the number of all possible combinations
of values is lower than the number of tries it will just generate all
possibilities. And since we only provided 6 different primes to choose from
6 * 5 is 30. And sure enough, one more item can be marked as "done":

```text
✓ factorize(2) -> [2] 
✓ factorize(prime) -> [prime]
✓ factorize(prime^2) -> [prime, prime] 
✓ factorize(prime^n) -> [prime, ..., prime]
factorize(prime1 * prime2) -> [prime1, prime2]
factorize(prime1 * .... * primeN) -> [prime1, ..., primeN]
factorize(n < 2) -> IllegalArgumentException
factorize(2 <= number <= Integer.MAX_VALUE) -> no exception  
product of all returned numbers must be equal to input number
all numbers in produced list must be primes
```

### Adding the final Functional Requirements

From a functional perspective all that's missing is factorization of numbers
with mixed prime factors. The next property - 
`factorize(prime1 * prime2) -> [prime1, prime2]` - would be another stepping
stone. Since I can almost imagine the working implementation for the general case
I feel comfortable enough to skip this step and go the full mile:

```text
factorize(prime1 * .... * primeN) -> [prime1, ..., primeN]
```

Knowing from experience that integer overflow might strike here, I build in
the guarding assumption from the start:

```java
@Property
void factorizing_product_of_list_of_primes_will_return_original_list(
        @ForAll("listOfPrimes") List<Integer> primes
) {
    BigInteger product =
            primes.stream()
                  .map(BigInteger::valueOf)
                  .reduce(BigInteger.ONE, BigInteger::multiply);
    Assume.that(product.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0);

    List<Integer> factors = Primes.factorize(product.intValueExact());
    Assertions.assertThat(factors).isEqualTo(primes);
}

@Provide
Arbitrary<List<Integer>> listOfPrimes() {
    return primes().list().ofMinSize(1).ofMaxSize(5);
}
```

As expected running the property reveals the simplest example that fails:

```
org.opentest4j.AssertionFailedError: 
    Expecting:
     <[2, 2]>
    to be equal to:
     <[2, 3]>
    but was not.
```

Sure enough, all I have to do is pulling in the first loop into the second:

```java
public static List<Integer> factorize(int number) {
    List<Integer> factors = new ArrayList<>();
    int candidate = 2;
    while (number >= candidate) {
        while (number % candidate != 0) {
            candidate++;
        }
        factors.add(candidate);
        number /= candidate;
    }
    return factors;
}
```

I was too bold, though: 

```
org.opentest4j.AssertionFailedError: 
    Expecting:
     <[2, 7]>
    to be equal to:
     <[7, 2]>
    but was not.
```

The problem is the order of factors. Since they should - and are indeed -
returned in ascending order the original list of primes must also be in
ascending order to match the result. We could do the sorting in the provider
method, but the property itself seems to be the better place:

```java
@Property
void factorizing_product_of_list_of_primes_will_return_original_list(
        @ForAll("listOfPrimes") List<Integer> primes
) {
    primes.sort(Integer::compareTo);
    BigInteger product = ... ;
    Assume.that(product.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0);

    List<Integer> factors = Primes.factorize(product.intValueExact());
    Assertions.assertThat(factors).isEqualTo(primes);
}
```

And indeed, all tests succeed now and we can happily revisit our inbox:

```text
✓ factorize(2) -> [2] 
✓ factorize(prime) -> [prime]
✓ factorize(prime^2) -> [prime, prime] 
✓ factorize(prime^n) -> [prime, ..., prime]
✓ factorize(prime1 * .... * primeN) -> [prime1, ..., primeN]
factorize(n < 2) -> IllegalArgumentException
factorize(2 <= number <= Integer.MAX_VALUE) -> no exception  
product of all returned numbers must be equal to input number
all numbers in produced list must be primes
```

### Error Cases

Testing illegal input and rejecting it is easy in our case. That's why
I'll make it short and just show the property and the implementation
that fulfills it:

```java
@Property
void numbers_below_2_are_illegal(
        @ForAll @IntRange(min = Integer.MIN_VALUE, max = 1) int number
) {
    Assertions.assertThatThrownBy(() -> {
        Primes.factorize(number);
    }).isInstanceOf(IllegalArgumentException.class);
}
```

```java
public static List<Integer> factorize(int number) {
    if (number < 2) {
        throw new IllegalArgumentException();
    }
    ...
}
```

So here's the remaining inbox:

```text
✓ factorize(2) -> [2] 
✓ factorize(prime) -> [prime]
✓ factorize(prime^2) -> [prime, prime] 
✓ factorize(prime^n) -> [prime, ..., prime]
✓ factorize(prime1 * .... * primeN) -> [prime1, ..., primeN]
✓ factorize(n < 2) -> IllegalArgumentException
factorize(2 <= number <= Integer.MAX_VALUE) -> no exception  
product of all returned numbers must be equal to input number
all numbers in produced list must be primes
```


### Putting the Implementation under Pressure

Up to now we've been quite careful with our choice of primes and the number
of primes the product of which we factorize in the tests. Let's first enhance
the list of primes to consider to the first 26:

```java
@Provide
Arbitrary<Integer> primes() {
    return Arbitraries.of(
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47,
            53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101
    );
} 
```

No problems with that. As a next step, we can raise the maximum size
of the list of primes to 20:

```java
@Provide
Arbitrary<List<Integer>> listOfPrimes() {
    return primes().list().ofMinSize(1).ofMaxSize(20);
}
```

Still no problem. Checking the _jqwik_ report, however, reveals that now more than
two thirds of generated examples will be thrown away due to the assumption
in the property:

```text
PrimeFactorizationTests:factorizing product of list of primes will return original list = 
                              |-----------------------jqwik-----------------------
tries = 1000                  | # of calls to property
checks = 301                  | # of not rejected calls
``` 

As a next step I'll take the next two items from our inbox,
combine them into one property method and use it to explore how big numbers
are handled by out factorization algorithm:

```java
@Property
void all_numbers_above_1_can_be_factorized(
        @ForAll @IntRange(min = 2, max = 10000) int number
) {
    List<Integer> factors = Primes.factorize(number);
    Integer product = factors.stream().reduce(1, (a, b) -> a * b);
    Assertions.assertThat(product).isEqualTo(number);
}
```

On my machine `max = 100_000_000` is still handled fine within a few seconds.
Turning it up to `1_000_000_000` got the runtime above my patience limit for
microtests. Maybe we can optimize the algorithm a little bit? Next try:

```java
public static List<Integer> factorize(int number) {
    if (number < 2) {
        throw new IllegalArgumentException();
    }
    List<Integer> factors = new ArrayList<>();
    int candidate = 2;
    while (number >= candidate) {
        while (number % candidate != 0) {
            if (candidate * candidate > number) {
                candidate = number;
            } else {
                candidate++;
            }
        }
        factors.add(candidate);
        number /= candidate;
    }
    return factors;
}
```

This will get us to `max = Integer.MAX_VALUE - 1` but `max = Integer.MAX_VALUE`
does not finish. Again, we're running into an overflow in the condition:

```java
if (candidate * candidate > number) { ... }
```

Using the square root instead of the square gets rid of this problem. So here's
the final version of our prime factorization algorithm, which works for all 
numbers between 2 and `Integer.MAX_VALUE`. At least _jqwik_ has not found
a counter example yet:

```java
public static List<Integer> factorize(int number) {
    if (number < 2) {
        throw new IllegalArgumentException();
    }
    List<Integer> factors = new ArrayList<>();
    int candidate = 2;
    while (number >= candidate) {
        while (number % candidate != 0) {
            if (Math.sqrt(number) < candidate) {
                candidate = number;
            } else {
                candidate++;
            }
        }
        factors.add(candidate);
        number /= candidate;
    }
    return factors;
}
```

If you really really want to know if it works for _all_ valid numbers
you can configure the property method like that:

```java
@Property(generation = GenerationMode.EXHAUSTIVE)
void all_numbers_above_1_can_be_factorized(
        @ForAll @IntRange(min = 2) int number
) { ... }
```

I pretty sure this would run a few hours on my machine though...