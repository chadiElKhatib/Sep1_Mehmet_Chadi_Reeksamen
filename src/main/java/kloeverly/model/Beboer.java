package kloeverly.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * En beboer i Kløverly (FR01-FR04) med sine opgaver og personlige point (FR08-FR10, FR18).
 *
 * Den personlige saldo gemmes ikke som et tal. Den beregnes altid som summen af
 * beboerens pointposteringer. Så kan saldo og historik aldrig komme ud af trit.
 *
 * Reglen "saldoen må aldrig blive negativ" (FR10) håndhæves her i domænet,
 * så den gælder uanset hvilken skærm der kalder klassen.
 */
public class Beboer
{
  private String navn;
  private String husnummer;
  private LocalDate fødselsdato;
  private List<Opgave> opgaver;
  private List<Pointpostering> pointposteringer;

  public Beboer(String navn, String husnummer, LocalDate fødselsdato)
  {
    setNavn(navn);
    setHusnummer(husnummer);
    if (fødselsdato == null || fødselsdato.isAfter(LocalDate.now()))
    {
      throw new IllegalArgumentException("Fødselsdato skal vælges og må ikke ligge i fremtiden.");
    }
    this.fødselsdato = fødselsdato;
    this.opgaver = new ArrayList<>();
    this.pointposteringer = new ArrayList<>();
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

  // Setterne bruges af "Rediger beboer" (FR02).
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

  public List<Opgave> getOpgaver()
  {
    return Collections.unmodifiableList(opgaver);
  }

  public List<Pointpostering> getPointposteringer()
  {
    return Collections.unmodifiableList(pointposteringer);
  }

  /**
   * Tilføjer en opgave og laver den tilhørende pointpostering (FR05, FR15, FR16).
   * - Faellesopgave: + point til beboeren
   * - Bytteopgave:   - point fra beboeren (afvises, hvis saldoen er for lav)
   * - GroentTiltag:  ingen personlige point - pointene går til fællespuljen
   */
  public void tilføjOpgave(Opgave opgave)
  {
    if (opgave == null)
    {
      throw new IllegalArgumentException("Opgaven mangler.");
    }

    if (opgave instanceof Faellesopgave)
    {
      pointposteringer.add(new Pointpostering(opgave.getDato(), opgave.getPoint(),
          "Fællesopgave: " + opgave.getBeskrivelse(), opgave));
    }
    else if (opgave instanceof Bytteopgave)
    {
      int saldo = beregnPersonligePoint();
      if (opgave.getPoint() > saldo)
      {
        throw new IllegalArgumentException(navn + " har kun " + saldo
            + " point og kan ikke betale " + opgave.getPoint() + " point.");
      }
      pointposteringer.add(new Pointpostering(opgave.getDato(), -opgave.getPoint(),
          "Bytteopgave: " + opgave.getBeskrivelse(), opgave));
    }

    opgaver.add(opgave);
  }

  /**
   * Sletter en opgave og de point, den gav (FR07).
   * Afvises, hvis saldoen så ville blive negativ - fx hvis pointene allerede er brugt.
   */
  public void fjernOpgave(Opgave opgave)
  {
    if (!opgaver.contains(opgave))
    {
      throw new IllegalArgumentException("Opgaven findes ikke hos " + navn + ".");
    }

    List<Pointpostering> posteringerFraOpgaven = new ArrayList<>();
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
      throw new IllegalArgumentException("Opgaven kan ikke slettes, fordi "
          + navn + "s saldo så ville blive negativ.");
    }

    pointposteringer.removeAll(posteringerFraOpgaven);
    opgaver.remove(opgave);
  }

  public void tilføjPointpostering(Pointpostering postering)
  {
    if (postering == null)
    {
      throw new IllegalArgumentException("Posteringen mangler.");
    }
    if (beregnPersonligePoint() + postering.getAntalPoint() < 0)
    {
      throw new IllegalArgumentException("Posteringen ville give " + navn + " en negativ saldo.");
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

  /**
   * Nulstiller saldoen til 0 (FR18). Historikken bevares: der laves en ny
   * postering med det modsatte beløb, i stedet for at slette de gamle.
   */
  public void nulstilPersonligePoint()
  {
    int saldo = beregnPersonligePoint();
    if (saldo > 0)
    {
      pointposteringer.add(new Pointpostering(LocalDate.now(), -saldo,
          "Nulstilling af personlige point", null));
    }
  }

  @Override
  public String toString()
  {
    return navn + " (hus " + husnummer + ")";
  }
}
