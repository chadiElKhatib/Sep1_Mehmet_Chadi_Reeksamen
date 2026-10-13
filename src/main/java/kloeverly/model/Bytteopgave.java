package kloeverly.model;

import java.time.LocalDate;

public class Bytteopgave extends Opgave
{
  public Bytteopgave(String ydelse, LocalDate dato, int pointPris)
  {
    super(ydelse, dato, pointPris);
  }
}
