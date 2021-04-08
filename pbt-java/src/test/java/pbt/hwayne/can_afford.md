# Budget and Bills

Tackling the problem specified by Hillel Wayne in https://gist.github.com/hwayne/e5a65b48ab50a2285de47cfc11fc955f
with a combination of example tests and property-based testing.


## Step 1

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


## Step 2

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

