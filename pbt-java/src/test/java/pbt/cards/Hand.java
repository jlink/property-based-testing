package pbt.cards;

import java.io.*;
import java.util.*;

public class Hand implements Serializable {

	private List<PlayingCard> cards;

	public Hand(List<PlayingCard> cards) {
		this.cards = cards;
	}

	public List<PlayingCard> show() {
		return cards;
	}

	public void sort() {
		cards.sort(PlayingCard::compareTo);
	}

	@Override
	public String toString() {
		return cards.toString();
	}
}