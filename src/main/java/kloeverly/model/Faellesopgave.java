package kloeverly.model;

import java.time.LocalDate;

/**
 * En fællesopgave (FR15), fx græsslåning eller oprydning.
 * Giver beboeren personlige point.
 */
public class Faellesopgave extends Opgave
{
  public Faellesopgave(String beskrivelse, LocalDate dato, int point)
  {
    super(beskrivelse, dato, point);
  }
}
