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
My personal list is certainly incomplete, and apart from the first one
I've stolen all names elsewhere:

- __Business Rule as Property__ (aka _Obvious Property_)

  Sometimes the domain specification itself can be rather easily interpreted
  and written as a property.

- __Fuzzying__

  Code should never explode, even if you feed it with lots of diverse and unusual
  input data.

- __Inverse Functions__

  If a function has an inverse function, then applying function first
  and inverse second should return the original input.

- __Idempotent Functions__

  The multiple application of an idempotent function should not change results.

- __Invariant Functions__

  Some properties of our code do not change after applying our logic.

- __Commutativity__

  If a set of functions is commutative, change of order in applying the functions
  should not change the final result. Think of _sorting then filtering_ should
  have the same effect as _filtering then sorting_.

- __Test Oracle__

  Sometimes we know of an alternative implementation for our function or algorithm
  which is called a _test oracle_.
  In this case any result of using the function should be the same for both
  original and alternative. There are a few sources where the alternatives
  can come from:

  - Simple and slow versus complicated but fast
  - Parallel versus single-threaded
  - Self-made versus commercial
  - Old (pre-refactoring) versus new (post-refactoring)

- __Hard to compute, easy to verify__ (aka _Black-box Testing_)

  Some logic is hard to execute but easy to check. Consider e.g.
  the effort for _finding prime numbers_ versus _checking a prime number_.

- __Stateful Testing__

  Especially in the OO world, an object's behaviour can often (partially)
  be described as a _state machine_ with a finite set of states and actions
  to move from state to state. Exploring the space of transitions is
  an important use case for PBT, that's why I will dedicate a later article
  to it.

Let's dive into more details and a bit of code for some of the patterns...

## Pattern: Business Rule as Property

Consider a business rule coming out of a feature specification:

> For all customers with last year's sales volume above X EUR
> we will provide an additional discount of Y percent, if the invoice's
> total amount is equal or above Z EUR.

You could, for example, split the discount rule into two properties:

```java
@Property
@Label("High volume customer discount is 5% for total above 500 EUR")
boolean highVolumeCustomerDiscount(@ForAll Euro lastYearVolume, @ForAll Euro invoiceTotal) {
    Assume.that(lastYearVolume.compareTo(Euro.amount(5000)) > 0);
    Assume.that(invoiceTotal.compareTo(Euro.amount(500)) >= 0);

    return new DiscountCalculator().discountInPercent(lastYearVolume, invoiceTotal)
        .equals(new Percent(5));
}

@Property
@Label("No discount for low volume customer or total below 500 EUR")
boolean noDiscount(@ForAll Euro lastYearVolume, @ForAll Euro invoiceTotal) {
    Assume.that(
        lastYearVolume.compareTo(Euro.amount(5000)) <= 0 ||
        invoiceTotal.compareTo(Euro.amount(500)) < 0
    );

    return new DiscountCalculator().discountInPercent(lastYearVolume, invoiceTotal)
        .equals(new Percent(0));
}
```

These properties are on a micro testing level; the customer itself with
their transactions of last year and their current order does not show up
at all. Instead we check the property against an exposed discount calculating
function which only takes the necessary values as input.

You might also consider to verify the business rule with similar properties
using _integrated testing_ filling a real user database, creating transaction histories
and an invoice referring to an order for a set of items in your inventory store.
Keep in mind, however,
that _integrated property testing_ has the same drawbacks as _integrated
example testing_: High setup costs, slower execution, increased non-determinism
and low precision. In addition, you have to multiply the individual runtime
by the number of checks: If a single integrated check takes one second,
1000 runs of this check will take more than 15 minutes.


