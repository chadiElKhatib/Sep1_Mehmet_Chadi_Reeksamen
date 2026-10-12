package kloeverly.model;

import java.time.LocalDate;

/**
 * Én ændring af point - som en linje i et kontoudtog.
 * Positivt antalPoint = point optjent, negativt = point brugt eller nulstillet.
 *
 * Feltet opgave er associationen "medfører" fra klassediagrammet: den opgave,
 * der gav posteringen. Det er null, når posteringen ikke kommer fra en opgave
 * (fx ved nulstilling, FR18).
 */
public class Pointpostering
{
  private LocalDate dato;
  private int antalPoint;
  private String årsag;
  private Opgave opgave;

  public Pointpostering(LocalDate dato, int antalPoint, String årsag, Opgave opgave)
  {
    if (dato == null)
    {
      throw new IllegalArgumentException("En postering skal have en dato.");
    }
    if (årsag == null || årsag.isBlank())
    {
      throw new IllegalArgumentException("En postering skal have en årsag.");
    }
    this.dato = dato;
    this.antalPoint = antalPoint;
    this.årsag = årsag;
    this.opgave = opgave;
  }

  public LocalDate getDato()
  {
    return dato;
  }

  public int getAntalPoint()
  {
    return antalPoint;
  }

  public String getÅrsag()
  {
    return årsag;
  }

  public Opgave getOpgave()
  {
    return opgave;
  }

  @Override
  public String toString()
  {
    return dato + ": " + antalPoint + " point - " + årsag;
  }
}
