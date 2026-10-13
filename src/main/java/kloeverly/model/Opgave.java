package kloeverly.model;

import java.time.LocalDate;

/**
 * Fælles superklasse for de tre opgavetyper: GroentTiltag, Faellesopgave og Bytteopgave (FR14-FR16).
 *
 * Klassen er abstrakt, fordi en opgave altid er én af de tre typer.
 * Man kan derfor ikke skrive "new Opgave(...)" - kun "new Faellesopgave(...)" osv.
 *
 * Point er altid et positivt tal. Det er Beboer, der afgør, om pointene lægges til
 * (Faellesopgave) eller trækkes fra (Bytteopgave).
 */
public abstract class Opgave
{
  private String beskrivelse;
  private LocalDate dato;
  private int point;

  public Opgave(String beskrivelse, LocalDate dato, int point)
  {
    setBeskrivelse(beskrivelse);
    setDato(dato);
    if (point <= 0)
    {
      throw new IllegalArgumentException("Point skal være et positivt tal.");
    }
    this.point = point;
  }

  public String getBeskrivelse()
  {
    return beskrivelse;
  }

  public LocalDate getDato()
  {
    return dato;
  }

  public int getPoint()
  {
    return point;
  }

  public void setBeskrivelse(String beskrivelse)
  {
    if (beskrivelse == null || beskrivelse.isBlank())
    {
      throw new IllegalArgumentException("Beskrivelse skal udfyldes.");
    }
    this.beskrivelse = beskrivelse.trim();
  }

  public void setDato(LocalDate dato)
  {
    if (dato == null)
    {
      throw new IllegalArgumentException("Dato skal vælges.");
    }
    this.dato = dato;
  }

  // Der er bevidst ingen setPoint(): når en opgave har givet point, ligger de i en
  // Pointpostering. Skal pointene ændres, slettes opgaven og oprettes på ny.

  @Override
  public String toString()
  {
    return dato + " - " + beskrivelse + " (" + point + " point)";
  }
}
