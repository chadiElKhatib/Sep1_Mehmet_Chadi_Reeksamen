package kloeverly.data;

import kloeverly.model.Beboer;
import kloeverly.model.GroentTiltag;
import kloeverly.model.Opgave;
import kloeverly.model.Pointpostering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class WebDataManager
{
  /*
   * Opdaterer hele hjemmesiden.
   *
   * Forsiden får de aktuelle tal fra systemet,
   * og siden med grønne tiltag bliver genereret
   * ud fra de afsluttede grønne tiltag.
   */
  public static void opdaterHjemmeside()
  {
    opdaterForside();
    opdaterGroenneTiltag();
  }


  // ==================================================
  // FORSIDE
  // ==================================================

  private static void opdaterForside()
  {
    int antalBeboere =
        AppData.getBeboere().size();

    int udfoerteOpgaver = 0;
    int personligePoint = 0;
    int faellesPoint = 0;


    /*
     * Beregn udførte opgaver
     * og personlige point.
     */
    for (Beboer beboer : AppData.getBeboere())
    {
      udfoerteOpgaver +=
          beboer.getAntalAfsluttedeOpgaver();

      personligePoint +=
          beboer.beregnPersonligePoint();
    }


    /*
     * Beregn den fælles pointpulje.
     */
    for (Pointpostering postering :
        AppData.getFaellesPointpulje()
            .getPointposteringer())
    {
      faellesPoint +=
          postering.getAntalPoint();
    }


    String html = String.format(
        """
        <!DOCTYPE html>

        <html lang="da">

        <head>

            <meta charset="UTF-8">

            <meta
                name="viewport"
                content="width=device-width, initial-scale=1.0"
            >

            <title>Kløverly</title>

            <link
                rel="stylesheet"
                href="style.css"
            >

        </head>


        <body id="top">


        <!-- =========================
             MENU
        ========================= -->

        <header class="topbar">

            <div class="container">

                <a
                    href="#top"
                    class="logo"
                >
                    Kløverly
                </a>


                <nav class="nav-links">

                    <a href="#top">
                        Forside
                    </a>

                    <a href="#om">
                        Om Kløverly
                    </a>

                    <a href="#hvordan">
                        Sådan virker det
                    </a>

                    <a href="groenne-tiltag.html">
                        Grønne tiltag
                    </a>

                </nav>

            </div>

        </header>


        <!-- =========================
             HERO
        ========================= -->

        <section class="hero">

            <div class="container hero-grid">

                <div>

                    <h1>
                        Et grønt og aktivt bofællesskab
                    </h1>

                    <p>
                        Kløverly er et bofællesskab,
                        hvor beboerne hjælper hinanden,
                        udfører opgaver og bidrager til
                        grønne initiativer gennem et
                        fælles pointsystem.
                    </p>


                    <div class="hero-buttons">

                        <a
                            href="groenne-tiltag.html"
                            class="btn btn-primary"
                        >
                            Se grønne tiltag
                        </a>

                        <a
                            href="#hvordan"
                            class="btn btn-secondary"
                        >
                            Læs mere
                        </a>

                    </div>

                </div>


                <div class="hero-card">

                    <h3>
                        Hvad kan man i Kløverly?
                    </h3>

                    <ul>

                        <li>
                            Udføre fællesopgaver
                            i bofællesskabet
                        </li>

                        <li>
                            Deltage i grønne tiltag
                            og inspiration
                        </li>

                        <li>
                            Bruge og optjene point
                            gennem aktiviteter
                        </li>

                        <li>
                            Skabe overblik over
                            fællesskabets udvikling
                        </li>

                    </ul>

                </div>

            </div>

        </section>


        <!-- =========================
             DYNAMISKE TAL
        ========================= -->

        <section class="stats">

            <div class="container stats-grid">


                <div class="stat-card">

                    <h3>%d</h3>

                    <p>
                        Beboere i systemet
                    </p>

                </div>


                <div class="stat-card">

                    <h3>%d</h3>

                    <p>
                        Udførte opgaver
                    </p>

                </div>


                <div class="stat-card">

                    <h3>%d</h3>

                    <p>
                        Personlige point i alt
                    </p>

                </div>


                <div class="stat-card">

                    <h3>%d</h3>

                    <p>
                        Point i fællespuljen
                    </p>

                </div>

            </div>

        </section>


        <!-- =========================
             OM KLØVERLY
        ========================= -->

        <section
            class="section"
            id="om"
        >

            <div class="container">

                <h2 class="section-title">
                    Velkommen til Kløverly
                </h2>

                <p class="section-subtitle">

                    Kløverly gør det nemt at holde styr
                    på beboere, opgaver og point.

                    Systemet hjælper med at skabe
                    overblik og styrke samarbejdet
                    i hverdagen.

                </p>


                <div class="card-grid">


                    <div class="info-card">

                        <h3>
                            Beboere
                        </h3>

                        <p>
                            Hver beboer kan registreres
                            med navn, husnummer og
                            pointaktiviteter.
                        </p>

                    </div>


                    <div class="info-card">

                        <h3>
                            Opgaver
                        </h3>

                        <p>
                            Fællesopgaver, grønne tiltag
                            og bytteopgaver kan oprettes
                            og afsluttes direkte i systemet.
                        </p>

                    </div>


                    <div class="info-card">

                        <h3>
                            Pointsystem
                        </h3>

                        <p>
                            Systemet holder styr på
                            personlige point og Kløverlys
                            fælles pointpulje.
                        </p>

                    </div>

                </div>

            </div>

        </section>


        <!-- =========================
             SÅDAN VIRKER DET
        ========================= -->

        <section
            class="section"
            id="hvordan"
        >

            <div class="container">

                <h2 class="section-title">
                    Sådan virker det
                </h2>

                <p class="section-subtitle">

                    Kløverly bruger tre forskellige
                    typer opgaver til at organisere
                    aktiviteter i bofællesskabet.

                </p>


                <div class="card-grid">


                    <!-- FÆLLESOPGAVE -->

                    <div class="info-card">

                        <h3>
                            1. Fællesopgaver
                        </h3>

                        <p>
                            En fællesopgave oprettes
                            uden en bestemt beboer.
                        </p>

                        <p>
                            Når opgaven afsluttes,
                            vælges den beboer der
                            udførte opgaven.
                        </p>

                        <p>
                            Beboeren modtager
                            personlige point.
                        </p>

                    </div>


                    <!-- BYTTEOPGAVE -->

                    <div class="info-card">

                        <h3>
                            2. Bytteopgaver
                        </h3>

                        <p>
                            En beboer kan bruge sine
                            personlige point på at få
                            hjælp fra en anden beboer.
                        </p>

                        <p>
                            Bestilleren betaler point,
                            og udføreren modtager
                            pointene.
                        </p>

                    </div>


                    <!-- GRØNT TILTAG -->

                    <div class="info-card">

                        <h3>
                            3. Grønne tiltag
                        </h3>

                        <p>
                            Grønne aktiviteter kan
                            registreres som grønne
                            tiltag.
                        </p>

                        <p>
                            Når tiltaget afsluttes,
                            gives point til Kløverlys
                            fælles pointpulje.
                        </p>

                        <p>
                            Tiltaget bliver derefter
                            vist anonymt på hjemmesiden.
                        </p>

                    </div>

                </div>

            </div>

        </section>


        <!-- =========================
             GRØN INSPIRATION
        ========================= -->

        <section class="section">

            <div class="container">

                <div class="cta-box">

                    <div>

                        <h2>
                            Se grøn inspiration fra Kløverly
                        </h2>

                        <p>
                            Se de grønne aktiviteter,
                            som er blevet gennemført
                            i bofællesskabet.
                        </p>

                    </div>


                    <a
                        href="groenne-tiltag.html"
                        class="btn btn-primary"
                    >
                        Gå til grønne tiltag
                    </a>

                </div>

            </div>

        </section>


        <!-- =========================
             FOOTER
        ========================= -->

        <footer>

            <div class="container">

                Kløverly •
                Administrationssystem for bofællesskab

            </div>

        </footer>


        </body>

        </html>
        """,

        antalBeboere,
        udfoerteOpgaver,
        personligePoint,
        faellesPoint
    );


    Path fil =
        Path.of(
            "src",
            "main",
            "resources",
            "web",
            "index.html"
        );


    try
    {
      Files.writeString(
          fil,
          html,
          StandardCharsets.UTF_8
      );
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }


  // ==================================================
  // GRØNNE TILTAG
  // ==================================================

  public static void opdaterGroenneTiltag()
  {
    List<Pointpostering> groennePosteringer =
        new ArrayList<>();


    /*
     * Find afsluttede grønne tiltag.
     *
     * Et grønt tiltag bliver først lagt i
     * fællespuljen, når det er afsluttet.
     */
    for (Pointpostering postering :
        AppData.getFaellesPointpulje()
            .getPointposteringer())
    {
      Opgave opgave =
          postering.getOpgave();

      if (opgave instanceof GroentTiltag)
      {
        groennePosteringer.add(postering);
      }
    }


    /*
     * Nyeste grønne tiltag først.
     */
    groennePosteringer.sort(
        (a, b) ->
            b.getOpgave()
                .getDato()
                .compareTo(
                    a.getOpgave()
                        .getDato()
                )
    );


    /*
     * Beregn den samlede fælles pointpulje.
     */
    int faellesPoint = 0;

    for (Pointpostering postering :
        AppData.getFaellesPointpulje()
            .getPointposteringer())
    {
      faellesPoint +=
          postering.getAntalPoint();
    }


    StringBuilder tiltagHtml =
        new StringBuilder();

    Set<String> visteTiltag =
        new HashSet<>();

    int antalViste = 0;

    int maksimum = 20;


    DateTimeFormatter datoFormat =
        DateTimeFormatter.ofPattern(
            "d. MMMM yyyy",
            Locale.forLanguageTag("da-DK")
        );


    /*
     * Generer kortene til hjemmesiden.
     */
    for (Pointpostering postering :
        groennePosteringer)
    {
      Opgave opgave =
          postering.getOpgave();


      String noegle =
          opgave.getBeskrivelse()
              .trim()
              .toLowerCase();


      /*
       * Samme grønne tiltag vises
       * ikke flere gange.
       */
      if (visteTiltag.contains(noegle))
      {
        continue;
      }


      if (antalViste >= maksimum)
      {
        break;
      }


      visteTiltag.add(noegle);

      antalViste++;


      String dato =
          opgave.getDato()
              .format(datoFormat);


      tiltagHtml.append(
          """
          <article class="tiltag-card">

              <span class="tiltag-badge">
                  GRØNT TILTAG
              </span>

              <h3>
          """
      );


      tiltagHtml.append(
          escapeHtml(
              opgave.getBeskrivelse()
          )
      );


      tiltagHtml.append(
          """
              </h3>


              <div class="tiltag-info">


                  <p>

                      <strong>
                          Dato
                      </strong>

                      <span>
          """
      );


      tiltagHtml.append(dato);


      tiltagHtml.append(
          """
                      </span>

                  </p>


                  <p>

                      <strong>
                          Bidrag til fællesskabet
                      </strong>

                      <span class="point-value">
                          +
          """
      );


      tiltagHtml.append(
          opgave.getPoint()
      );


      tiltagHtml.append(
          """
                          point
                      </span>

                  </p>


              </div>

          </article>
          """
      );
    }


    /*
     * Hvis der ikke findes nogen
     * afsluttede grønne tiltag.
     */
    if (antalViste == 0)
    {
      tiltagHtml.append(
          """
          <div class="empty-box">

              <h3>
                  Ingen grønne tiltag endnu
              </h3>

              <p>
                  Når Kløverly gennemfører
                  et grønt tiltag, vil det
                  automatisk blive vist her.
              </p>

          </div>
          """
      );
    }


    String html =
        String.format(
            """
            <!DOCTYPE html>

            <html lang="da">

            <head>

                <meta charset="UTF-8">

                <meta
                    name="viewport"
                    content="width=device-width, initial-scale=1.0"
                >

                <title>
                    Grønne tiltag - Kløverly
                </title>

                <link
                    rel="stylesheet"
                    href="style.css"
                >

            </head>


            <body class="green-page">


            <!-- =========================
                 MENU
            ========================= -->

            <header class="topbar">

                <div class="container">

                    <a
                        href="index.html"
                        class="logo"
                    >
                        Kløverly
                    </a>


                    <nav class="nav-links">

                        <a href="index.html">
                            Forside
                        </a>

                        <a href="index.html#om">
                            Om Kløverly
                        </a>

                        <a href="index.html#hvordan">
                            Sådan virker det
                        </a>

                        <a href="groenne-tiltag.html">
                            Grønne tiltag
                        </a>

                    </nav>

                </div>

            </header>


            <!-- =========================
                 HERO
            ========================= -->

            <section class="page-header">

                <div class="container">

                    <span class="page-label">
                        KLØVERLY
                    </span>

                    <h1>
                        Grøn inspiration
                    </h1>

                    <p>
                        Se de grønne aktiviteter,
                        der er gennemført i
                        Kløverlys fællesskab.
                    </p>

                </div>

            </section>


            <!-- =========================
                 INDHOLD
            ========================= -->

            <main>

                <div class="container">


                    <!-- OVERSIGT -->

                    <section class="green-summary">


                        <div class="green-summary-card">

                            <span class="summary-number">
                                %d
                            </span>

                            <span class="summary-label">
                                Grønne tiltag
                            </span>

                        </div>


                        <div class="green-summary-card">

                            <span class="summary-number">
                                %d
                            </span>

                            <span class="summary-label">
                                Point i fællespuljen
                            </span>

                        </div>


                    </section>


                    <!-- TILTAG -->

                    <section class="content-box">


                        <div class="green-section-heading">

                            <div>

                                <span class="section-kicker">
                                    FÆLLESSKABET
                                </span>

                                <h2>
                                    Seneste grønne tiltag
                                </h2>

                                <p>
                                    Tiltagene vises anonymt.
                                    Formålet er at dele
                                    inspiration og synliggøre
                                    Kløverlys grønne indsats.
                                </p>

                            </div>

                        </div>


                        <div class="tiltag-grid">

                            %s

                        </div>


                        <a
                            href="index.html"
                            class="back-link"
                        >
                            ← Tilbage til forsiden
                        </a>


                    </section>

                </div>

            </main>


            <!-- =========================
                 FOOTER
            ========================= -->

            <footer>

                <div class="container">

                    Kløverly •
                    Et grønt og aktivt bofællesskab

                </div>

            </footer>


            </body>

            </html>
            """,

            antalViste,
            faellesPoint,
            tiltagHtml.toString()
        );


    Path fil =
        Path.of(
            "src",
            "main",
            "resources",
            "web",
            "groenne-tiltag.html"
        );


    try
    {
      Files.writeString(
          fil,
          html,
          StandardCharsets.UTF_8
      );
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }


  // ==================================================
  // HTML-BESKYTTELSE
  // ==================================================

  private static String escapeHtml(
      String tekst
  )
  {
    return tekst
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;");
  }
}