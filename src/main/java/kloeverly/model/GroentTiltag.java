package kloeverly.model;

import java.time.LocalDate;

/**
 * Et grønt tiltag (FR14), fx kompostering eller cykling i stedet for bil.
 * Pointene går til FaellesPointpulje - ikke til beboerens personlige saldo.
 */
public class GroentTiltag extends Opgave
{
  public GroentTiltag(String beskrivelse, LocalDate dato, int point)
  {
    super(beskrivelse, dato, point);
  }
}
