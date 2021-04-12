# Budget and Bills

Tackling the problem specified by Hillel Wayne in https://gist.github.com/hwayne/e5a65b48ab50a2285de47cfc11fc955f
with a combination of example tests and property-based testing.


## Step 1 - Use Example to Discover API

Using an initial example to create the initial API:

```java
@Example
void cannot_afford_zero_budget() {
  Budget zeroBudget = Budget.withTotalLimit(0);
  Bill billNotZero = Bill.of(Item.withCost(1));

  assertThat(zeroBudget.canAfford(billNotZero)).isFalse();
}
```

An example gets me faster to the point with at least the basic types and methods in place.
If I used a property here I would have to create a generator just as a distraction.


## Step 2 - Transform Example into More Generic Property

Converting the initial example into a property:

```java
@Property
void cannot_afford_zero_budget(@ForAll("bills") Bill billWithItems) {
  Budget zeroBudget = Budget.withTotalLimit(0);
  assertThat(zeroBudget.canAfford(billWithItems)).isFalse();
}

@Provide
Arbitrary<Bill> bills() {
  return Arbitraries.just(Bill.of(Item.withCost(1)));
}
```

Initially the bill generator is trivial and will always create the same bill.
I'll now start to refactor it into something more useful:

```java
@Provide
Arbitrary<Bill> bills() {
  Arbitrary<Item[]> items = items().array(Item[].class).ofMinSize(1);
  return items.map(Bill::of);
}

Arbitrary<Item> items() {
  return Arbitraries.integers().between(1, 1000).map(Item::withCost);
}
```

While working on these generators two so far unresolved questions came up:
- Can a bill have no items?
- Can an item have a cost of zero?

My inner product owner answered _No_ to the first and _Yes_ to the second question.
Thus I had to adapt the Item generator and the property itself.
I started with the generator:

```java
Arbitrary<Item> items() {
  return Arbitraries.integers().between(0, 1000).map(Item::withCost);
}
```

This should break the property, but it does not, so I changed the implementation of
`Bill.canAfford` to only accept items with a cost of zero:

```java
public class Budget...
  public boolean canAfford(Bill bill) {
    return bill.items().stream().allMatch(Item::isForFree);
  }
```

This made the property fail and forced me to constrain the domain within the property:

```java
@Property
void cannot_afford_zero_budget(@ForAll("bills") Bill billWithItems) {
  Assume.that(billWithItems.items().stream().anyMatch(item -> !item.isForFree()));

  Budget zeroBudget = Budget.withTotalLimit(0);
  assertThat(zeroBudget.canAfford(billWithItems)).isFalse();
}
```

Sure enough, there's a "feature envy" smell to trigger an "extract method" refactoring:

```java
class Bill...
  public boolean isForFree() {
    return items.stream().allMatch(Item::isForFree);
  }
  
class Budget...
  public boolean canAfford(Bill bill) {
    return bill.isForFree();
  }
  
class Can_Afford_Properties...
  @Property
  void cannot_afford_zero_budget(@ForAll("bills") Bill billWithItems) {
    Assume.that(!billWithItems.isForFree());
    Budget zeroBudget = Budget.withTotalLimit(0);
    assertThat(zeroBudget.canAfford(billWithItems)).isFalse();
  }
```

Will I keep the initial example test or remove it since it's fully covered by the property?
I often keep examples when they are easier to understand than the equivalent property.
Here, the property seems just as readable, so I delete the example.

# Step 3 - Drive next Feature with Property

In the good old moving-from-simple-to-more-complicated style of TDD the _totalLimit_ seems
the obvious next target. Writing the property for this is mostly straightforward:

```java
@Property
void total_limit_of_budget_used_for_afford(
  @ForAll @IntRange(min = 1) int totalLimit,
  @ForAll("bills") Bill bill
) {
  Budget budget = Budget.withTotalLimit(totalLimit);
  if (bill.totalCost() <= totalLimit) {
    assertThat(budget.canAfford(bill)).isTrue();
  } else {
    assertThat(budget.canAfford(bill)).isFalse();
  }
}
```

