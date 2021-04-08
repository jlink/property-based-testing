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




