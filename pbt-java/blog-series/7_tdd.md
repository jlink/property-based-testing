# PBT and Test-driven Development

It's been a few months that I wrote about
[stateful testing]({% post_url 2018-09-06-stateful-testing %}).
Since then I've given a few workshops about property-based testing and
jqwik has reached [version 1.0.0](https://jqwik.net/release-notes.html#100).
So, I'm getting more and more committed to PBT but, to be frank, when developing
software for clients my main approach is still Test-Driven Development with
mostly example-based test cases.
Wouldn't it be nice if both my favourite topics could be merged in
some sort of grand unified development approach?
Turns out that they do go together quite well, at least sometimes.

## FizzBuzz - Property-Test-Driven

To get the discussion going I'll demonstrate one possible approach by tackling
the infamous [FizzBuzz problem](http://codingdojo.org/kata/FizzBuzz/) using a
combination of TDD and PBT.

### Starting with Examples

More often than not I learn looking at a few examples is an easy pathway into
a new domain or a new problem. Given a few "normal" numbers
- e.g. 1, 2, 4, 76 - leads us to a first example

```java
class FizzBuzzDemo {
	@Example
	void normal_numbers_return_themselves() {
		assertThat(count(1)).isEqualTo("1");
	}
}
```

and a trivial implementation:

```java
String count(int index) {
    return "1";
}
```