Except that I haven't implemented `Bill.totalCost()` yet. 
So I comment out the property above and slip in a property-driven implementation 
of a bill's total cost calculation.

# Step 3.1 - Property-drive Implementation of Stepping Stone Implementation

```java
class Bill_Properties...
  @Property
  void totalCost(@ForAll List<@IntRange(min = 0) Integer> singleCosts) {
    Item[] items = singleCosts.stream()
                              .map(cost -> Item.withCost(cost))
                              .toArray(Item[]::new);
    Bill bill = Bill.of(items);

    int sum = singleCosts.stream().mapToInt(i -> i).sum();
    Assertions.assertThat(bill.totalCost()).isEqualTo(sum);
  }
```

This fails with the simplest falsifiable sample:

```
Bill Properties:totalCost = 
  org.opentest4j.AssertionFailedError:
    expected: 1
    but was : 0
    
Shrunk Sample (10 steps)
------------------------
  singleCosts: [1]
```

Having done this kind of summing a few times I'm quite confident 
getting the basics right in one try:

```java
class Bill...
  public int totalCost() {
    return items.stream().mapToInt(Item::singleCost).sum();
  }
```

What I had expected at this point is the property to fail with a list of items 
the sum of which will lead to an overflow of `Integer.MAX_VALUE`.
However, it does not fail which makes me check the statistics of sum:

```java
@Property
void totalCost(@ForAll @Size(min = 1) List<@IntRange(min = 0) Integer> singleCosts) {
  ...
  int sum = singleCosts.stream().mapToInt(i -> i).sum();
  Statistics.collect(sum);
  ...
}
```

The result reveals the problem:
```
[Bill Properties:totalCost] (1000) statistics = 
    0           (14) :  1.40 %
    2147483647  (13) :  1.30 %
    1           (13) :  1.30 %
    ...
    -2142351300 ( 1) :  0.10 %
    ...
```

There are negative sums but since the property uses (more or less) the same code
as oracle that the domain code uses for total cost calculation _both_ will arrive
at the same negative value. 
My mistake, I definitely forgot one part of the property, so I add it:

```java
@Property
void totalCost(@ForAll @Size(min = 1) List<@IntRange(min = 0) Integer> singleCosts) {
  ...
  int sum = singleCosts.stream().mapToInt(i -> i).sum();
  assertThat(sum).isGreaterThanOrEqualTo(0);
  ...
}
```

Now the property fails at immediately:
```
Bill Properties:totalCost = 
  java.lang.AssertionError:
    Expecting:
      -2147483648
    to be greater than or equal to:
      0

Shrunk Sample (16 steps)
------------------------
  singleCosts: [1, 2147483647]
```

and forces me to clarify what to do about the potential overflow.
I could go with an unlimited data type like `BigInteger`.
However, its usage is so cumbersome in Java, that's why the ~~enemy~~ product owner in me decides 
to ignore the problem: 
"No user will ever have more than 20 items and no single item will ever cost more than 1000".
That domain constraint has to be reflected in the generators:

```java
class Bill_Properties...
  @Property
  void totalCost(@ForAll @Size(min = 1, max = 20) List<@IntRange(min = 0, max = 1000) Integer> singleCosts) {
    ...
  }
```

Re-using the item generator from earlier can simplify this property:

```java
@Property
void totalCost(@ForAll @Size(min = 1, max = 20) List<@From("items") Item> items) {
  Bill bill = Bill.of(items.toArray(new Item[0]));

  int sum = items.stream().mapToInt(Item::singleCost).sum();
  assertThat(sum).isGreaterThanOrEqualTo(0);
  assertThat(bill.totalCost()).isEqualTo(sum);
}
```

Was there any advantage from using a property rather than two or three examples to drive _totalCost_?
Well, it freed me from thinking about the usual partitions like 0 items, 1 items, many items.
Moreover, I feel more secure with doing bigger chunks of implementation in one step 
when backed by a property as compared to "just" an example. 
This could arguably be a false sense of security, though. 