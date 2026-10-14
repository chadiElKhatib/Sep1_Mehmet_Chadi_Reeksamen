package kloeverly.data;

import kloeverly.model.Beboer;
import kloeverly.model.GroentTiltag;
import kloeverly.model.Opgave;
import kloeverly.model.Pointpostering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Eksporterer data fra programmet til hjemmesiden (FR13, FR17).
 *
 * Java-programmet skriver kun en datafil: web/data.json.
 * Hjemmesiden (index.html og groenne-tiltag.html) læser filen
 * med JavaScript (kloeverly.js) og viser tallene og de grønne tiltag.
 *
 * Filen indeholder ingen navne, husnumre eller personlige saldi pr. beboer,
 * kun samlede tal og grønne tiltag uden navne (NFR06).
 */
public class WebDataManager
{
  private static final Path DATAFIL =
      Path.of("src", "main", "resources", "web", "data.json");

  private static final int MAKS_GROENNE_TILTAG = 20;

  /*
   * Skriver data.json med de aktuelle tal og grønne tiltag.
   * Kaldes når programmet starter, og hver gang data gemmes.
   */
  public static void opdaterHjemmeside()
  {
    int antalBeboere = AppData.getBeboere().size();
    int udfoerteOpgaver = 0;
    int personligePoint = 0;

    for (Beboer beboer : AppData.getBeboere())
    {
      udfoerteOpgaver += beboer.getAntalAfsluttedeOpgaver();
      personligePoint += beboer.beregnPersonligePoint();
    }

    int faellesPoint = AppData.getFaellesPointpulje().beregnSamledePoint();
    List<Opgave> groenneTiltag = findGroenneTiltag();

    StringBuilder json = new StringBuilder();
    json.append("{\n");
    json.append("  \"antalBeboere\": ").append(antalBeboere).append(",\n");
    json.append("  \"udfoerteOpgaver\": ").append(udfoerteOpgaver).append(",\n");
    json.append("  \"personligePoint\": ").append(personligePoint).append(",\n");
    json.append("  \"faellesPoint\": ").append(faellesPoint).append(",\n");
    json.append("  \"groenneTiltag\": [");

    for (int i = 0; i < groenneTiltag.size(); i++)
    {
      Opgave tiltag = groenneTiltag.get(i);
      json.append(i == 0 ? "\n" : ",\n");
      json.append("    { \"beskrivelse\": \"")
          .append(escapeJson(tiltag.getBeskrivelse()))
          .append("\", \"dato\": \"")
          .append(tiltag.getDato())
          .append("\", \"point\": ")
          .append(tiltag.getPoint())
          .append(" }");
    }

    json.append(groenneTiltag.isEmpty() ? "]\n" : "\n  ]\n");
    json.append("}\n");

    try
    {
      Files.writeString(DATAFIL, json.toString(), StandardCharsets.UTF_8);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /*
   * Bevares, så de eksisterende kald i OpgaveController virker uændret.
   * Alle data ligger nu i én fil, så hele filen skrives igen.
   */
  public static void opdaterGroenneTiltag()
  {
    opdaterHjemmeside();
  }

  /*
   * Finder de afsluttede grønne tiltag i fællespuljen.
   * Nyeste først, samme beskrivelse kun én gang, højst 20.
   */
  private static List<Opgave> findGroenneTiltag()
  {
    List<Opgave> alle = new ArrayList<>();

    for (Pointpostering postering :
        AppData.getFaellesPointpulje().getPointposteringer())
    {
      if (postering.getOpgave() instanceof GroentTiltag)
      {
        alle.add(postering.getOpgave());
      }
    }

    alle.sort((a, b) -> b.getDato().compareTo(a.getDato()));

    List<Opgave> resultat = new ArrayList<>();
    Set<String> visteBeskrivelser = new HashSet<>();

    for (Opgave tiltag : alle)
    {
      String noegle = tiltag.getBeskrivelse().trim().toLowerCase();

      if (resultat.size() >= MAKS_GROENNE_TILTAG)
      {
        break;
      }
      if (visteBeskrivelser.add(noegle))
      {
        resultat.add(tiltag);
      }
    }
    return resultat;
  }

  /*
   * Gør tekst sikker at sætte ind i JSON,
   * fx hvis en beskrivelse indeholder anførselstegn.
   */
  private static String escapeJson(String tekst)
  {
    StringBuilder resultat = new StringBuilder();

    for (char tegn : tekst.toCharArray())
    {
      switch (tegn)
      {
        case '"' -> resultat.append("\\\"");
        case '\\' -> resultat.append("\\\\");
        case '\n' -> resultat.append("\\n");
        case '\r' -> resultat.append("\\r");
        case '\t' -> resultat.append("\\t");
        default ->
        {
          if (tegn < 0x20)
          {
            resultat.append(String.format("\\u%04x", (int) tegn));
          }
          else
          {
            resultat.append(tegn);
          }
        }
      }
    }
    return resultat.toString();
  }
}
