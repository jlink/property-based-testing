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

