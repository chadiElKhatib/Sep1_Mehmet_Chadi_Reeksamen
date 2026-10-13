package kloeverly.model;

import java.time.LocalDate;

public class Faellesopgave extends Opgave
{
  public Faellesopgave(String beskrivelse, LocalDate dato, int point)
  {
    super(beskrivelse, dato, point);
  }
}
