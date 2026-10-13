package kloeverly.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Beboer implements Serializable
{
  private static final long serialVersionUID = 1L;

  private String navn;
  private String husnummer;
  private LocalDate fødselsdato;
  private List<Opgave> opgaver;
  private List<Pointpostering> pointposteringer;
  private int antalAfsluttedeOpgaver;

  public Beboer(String navn, String husnummer, LocalDate fødselsdato)
  {
    setNavn(navn);
    setHusnummer(husnummer);
    setFødselsdato(fødselsdato);

    this.opgaver = new ArrayList<>();
    this.pointposteringer = new ArrayList<>();
    this.antalAfsluttedeOpgaver = 0;
  }

  public String getNavn()
  {
    return navn;
  }

  public String getHusnummer()
  {
    return husnummer;
  }

  public LocalDate getFødselsdato()
  {
    return fødselsdato;
  }

  public int getAntalAfsluttedeOpgaver()
  {
    return antalAfsluttedeOpgaver;
  }

  public void setNavn(String navn)
  {
    if (navn == null || navn.isBlank())
    {
      throw new IllegalArgumentException("Navn skal udfyldes.");
    }

    this.navn = navn.trim();
  }

  public void setHusnummer(String husnummer)
  {
    if (husnummer == null || husnummer.isBlank())
    {
      throw new IllegalArgumentException("Husnummer skal udfyldes.");
    }

    this.husnummer = husnummer.trim();
  }

  public void setFødselsdato(LocalDate fødselsdato)
  {
    if (fødselsdato == null ||
        fødselsdato.isAfter(LocalDate.now()))
    {
      throw new IllegalArgumentException(
          "Fødselsdato skal vælges og må ikke ligge i fremtiden."
      );
    }

    this.fødselsdato = fødselsdato;
  }

  public List<Opgave> getOpgaver()
  {
    return Collections.unmodifiableList(opgaver);
  }

  public List<Pointpostering> getPointposteringer()
  {
    return Collections.unmodifiableList(pointposteringer);
  }

  public void tilføjOpgave(Opgave opgave)
  {
    if (opgave == null)
    {
      throw new IllegalArgumentException("Opgaven mangler.");
    }

    // Bytteopgave koster personlige point
    if (opgave instanceof Bytteopgave)
    {
      int saldo = beregnPersonligePoint();

      if (opgave.getPoint() > saldo)
      {
        throw new IllegalArgumentException(
            navn + " har kun " + saldo
                + " point og kan ikke betale "
                + opgave.getPoint() + " point."
        );
      }

      pointposteringer.add(
          new Pointpostering(
              opgave.getDato(),
              -opgave.getPoint(),
              "Bytteopgave: " + opgave.getBeskrivelse(),
              opgave
          )
      );
    }

    opgaver.add(opgave);
  }

  public void afslutOpgave(Opgave opgave)
  {
    if (opgave == null || !opgaver.contains(opgave))
    {
      throw new IllegalArgumentException("Opgaven findes ikke.");
    }

    opgaver.remove(opgave);
  }

  public void registrerAfsluttetOpgave()
  {
    antalAfsluttedeOpgaver++;
  }

  public void fjernOpgave(Opgave opgave)
  {
    if (!opgaver.contains(opgave))
    {
      throw new IllegalArgumentException(
          "Opgaven findes ikke hos " + navn + "."
      );
    }

    List<Pointpostering> posteringerFraOpgaven =
        new ArrayList<>();

    int pointFraOpgaven = 0;

    for (Pointpostering postering : pointposteringer)
    {
      if (postering.getOpgave() == opgave)
      {
        posteringerFraOpgaven.add(postering);
        pointFraOpgaven += postering.getAntalPoint();
      }
    }

    if (beregnPersonligePoint() - pointFraOpgaven < 0)
    {
      throw new IllegalArgumentException(
          "Opgaven kan ikke slettes, fordi "
              + navn + "s saldo så ville blive negativ."
      );
    }

    pointposteringer.removeAll(posteringerFraOpgaven);
    opgaver.remove(opgave);
  }

  public void tilføjPointpostering(Pointpostering postering)
  {
    if (postering == null)
    {
      throw new IllegalArgumentException(
          "Posteringen mangler."
      );
    }

    if (beregnPersonligePoint()
        + postering.getAntalPoint() < 0)
    {
      throw new IllegalArgumentException(
          "Posteringen ville give "
              + navn + " en negativ saldo."
      );
    }

    pointposteringer.add(postering);
  }

  public int beregnPersonligePoint()
  {
    int sum = 0;

    for (Pointpostering postering : pointposteringer)
    {
      sum += postering.getAntalPoint();
    }

    return sum;
  }

  public void nulstilPersonligePoint()
  {
    int saldo = beregnPersonligePoint();

    if (saldo > 0)
    {
      pointposteringer.add(
          new Pointpostering(
              LocalDate.now(),
              -saldo,
              "Nulstilling af personlige point",
              null
          )
      );
    }
  }

  @Override
  public String toString()
  {
    return navn + " (hus " + husnummer + ")";
  }
}