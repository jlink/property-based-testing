package pbt.cards;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

/**
 * Motivated by https://www.leadingagile.com/2018/04/step-by-step-toward-property-based-testing/
 * <p>
 * Partially copied from https://github.com/neopragma/java-poker/tree/master/src/main/java/com/neopragma/poker
 */
class PokerProperties {

	@Property
	void all52PossibleCardsAreGenerated(@ForAll("cards") PlayingCard card) {
		System.out.println(card);
	}

	@Provide
	Arbitrary<PlayingCard> cards() {
		Arbitrary<Suit> suit = Arbitraries.of(Suit.class).filter(s -> s != Suit.NONE);
		Arbitrary<Rank> rank = Arbitraries.of(Rank.class)
										  .filter(r -> r != Rank.NONE && r != Rank.JOKER);
		return Combinators.combine(suit, rank).as((s, r) -> new PlayingCard(s, r));
	}

	@Property
	void shuffledDecksAreGenerated(@ForAll("decks") List<PlayingCard> deck) {
		// System.out.println(deck);
		// Statistics.collect(deck.get(0)); // Collect statistics for first card in deck
		Assertions.assertThat(deck).hasSize(52);
		Assertions.assertThat(new HashSet<>(deck)).hasSize(52);
	}

	@Provide
	Arbitrary<List<PlayingCard>> decks() {
		return cards().unique().list().ofSize(52);
	}

	@Property
	void aHandHas5UniqueCards(@ForAll("hands") Hand hand) {
		// System.out.println(hand);
		Assertions.assertThat(hand.show()).hasSize(5);
		Assertions.assertThat(new HashSet<>(hand.show())).hasSize(5);
	}

	@Provide
	Arbitrary<Hand> hands() {
		return decks().map(deck -> new Hand(deck.subList(0, 5)));
	}

	@Property
	void twoHandsDontShareCards(@ForAll("twoHands") Tuple2<Hand, Hand> twoHands) {
		Hand first = twoHands.get1();
		Hand second = twoHands.get2();
		Assertions.assertThat(first.show()).hasSize(5);
		Assertions.assertThat(second.show()).hasSize(5);
		Assertions.assertThat(first.show()).doesNotContainAnyElementsOf(second.show());
	}

	@Provide
	Arbitrary<Tuple2<Hand, Hand>> twoHands() {
		return decks().map(deck -> {
			Hand first = new Hand(deck.subList(0, 5));
			Hand second = new Hand(deck.subList(5, 10));
			return Tuple.of(first, second);
		});
	}

	@Property
	void shuffledDecksAreGenerated_alternative(@ForAll("decks2") List<PlayingCard> deck) {
		Assertions.assertThat(deck).hasSize(52);
		Assertions.assertThat(new HashSet<>(deck)).hasSize(52);
	}

	@Provide
	Arbitrary<List<PlayingCard>> decks2() {
		// The decks() method above only works if random generation for finding unique values
		// of cards does not fail too often. This is not a problem in the
		// current setup but other applications of unique() might suffer from that.
		// The code below provides an alternative approach:

		List<PlayingCard> allCards = cards().allValues()
											.map(stream -> stream.collect(Collectors.toList()))
											.orElseThrow(() -> new RuntimeException("Cannot generate all cards"));
		return Arbitraries.shuffle(allCards);
	}

}
