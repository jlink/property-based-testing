# Property-based Testing in Java - Introduction

After two decades of doing mostly object-oriented programming it was the claim
that functional languages will greatly simplify the writing of correct concurrent 
programs that first got me interested in Clojure, Erlang and eventually Haskell. 
That was almost 10 years ago, my activities in concurrency have somewhat faded away,
but my curiosity for the functional side of software development has stayed with me.

As a developer I am being test-driven through and through, so it will not surprise you
that a community's approach to testing are often one of the first things I look at
when trying to grasp how "the others" work and tick. And sadly enough - looking at 
functional stuff vastly outnumbers the times that I really try to write functional code.
To make a long story a little shorter, functional programming folks are very much into 
_properties_ as opposed to our OO _unit tests_. This change of focus cannot only
be seen in strictly typed languages like Haskell but also in dynamically typed ones
like Erlang and Clojure. 

Java, Groovy and to a lesser degree JavaScript are still my working horses 
when it comes to earning money. Thus I began to ask myself whether Property-based Testing (PBT)
can help me there as well. ~~Googling~~ DuckDuckGoing for "Java" and 
"Property-based Testing" will give you a few results - some dating back to the time
when JUnit's Theories-Runner was still a thing - but there's much less 
to be found than I had expected. 
Call it a lucky coincidence that two years ago I left the JUnit-5 core team but decided
to experiment with the JUnit platform. That's why I created 
[a new test engine](http://blog.johanneslink.net/2017/04/10/jqwik-junit5-test-engine-alternative/) 
with a strong focus on property tests.

In the weeks to come I'll publish a short series of blog entries describing what I
learned about PBT on the way and how you can use it in Java. So far, five parts are
in the making:

- From Example Tests to Properties
- Jqwik and other Tools
- The Importance of Being Shrunk
- Patterns to Find Good Properties
- PBT and Test-driven Development 

Stay tuned for what's to come...
