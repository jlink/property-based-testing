package pbt.cards;

import java.io.*;

public class PlayingCard implements Comparable<PlayingCard>, Serializable {

	private Rank rank;
	private Suit suit;

	public Suit suit() {
		return suit;
	}

	public Rank rank() {
		return rank;
	}

	public PlayingCard(Suit suit, Rank rank) {
		this.suit = suit;
		this.rank = rank;
	}

	@Override
	public String toString() {
		return String.format("%s of %s", rank, suit);
	}

	@Override
	public int compareTo(PlayingCard o) {
		int suitCompare = suit.compareTo(o.suit);
		if (suitCompare == 0) {
			return rank.compareTo(o.rank);
		} else {
			return suitCompare;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PlayingCard that = (PlayingCard) o;
		return this.compareTo(that) == 0;
	}

	@Override
	public int hashCode() {
		int result = rank.hashCode();
		result = 31 * result + suit.hashCode();
		return result;
	}
}