# How to Specify it! in Java!

A couple of days ago [John Hughes](https://twitter.com/rjmh), 
one of the most prominent proponents of Property-based Testing, 
published [_How to Specify it!_](https://www.dropbox.com/s/tx2b84kae4bw1p4/paper.pdf).
In this paper he presents 
"five generic approaches to writing \[...\] specifications" a.k.a. properties.

Throughout the paper he uses [QuickCheck in Haskell](http://hackage.haskell.org/package/QuickCheck)
as tool and language of choice. Since many of my readers are not familiar with Haskell
I want to translate the examples into Java using [jqwik](https://jqwik.net)
as property testing library. 
[John was kind enough](https://twitter.com/rjmh/status/1147034204439490560) 
to allow me to use _his text_ enriched by my examples. 
Where necessary I will also add a few sentences of my own
to explain differences between the original and my code. 

> ## 1 Introduction
>
> Searching for “property-based testing” on Youtube results in a lot of hits. Most of the top 100 consist of talks recorded at developer conferences and meetings, where (mostly) other people than this author present ideas, tools and methods for property-based testing, or applications that make use of it. Clearly, property- based testing is an idea whose time has come. But clearly, it is also poorly understood, requiring explanation over and over again!
>
> We have found that many developers trying property-based testing for the first time find it difficult to identify properties to write—and find the simple examples in tutorials difficult to generalize. This is known as the oracle problem, and it is common to all approaches that use test case generation.
>
> In this paper, therefore, we take a simple—but non-trivial—example of a purely functional data structure, and present five different approaches to writing properties, along with the pitfalls of each to keep in mind. We compare and constrast their effectiveness with the help of eight buggy implementations. We hope that the concrete advice presented here will enable readers to side-step the “where do I start?” question, and quickly derive the benefits that property-based testing has to offer.

