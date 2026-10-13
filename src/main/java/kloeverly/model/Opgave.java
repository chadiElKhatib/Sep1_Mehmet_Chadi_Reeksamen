package kloeverly.model;

import java.time.LocalDate;
import java.io.Serializable;

public abstract class Opgave implements Serializable
{
  private String beskrivelse;
  private LocalDate dato;
  private int point;

  public Opgave(String beskrivelse, LocalDate dato, int point)
  {
    setBeskrivelse(beskrivelse);
    setDato(dato);
    setPoint(point);
  }

  public void setPoint(int point)
  {
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

  @Override public String toString()
  {
    return dato + " - " + beskrivelse + " (" + point + " point)";
  }
}
