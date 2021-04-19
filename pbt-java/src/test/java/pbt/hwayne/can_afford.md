# Budget and Bills

Tackling the problem specified by Hillel Wayne in https://gist.github.com/hwayne/e5a65b48ab50a2285de47cfc11fc955f
with a combination of example tests and property-based testing.


<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Step 1 - Use Example to Discover API](#step-1---use-example-to-discover-api)
- [Step 2 - Transform Example into More Generic Property](#step-2---transform-example-into-more-generic-property)
- [Step 3 - Drive next Feature with Property](#step-3---drive-next-feature-with-property)
  - [Step 3.1 - Property-drive Implementation of Stepping Stone Implementation](#step-31---property-drive-implementation-of-stepping-stone-implementation)
  - [Step 3.2 - Use new Implementation for Feature](#step-32---use-new-implementation-for-feature)
  - [Step 3.3 - Adding Item.count()](#step-33---adding-itemcount)
  - [Step 3.4 - Making Domain Constraints Explicit](#step-34---making-domain-constraints-explicit)
- [Step 4 - Budget with Single Category Items](#step-4---budget-with-single-category-items)
  - [Step 4.1 Keeping Track of Limits and Categories](#step-41-keeping-track-of-limits-and-categories)
  - [Step 4.2 - Examples for Single Category](#step-42---examples-for-single-category)
  - [Step 4.3 - Budget Generator and Properties](#step-43---budget-generator-and-properties)
- [Step 5 - Targeting Items with Multiple Categories](#step-5---targeting-items-with-multiple-categories)
  - [Step 5.1 - Making Items Ready for Multiple Categories](#step-51---making-items-ready-for-multiple-categories)
  - [Step 5.2 - Enable Item Generator for Multiple Categories](#step-52---enable-item-generator-for-multiple-categories)
  - [Step 5.3 - Maximum Permissiveness by Example](#step-53---maximum-permissiveness-by-example)
  - [Step 5.4 - Adding Properties to Detect Oversights](#step-54---adding-properties-to-detect-oversights)
- [Wrap Up](#wrap-up)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


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

## Step 3 - Drive next Feature with Property

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

### Step 3.1 - Property-drive Implementation of Stepping Stone Implementation

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

Having done this kind of summing up a few times in my career, I'm quite confident 
to get the basics right in one try:

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

There are __negative sums__, but since the property uses (more or less) the same code
as oracle that the domain code uses for total cost calculation, _both_ will arrive
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

Now the property fails immediately:
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


### Step 3.2 - Use new Implementation for Feature

Now that `Bill.totalCost()` exists let's revisit and re-activate 
the property for the total limit feature:

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

As expected it fails. The falsified example, however, is a bit uninformative:

```
Shrunk Sample (17 steps)
------------------------
  totalLimit: 1
  bill: pbt.hwayne.Bill@d6e7bab
```

It clearly tells me that `Bill` and `Item` need reasonable reporting functionality.
In Java implementing `toString()` is the obvious way - and I'd certainly do it - but _jqwik_ 
[has additional reporting capabilities](https://jqwik.net/docs/current/user-guide.html#failure-reporting)
which I'll use here (see [BudgetReportingFormats](https://github.com/jlink/property-based-testing/blob/main/pbt-java/src/test/java/pbt/hwayne/BudgetReportingFormats.java)).
The sample report is more to the point now:

```
Shrunk Sample (17 steps)
------------------------
  totalLimit: 1
  bill: {"items"=[{"singleCost"=1}]}
```

Making the property succeed is straightforward:

```java
public class Budget...
  public boolean canAfford(Bill bill) {
    return bill.totalCost() <= totalLimit;
  }
```

I keep wondering, though, if both branches of the invariant are really covered;
statistics and coverage checking come to the help:

```java
@Property
void total_limit_of_budget_used_for_afford(
  @ForAll @IntRange(min = 1) int totalLimit,
  @ForAll("bills") Bill bill
) {
  Budget budget = Budget.withTotalLimit(totalLimit);

  boolean canBeAfforded = bill.totalCost() <= totalLimit;
  Statistics.label("bill can be afforded")
            .collect(canBeAfforded)
            .coverage(checker -> {
              checker.check(true).percentage(p -> p > 10);
              checker.check(false).percentage(p -> p > 10);
            });

  if (canBeAfforded) {
    assertThat(budget.canAfford(bill)).isTrue();
  } else {
    assertThat(budget.canAfford(bill)).isFalse();
  }
}
```

The report shows that there is more or less equal distribution between the two cases:

```
Can Afford Properties:total limit of budget used for afford] (1000) bill can be afforded = 
    true  (553) : 55 %
    false (447) : 45 %
```

Arguably, the combination of branching and coverage checking could (and probably should) 
require less boilerplate. 
Quickcheck has a dedicated feature for that and 
[jqwik deserves one as well](https://github.com/jlink/jqwik/issues/86). 


### Step 3.3 - Adding Item.count()

When working towards a concrete goal - like now - I maintain a "inbox". 
All things that must be done for the goal to be reached and that cannot be done at once
get in there. Here's an excerpt of my can-afford inbox:

- _Consider Categories for afford_
- _Item.count(): Use it for cost calculation._

Whereas the first point has been there from the beginning and will be broken down in a future step,
I added _Item.count()_ recently while implementing `Bill.totalCost()`. 
It's not a particular difficult thing to do, but it's important for getting done.
Since I consider `count` to be "just" an attribute to remember 
I go with the obvious property to drive implementation:

```java
@Group
class Item_Properties {
  @Property
  void totalCost_considers_count(
    @ForAll @IntRange(max = 1000) int singleCost,
    @ForAll @IntRange(min = 1, max = 100) int count
  ) {
    Item item = Item.withCostAndCount(singleCost, count);
    assertThat(item.singleCost()).isEqualTo(singleCost);
    assertThat(item.count()).isEqualTo(count);
    assertThat(item.cost()).isEqualTo(singleCost * count);
  }
}
```

This property is somewhat more complex than a simple example, but it frees me from deciding whether triangulation is necessary.
Moreover, it forces me to think about the maximum possible _count_ value.

I'll spare you the trivial implementation of _count_ and _cost_, but there's more to do:
`Bill.totalCost()` is still based on `Item.singleCost()`. 
That must be fixed, and I prefer to have a failing property tell me so.
Changing the _items_ generator and the _total cost_ property looks like a reasonable means to that end:

```java
@Provide
Arbitrary<Item> items() {
  Arbitrary<Integer> singleCosts = Arbitraries.integers().between(0, 1000);
  Arbitrary<Integer> counts = Arbitraries.integers().between(1, 100);
  return Combinators.combine(singleCosts, counts).as(Item::withCostAndCount);
}

@Group
class Bill_Properties {
  @Property
  void totalCost(@ForAll @Size(min = 1, max = 20) List<@From("items") Item> items) {
    Bill bill = Bill.of(items.toArray(new Item[0]));

    int sum = items.stream().mapToInt(Item::cost).sum();
    assertThat(sum).isGreaterThanOrEqualTo(0);
    assertThat(bill.totalCost()).isEqualTo(sum);
  }
}
```

And indeed, the property fails and makes me adapt `Bill.totalCost()`:

```java
public class Bill...
  public int totalCost() {
    return items.stream().mapToInt(Item::cost).sum();
  }
```

### Step 3.4 - Making Domain Constraints Explicit

So far, a couple of domain decisions had to be taken on the way:
- A bill must have at least one item
- An Item can have a single cost between 0 and 1000
- An Item's count is between 1 and 100. When no count is given, 1 is the default.

Those constraints are embedded in the generators. 
They should be made explicit, e.g. through constants within the domain code.
Moreover, generator code should be refactored so that deriving new generators 
and writing new properties has only little chance of missing the constraints.

Let's start with extracting a few constants:

```java
public class Bill...
  public static int MIN_NUMBER_OF_ITEMS = 1;

public class Item...
  public static final int MAX_SINGLE_COST = 1000;
  public static final int DEFAULT_COUNT = 1;
  public static final int MAX_COUNT = 100;
```

What I also did is extracting some parts of the items generator:

```java
@Provide
Arbitrary<Item> items() {
  return Combinators.combine(itemSingleCosts(), itemCounts()).as(Item::withCostAndCount);
}

IntegerArbitrary itemCounts() {
  return Arbitraries.integers().between(Item.DEFAULT_COUNT, Item.MAX_COUNT);
}

IntegerArbitrary itemSingleCosts() {
  return Arbitraries.integers().between(0, Item.MAX_SINGLE_COST);
}
```

Additionally, I introduced two custom annotations to facilitate further domain-specific properties:

```java
@IntRange(min = 0, max = Item.MAX_SINGLE_COST) 
@interface ItemSingleCost {}

@IntRange(min = Item.DEFAULT_COUNT, max = Item.MAX_COUNT) 
@interface ItemCount {}

@Property
void totalCost_considers_count(
  @ForAll @ItemSingleCost int singleCost,
  @ForAll @ItemCount int count
) {
  Item item = Item.withCostAndCount(singleCost, count);
  assertThat(item.singleCost()).isEqualTo(singleCost);
  assertThat(item.count()).isEqualTo(count);
  assertThat(item.cost()).isEqualTo(singleCost * count);
}
```

_jqwik_ offers even more mechanisms for modularizing and encapsulating domain specific
value generation, but this is not supposed to be a _jqwik_ tutorial.


## Step 4 - Budget with Single Category Items

Being an impatient person my feeling is that we've been lingering over the easy parts for too long.
Time to tackle the heart of the problem: categories. 

### Step 4.1 Keeping Track of Limits and Categories

I'll fast-forward over the more or less trivial necessities: `Item.with(int cost, int count, Set<String> categories)` and
`Budget.with(int totalLimit, Set<Limit> limit)`. 
You can find my properties for these book-keeping features 
[in the code base](https://github.com/jlink/property-based-testing/tree/main/pbt-java/src/test/java/pbt/hwayne).

One notable detail: I anticipated the need to have matching categories in later properties. 
So I tweaked the category string generator in order to raise the probability of duplicate category creation:

```java
@Provide
Arbitrary<String> categories() {
  return Arbitraries.oneOf(
    Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10),
    Arbitraries.of("food", "rent", "candles", "gym", "transit", "clothes")
  );
}
```

Later on I'll probably have to revisit the frequencies of unique versus duplicate strings.
On the other hand I had to make sure that the same category does not occur more than once
per budget. That's a domain constraint, or is it @hwaine?

```java
@Provide
Arbitrary<Set<Limit>> setOfLimits() {
  return limits().set().uniqueElements(Limit::category).ofMaxSize(10);
}
```

### Step 4.2 - Examples for Single Category

Trying to think of properties that must hold for single category affording:
- Changing the total budget limit for a previously unaffordable bill will leave it unaffordable
- Changing the budget limit for a category not in the bill won't change the afford decision
- Raising any budget limit for a previously affordable bill will leave it affordable

I could come up with more of those [metamorphic properties](https://johanneslink.net/how-to-specify-it/#43-metamorphic-properties). 
All have in common that they tell us something about the behaviour of two related function calls.
Properties of this kind can be powerful and are valuable.
What they are not is good, readable and prototypical examples for the expected outcome.
So I decided to start the feature off with an example:

```java
@Example
void limit_of_category_is_considered_for_item_with_single_category() {
  Budget budget = Budget.with(100, setOf(
    Limit.of("books", 50)
  ));

  Bill bill = Bill.of(Item.with(51, 1, "books"));

  assertThat(budget.canAfford(bill)).isFalse();
}
```

The test fails, and I make it succeed with one of TDD's main strategies 
called "Fake it till you make it":

```java
public boolean canAfford(Bill bill) {
  if (isOutsideTotalBudget(bill)) {
    return false;
  };
  for (Item item : bill.items()) {
    if (item.category().isPresent()) {
      return false;
    }
  }
  return true;
}
```

The code is obviously wrong since it will reject any bill with a categorized item.
Without PBT I'd now have to add new examples to instigate the "correct" implementation.
Looking at the existing properties there is one that already should have caught
my naive implementation from above:

```java
@Property
void total_limit_of_budget_used_for_afford(
  @ForAll @IntRange(min = 1) int totalLimit,
  @ForAll("bills") Bill bill
) {
  Budget budget = Budget.withTotalLimit(totalLimit);
  boolean canBeAfforded = bill.totalCost() <= totalLimit;
  ...
  if (canBeAfforded) {
    assertThat(budget.canAfford(bill)).isTrue();
  } else {
    assertThat(budget.canAfford(bill)).isFalse();
  }
}
```

So why does it not catch it? It's the items generator that I forgot to adapt when
introducing categories for items. 
Repairing my oversight requires to add an optional category to the creation of `Item` instances.

```java
@Provide
Arbitrary<Item> items() {
  Arbitrary<String> categories = categories().injectNull(0.2);
  return Combinators.combine(itemSingleCosts(), itemCounts(), categories).as(Item::with);
}
```

This makes `total_limit_of_budget_used_for_afford` fail. 
The code to fix the failing test:

```java
public boolean canAfford(Bill bill) {
  if (isOutsideTotalBudget(bill)) {
    return false;
  }
  for (Item item : bill.items()) {
    if (item.category().isPresent()) {
      if (isOutsideCategoryBudget(item.cost())) {
        return false;
      }
    }
  }
  return true;
}

private boolean isOutsideCategoryBudget(int cost) {
  return limits.stream()
               .anyMatch(limit -> limit.amount() < cost);
}
```

The code is still not correct - it does not compare if a limit actually is for the item's category.
That's why we either need a new example or a new property. 
For example this one:

```java
@Example
void limit_of_category_must_match_item_category() {
  Budget budget = Budget.with(100, setOf(
    Limit.of("books", 50)
  ));

  Bill bill = Bill.of(Item.with(51, 1, "gym"));

  assertThat(budget.canAfford(bill)).isTrue();
}
```

This failing test will force the code to add category checking.
However, it also triggers more questions:
- Does it work for any order of budget limits?
- What if one item matches a budget limit and another does not?
- What if the bill contains more than one item of the same category?
- etc.

Whenever several of those questions pop up that may be related and that cannot be answered with confidence,
I reach for properties instead of examples. That's the topic for the next step...

### Step 4.3 - Budget Generator and Properties

Good properties need good generators. 
In my experience good generators cover the domain type.
The central domain type is `Budget`; let's add a generator for it:

```java
@Provide
Arbitrary<Budget> budgets() {
  int maxLimit = Item.MAX_SINGLE_COST * Item.MAX_COUNT * 10;
  Arbitrary<Integer> totalLimit = Arbitraries.integers().between(1, maxLimit);
  return Combinators.combine(totalLimit, setOfLimits())
                    .as(Budget::with);
}
```

Since we want to use the budget generator in combination with a bill generator,
it's important that the different cases do really occur, e.g. that the categories used
for the budget are also present in the bill. 
As soon as you leave the area of trivial generators, it's worthwhile to check
if generators expose the desired behaviour:

```java
@Property
void generated_budget_limits_and_item_categories_overlap(
  @ForAll("budgets") Budget budget,
  @ForAll("bills") Bill bill
) {
  Set<String> categoriesInLimits = budget.limits().stream()
                       .map(Limit::category)
                       .collect(Collectors.toSet());
  Set<String> categoriesInItems = bill.items().stream()
                    .filter(i -> i.category().isPresent())
                    .map(i -> i.category().get()).collect(Collectors.toSet());

  Statistics.label("categories overlap").collect(overlap(categoriesInItems, categoriesInLimits));
}
```

```
[Can Afford Properties:generated budget limits and item categories overlap] (1000) categories overlap = 
    false (587) : 59 %
    true  (413) : 41 %
```

We see that there's an overlap in about half the cases. 
That's not too bad, but that also means that the other half of cases 
will only be useful for checking the total budget limit.
Let's tweak the category generator a bit by raising the probability for four constant categories:

```java
@Provide
Arbitrary<String> categories() {
  return Arbitraries.frequencyOf(
    Tuple.of(100, Arbitraries.of("a", "b", "c", "d")),
    Tuple.of(1, Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10))
  );
}
```

This gets us to about 60:40, which seems ok for now.
If fiddling with probabilities does not get you where you want to be, 
there are many more tricks in the bag of generator building.
Most have to do with using the output of one generator to configure the domain range for another.

Here's my first attempt at a property with random budgets and bills:

```java
@Property
void limits_of_single_categories_are_preserved(
  @ForAll("budgets") Budget budget,
  @ForAll("bills") Bill bill
) {
  Assume.that(budget.totalLimit() >= bill.totalCost());
  Set<String> categoriesInLimits = budget.limits().stream()
                       .map(Limit::category)
                       .collect(Collectors.toSet());
  Set<String> categoriesInItems = bill.items().stream()
                    .filter(i -> i.category().isPresent())
                    .map(i -> i.category().get()).collect(Collectors.toSet());

  Set<String> sharedCategories = intersect(categoriesInLimits, categoriesInItems);
  Assume.that(!sharedCategories.isEmpty());

  // Only about 20% of generated test cases get here

  for (String category : sharedCategories) {
    int total = totalForSingleCategory(category, bill);
    if (total > limitForCategory(category, budget)) {
      assertThat(budget.canAfford(bill))
        .describedAs("category %s should not be affordable", category)
        .isFalse();
    }
  }
  assertThat(budget.canAfford(bill))
    .describedAs("full bill should be affordable")
    .isTrue();
}
```

This is already rather complicated.
It has two assumptions to filter out budgets whose totalLimit is exceeded 
and cases without shared categories. 
Only about 20% of all generated cases get through beyond this hurdle.
But then - hooray! - our hypothesis is falsified:

```
Can Afford Properties:limits of single categories are preserved = 
  org.opentest4j.AssertionFailedError:
    [category d should not be affordable] 
    Expecting value to be false but was true
    
Shrunk Sample (5 steps)
-----------------------
  budget: {"totalLimit"=999999, "limits"=[{"amount"=50654, "category"="d"}]}
  bill:
    {
      "items"=
        [
          {"singleCost"=5, "count"=32, "category"="d"}, 
          {"singleCost"=749, "count"=5, "category"="d"}, 
          {"singleCost"=41, "count"=3, "category"="d"}, 
          {"singleCost"=404, "count"=28, "category"="d"}, 
          {"singleCost"=192, "count"=13, "category"="d"}, 
          {"singleCost"=817, "count"=62, "category"="d"}, 
          {"singleCost"=787, "count"=60, "category"="d"}, 
          {"singleCost"=52, "count"=9, "category"="d"},
          ... 
        ]
    }    
```

That's a real bug.
The implementation did not consider a bill with more than one item of the same category.
Let's repair it:

```java
public class Budget...
  public boolean canAfford(Bill bill) {
    if (isOutsideTotalBudget(bill)) {
      return false;
    }
    Map<String, Integer> aggregatedTotals = aggregate(bill.items());
    for (Map.Entry<String, Integer> total : aggregatedTotals.entrySet()) {
      if (isOutsideCategoryBudget(total.getKey(), total.getValue())) {
        return false;
      }
    }
    return true;
  }

  private Map<String, Integer> aggregate(List<Item> items) {
    Map<String, Integer> aggregated = new HashMap<>();
    for (Item item : items) {
      for (String category : item.categories()) {
        int total = aggregated.getOrDefault(category, 0);
        total += item.cost();
        aggregated.put(category, total);
      }
    }
    return aggregated;
  }
```

My inkling is that this implementation cannot be evolved in a straightforward way
as soon as multiple categories per item enter the scene.
But we are evolutionary designers; let's stay optimistic then!


## Step 5 - Targeting Items with Multiple Categories

This feature is supposed to bring in the real domain complexity.
I expect quite a few things to break on the way, and also a few questions to arise.

### Step 5.1 - Making Items Ready for Multiple Categories

I'll start with refactoring the API in two steps: 
- `Item with(int singleCost, int count, String category)` -> `Item with(int singleCost, int count, String ... categories)`
- `Item.category() : Optional<String>` -> `Item.categories() : Set<String>`

How to achieve this in small, safe steps would be an interesting article, too. 
It's not in the focus today, so I'll just do it...

Done. On my way I had to change the item generator to serve the changed API:

```java
@Provide
Arbitrary<Item> items() {
  Arbitrary<String[]> categories = categories().array(String[].class).ofMaxSize(1);
  return Combinators.combine(itemSingleCosts(), itemCounts(), categories).as(Item::with);
}
```

I did not, however, introduce more than one category per item. Yet.  


### Step 5.2 - Enable Item Generator for Multiple Categories

The current item generator will never generate items with more than one category.
We can easily change that and allow up to 5 categories.

```java
@Provide
Arbitrary<Item> items() {
  Arbitrary<String[]> categories = categories().array(String[].class).ofMaxSize(5);
  return Combinators.combine(itemSingleCosts(), itemCounts(), categories).as(Item::with);
}
```

Using this altered behaviour will not disturb most properties because they should and can work with any valid items.
Yet one is not: `limits_of_single_categories_are_preserved`.
As the name says, this property is supposed to hold for single categories only.
Let's fix it!

Our first attempt is to add another assumption to the property, which ignores all
bills that have items with more than one category:

```java
Assume.that(bill.items().stream().allMatch(i -> i.categories().size() <= 1));
```

Sadly, this will throw away too many test cases:

```
Can Afford Properties:limits of single categories are preserved = 
  org.opentest4j.AssertionFailedError:
    Property [Can Afford Properties:limits of single categories are preserved] exhausted after [1000] tries and [883] rejections
```

We raise jqwik's tolerance level for exhausted generation but in the end we want as many valid test cases as possible.
That's why I decided to change the generator instead.
There are a few mechanisms how generators can be influenced in jqwik - e.g. annotations, domains -
but I'll go with the simplest one here: Calling a different generator method:

```java
@Property
void limits_of_single_categories_are_preserved(
  @ForAll("budgets") Budget budget,
  @ForAll("billsWithSingleCategoryItems") Bill bill
) {
  ...
}

@Provide
Arbitrary<Bill> billsWithSingleCategoryItems() {
  Arbitrary<Item[]> items = listOfItemsWithSingleCategory().map(l -> l.toArray(new Item[0]));
  return items.map(Bill::of);
}

Arbitrary<List<Item>> listOfItemsWithSingleCategory() {
  return items(1).list().ofMaxSize(Bill.MAX_NUMBER_OF_ITEMS);
}

Arbitrary<Item> items(int maxSizeCategories) {
  Arbitrary<String[]> categories = categories().array(String[].class).ofMaxSize(maxSizeCategories);
  return Combinators.combine(itemSingleCosts(), itemCounts(), categories).as(Item::with);
}
```

This approach required to introduce a parameter in the `items` method,
but that's just plain abstraction through parameterization.

We have now reached a point where all but one property are as generic as they can be.
Another observation: The chosen approach has led to 7 properties and 2 remaining examples so far.
The suite runs in less than 2 seconds on my machine, despite ~ 7000 test cases being generated.


### Step 5.3 - Maximum Permissiveness by Example

So far, I haven't really thought through the problem's full complexity. 
Therefore, starting with the example from the spec seems like a good first step:

```java
@Example
void when_in_doubt_be_permissive() {
  Budget budget = Budget.with(
    5,
    setOf(
      Limit.of("a", 1),
      Limit.of("b", 3)
    )
  );

  Bill bill = Bill.of(Item.with(2, "a", "b"));
  assertThat(budget.canAfford(bill)).isTrue();
}
```

I was pretty sure that the current implementation would not cover this case;
and indeed, the example failed. 
I played around a while with localized changes to make this example (together with all the other tests) succeed,
but I failed.
It looked to me that now the algorithm needed a fundamental change:

- Start with the assumption that bill can be afforded
- For each item in the bill
  - If no budget limit applies, continue
  - Try to find fitting category to which it can be added without breaking the category's budget
    - If there is none, stop and return false
    - If there is one, update the remaining budget for this category  
- Return true

Turning this into working code took me about 15 minutes - 
way too long for my person feeling of controlled progress -
but then every example and every property succeeded:

```java
public boolean canAfford(Bill bill) {
  if (isOutsideTotalBudget(bill)) {
    return false;
  }
  Map<String, Integer> availableBudgets = initialBudgets(limits);
  for (Item item : bill.items()) {
    if (noBudgetApplies(item)) {
      continue;
    }
    Optional<String> fittingCategory = findFittingCategory(item, availableBudgets);
    if (!fittingCategory.isPresent()) {
      return false;
    }
    fittingCategory.ifPresent(category -> updateBudgets(availableBudgets, category, item.cost()));
  }
  return true;
}
```


### Step 5.4 - Adding Properties to Detect Oversights

At this point my intuition as a software developer tells me that the implementation does not cover all cases. 
The question that bothers me most: What if several items come with different but overlapping categories?
In the old days - with example testing as my only tool - 
I would have created a matrix of conditions and edge cases to help me create a few more test cases 
with the potential to reveal bugs.
Nowadays I prefer pondering over generic properties instead.

Quite a few strategies and patterns have evolved during the twenty years of PBT practice.
John Hughes, one of the original inventors of Quickcheck, has recently (July 2019) summarized his learnings
in a paper called [_How to Specify it!_](https://www.dropbox.com/s/tx2b84kae4bw1p4/paper.pdf).
If you prefer Java to Haskell, [this](https://johanneslink.net/how-to-specify-it/) 
is my transfer of the paper into a language for the mortal OO programmer.

One of the most interesting ideas treated therein are _metamorphic properties_;
they are all about the relation between two or more executions of the code under attack.
_Order independence_ is a metamorphic relation that pops into my mind here:
Given the result of an arbitrary call to `Budget.canAfford(bill)`, 
this result should not change if the order of limits in the budget 
or the order of items in the bill changes. 
Let's translate these idea into two jqwik properties:

```java
@Property
void order_of_limits_does_not_change_result(
  @ForAll("budgets") Budget budget,
  @ForAll("bills") Bill bill,
  @ForAll Random random
) {
  boolean canAfford = budget.canAfford(bill);

  List<Limit> shuffledLimits = new ArrayList<>(budget.limits());
  Collections.shuffle(shuffledLimits, random);
  Budget changedBudget = Budget.with(
    budget.totalLimit(),
    new HashSet<>(shuffledLimits)
  );

  assertThat(changedBudget.canAfford(bill)).isEqualTo(canAfford);
}
```

It's a bit clumsy to write, because we have to convert a set to list and back.
Moreover, it succeeds, and we didn't learn anything new.

```java
@Property
void order_of_items_does_not_change_result(
  @ForAll("budgets") Budget budget,
  @ForAll("bills") Bill bill,
  @ForAll Random random
) {
  boolean canAfford = budget.canAfford(bill);

  List<Item> shuffledItems = new ArrayList<>(bill.items());
  Collections.shuffle(shuffledItems, random);
  Bill changedBill = Bill.of(shuffledItems.toArray(new Item[0]));

  assertThat(budget.canAfford(changedBill)).isEqualTo(canAfford);
}
```

This one also succeed at the first run, i.e. with 1000 tries.
My guts, however, tell me to re-run it a few times. 
Et voilà, the 11th run produces a failing example:

```
Affordability:order of items does not change result = 
  org.opentest4j.AssertionFailedError:
    expected: false
    but was : true

Shrunk Sample (129 steps)
-------------------------
  budget: {"totalLimit"=9142, "limits"=[{"amount"=2, "category"="d"}, {"amount"=9141, "category"="a"}]}
  bill:
    {
      "items"=
        [
          {"singleCost"=1, "count"=1, "categories"=["a", "d"]}, 
          {"singleCost"=10, "count"=26, "categories"=["a"]}, 
          {"singleCost"=107, "count"=83, "categories"=["a"]}
        ]
    }
  random: java.util.Random@3fba4d47
```

Running items through the can-afford-algorithm in the original order will fail,
because the budget for "a" is already down to 8880 before the last item with
a cost of 8881 (107*83) is tried to fit in. 
If, however, the first item will be tried last, its cost will be taken from
category "d"'s budget and all is affordable. 
This is the actually desired behaviour - it's the most permissive one.

The fix is, again, more involved than I had hoped. 
If my assumptions are right (I have been wrong before), the generic solution requires
to check the permutations of bill items to rule out a more permissive budget calculation.
Going for all permutations, however, forced me to restrict the number of generated items per bill to a maximum of 8.
Otherwise, runtime would exceed my personal threshold of patience.
So I went for a few simple optimisations, which enabled me to raise the number to 14.

## Wrap Up

The final implementation, which in its core is old-style procedural/OO code 
is [here](https://github.com/jlink/property-based-testing/blob/main/pbt-java/src/test/java/pbt/hwayne/Budget.java).

I ended up with 10 properties and 3 examples. 
In other projects I often have about as many properties as examples.

One open domain question: How many items can a bill have?
14 seems too low, but with higher numbers, more optimisation or a better algorithm would be needed.
Would I be aware of this limit without PBT? Maybe, maybe not.



