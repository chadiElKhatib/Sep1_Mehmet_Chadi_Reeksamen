package kloeverly.model;

import java.time.LocalDate;

/**
 * En bytteopgave (FR16): beboeren bruger personlige point på en ydelse, fx vinduespudsning.
 * Beskrivelsen er ydelsen, og point er prisen. Pointene trækkes fra beboerens saldo.
 */
public class Bytteopgave extends Opgave
{
  public Bytteopgave(String ydelse, LocalDate dato, int pointPris)
  {
    super(ydelse, dato, pointPris);
  }
}
