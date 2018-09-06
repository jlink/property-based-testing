# Stateful Testing

The last episode was about
[patterns to find good properties]({% post_url 2018-07-16-patterns-to-find-properties %}).
One pattern I mentioned there is _Stateful Testing_. Some PBT proponents treat this one
with some condescension since _state_ is considered as something that should be avoided
- at least in functional programming.

I tend to look at it with more tolerance.
I agree with the general notion of trying to get away from state if you reasonably can,
but sometimes the stateless implementation of an algorithm feels weired -
especially when working in a language like Java, in which some functional constructs
require a lot of additional boilerplate. In other situations a _state machine_ is
at the heart of a domain problem and you won't get away without state in one form or another.
Those are the times when properties to test the expected behaviour of a state machine come in
very handy. Let's look at a simple example: a stack.

Our stack is supposed to handle only strings, so the interface is simple
and probably obvious:

```java
public class MyStringStack {
	public void push(String element) { ... }

	public String pop() { ... }

	public void clear() { ... }

	public boolean isEmpty() { ... }

	public int size() { ... }

	public String top() { ... }
}
```

## Finite State Machines

As any object with state a stack can be considered to be a
[finite state machine](https://en.wikipedia.org/wiki/Finite-state_machine)
which comes with a few characteristics:

1. It always has a defined _current state_ - out of a finite list of possible states.
2. For every state there is a finite and possible empty list of _transitions_
   that bring the state machine into its next state.
3. Transitions are often triggered by _actions_ (aka _events_).

One way to display a finite state machine is a _state transition table_.
Here's one for our example:

|Current State|Action|Next State|
|-------------|------|----------|
|empty        |push  |filled    |
|empty        |clear |empty     |
|filled       |push  |filled    |
|filled(1)    |pop   |empty     |
|filled(1+)   |pop   |filled    |
|filled       |clear |empty     |

This table reveals a few things:
- It can give us a hint about what transitions we might want to test.
- Not all actions are allowed in all states, e.g. `pop` does not
  make sense for an empty stack.
- A stack has additional and important functional properties that are not visible
  in the transition table, e.g. `pop` should always return the last
  element that was `push`ed on the stack. Those functional properties
  can typically be considered as postconditions or invariants.

Looking at this from a property-based testing point of view will suggests
the following approach:

1. Generate a random sequence of actions
2. Apply this sequence to a state machine in its initial state
3. For any (allowed) action, check that the resulting state is correct
   and that any other invariants and postconditions hold

With some imagination you should be able to implement this approach using
just _jqwik_'s basic mechanisms. It's easier, however, to leverage the library's
built-in support for state testing. There's two things you have to do:

1. Define a state machine's possible actions
2. Formulate property to execute the state machine


## Defining the Actions

Actions have to implement the `Action` interface:

```java
public interface Action<M> {

	default boolean precondition(M model) {
		return true;
	}

	M run(M model);
}
```

The `precondition` method
is only required for actions that are not always allowed, e.g. `pop`.
The `run` method does not only apply an action to a model but it should
also check all postconditions of an action, including the new state.

For convenience I'll put all action definitions into a single
container class:

```java
class MyStringStackActions {

	static Arbitrary<Action<MyStringStack>> actions() {
		return Arbitraries.oneOf(push(), clear(), pop());
	}

	static Arbitrary<Action<MyStringStack>> push() {
		return Arbitraries.strings().alpha().ofLength(5).map(PushAction::new);
	}

	private static Arbitrary<Action<MyStringStack>> clear() {
		return Arbitraries.constant(new ClearAction());
	}

	private static Arbitrary<Action<MyStringStack>> pop() {
		return Arbitraries.constant(new PopAction());
	}

	private static class PushAction implements Action<MyStringStack> {

		private final String element;

		private PushAction(String element) {
			this.element = element;
		}

		@Override
		public MyStringStack run(MyStringStack model) {
			int sizeBefore = model.size();
			model.push(element);
			Assertions.assertThat(model.isEmpty()).isFalse();
			Assertions.assertThat(model.size()).isEqualTo(sizeBefore + 1);
			return model;
		}

		@Override
		public String toString() {
			return String.format("push(%s)", element);
		}
	}

	private static class ClearAction implements Action<MyStringStack> {

		@Override
		public MyStringStack run(MyStringStack model) {
			model.clear();
			Assertions.assertThat(model).isEqualTo(new MyStringStack());
			return model;
		}

		@Override
		public String toString() {
			return "clear";
		}
	}

	private static class PopAction implements Action<MyStringStack> {

		@Override
		public boolean precondition(MyStringStack model) {
			return !model.isEmpty();
		}

		@Override
		public MyStringStack run(MyStringStack model) {
			int sizeBefore = model.size();
			String topBefore = model.top();

			String popped = model.pop();
			Assertions.assertThat(popped).isEqualTo(topBefore);
			Assertions.assertThat(model.size()).isEqualTo(sizeBefore - 1);

			return model;
		}

		@Override
		public String toString() {
			return "pop";
		}
	}
}
```

This class contains both, the action implementations and methods to create
arbitrary instances for those actions.

From the outside only the static `actions` method is relevant because
it will be used to generate sequences.


## Defining the Property

The most common property test is very straightforward:

```java
@Property
void checkMyStackMachine(@ForAll("sequences") ActionSequence<MyStringStack> sequence) {
    sequence.run(new MyStringStack());
}

@Provide
Arbitrary<ActionSequence<MyStringStack>> sequences() {
    return Arbitraries.sequences(MyStringStackActions.actions());
}
```

The `ActionSequence` interface is part of _jqwik_ and therefore the library
knows how to create sequences - given a method to generate actions -,
how to apply a sequence to a model, and how to shrink a sequence if a
failing example has been found.


## Running the Property

Let's inject a bug into the stack implementation by disabling `clear` if
the stack contains more than 3 elements:

```java
public class MyStringStack...
	private final List<String> elements = new ArrayList<>();

	public void push(String element) {
		elements.add(0, element);
	}

	public String pop() {
		return elements.remove(0);
	}

	public void clear() {
		if (elements.size() < 4) elements.clear();
	}
```

Running the property will indeed give the result we'd expect:

```
org.opentest4j.AssertionFailedError: Run failed after following actions:
    push(AAAAA)
    push(AAAAA)
    push(AAAAA)
    push(AAAAA)
    clear
  final state: [AAAAA, AAAAA, AAAAA, AAAAA]
expected:<[[]]> but was:<[[AAAAA, AAAAA, AAAAA, AAAAA]]>
```

You find the complete example code [here](https://github.com/jlink/property-based-testing/tree/master/pbt-java/src/test/java/pbt/stateful/stack)


## Advanced State Testing

In the example above the model can be the stack implementation itself since
the `MyStringStack` contains everything required for executing and
checking. In other situation
(see [CircularBuffer](https://github.com/jlink/property-based-testing/tree/master/pbt-java/src/test/java/pbt/stateful/circularbuffer))
you might have to implement a model explicitly for testing purposes.
That example also shows how invariants can be added to state machine testing.

Since `Action.run(M model)` is supposed to return the model in its new state
the approach works perfectly fine for immutable models.

State properties can also be used in integration testing. Consider a RESTful
API that exposed some state. Actions might consist of sending out certain types
of http requests. PBT will probably come up with sequences of requests
that you'd never think of yourself and thereby stress your API in a different
way than hand-made integrated tests do. One caveat, though: Running hundreds or
thousands of tests over a network might take a very long time!

## Next Episode

In the next article I will show in an example how
Test-Driven Development and Property-Based testing can work well together.

