package kloeverly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester Beboer-domænelogik:
 * personlige point, bytteopgaver, nulstilling
 * og registrering af afsluttede opgaver.
 */
class BeboerTest
{
  private Beboer hanne;
  private LocalDate idag;

  @BeforeEach
  void setUp()
  {
    hanne = new Beboer(
        "Hanne",
        "12",
        LocalDate.of(1980, 5, 1)
    );

    idag = LocalDate.now();
  }

  @Test
  void nyBeboerHarNulPoint()
  {
    assertEquals(0, hanne.beregnPersonligePoint());
  }

  @Test
  void faellesopgaveGiverPointVedAfslutning()
  {
    Faellesopgave opgave =
        new Faellesopgave("Slå græs", idag, 20);

    Pointpostering postering =
        new Pointpostering(
            idag,
            20,
            "Fællesopgave: Slå græs",
            opgave
        );

    hanne.tilføjPointpostering(postering);
    hanne.registrerAfsluttetOpgave();

    assertEquals(20, hanne.beregnPersonligePoint());
    assertEquals(1, hanne.getAntalAfsluttedeOpgaver());
  }

  @Test
  void bytteopgaveTraekkerPointVedOprettelse()
  {
    hanne.tilføjPointpostering(
        new Pointpostering(
            idag,
            20,
            "Manuel pointkorrektion",
            null
        )
    );

    Bytteopgave bytteopgave =
        new Bytteopgave(
            "Vinduespudsning",
            idag,
            15
        );

    hanne.tilføjOpgave(bytteopgave);

    assertEquals(5, hanne.beregnPersonligePoint());
    assertTrue(hanne.getOpgaver().contains(bytteopgave));
  }

  @Test
  void bytteopgaveAfvisesVedForFaaPoint()
  {
    hanne.tilføjPointpostering(
        new Pointpostering(
            idag,
            10,
            "Manuel pointkorrektion",
            null
        )
    );

    Bytteopgave bytteopgave =
        new Bytteopgave(
            "Vinduespudsning",
            idag,
            15
        );

    assertThrows(
        IllegalArgumentException.class,
        () -> hanne.tilføjOpgave(bytteopgave)
    );

    assertEquals(10, hanne.beregnPersonligePoint());
    assertTrue(hanne.getOpgaver().isEmpty());
  }

  @Test
  void sletningAfBytteopgaveTilbagefoererPoint()
  {
    hanne.tilføjPointpostering(
        new Pointpostering(
            idag,
            20,
            "Manuel pointkorrektion",
            null
        )
    );

    Bytteopgave bytteopgave =
        new Bytteopgave(
            "Vinduespudsning",
            idag,
            15
        );

    hanne.tilføjOpgave(bytteopgave);

    assertEquals(5, hanne.beregnPersonligePoint());

    hanne.fjernOpgave(bytteopgave);

    assertEquals(20, hanne.beregnPersonligePoint());
    assertTrue(hanne.getOpgaver().isEmpty());
  }

  @Test
  void afsluttetBytteopgaveBeholderBetalingen()
  {
    hanne.tilføjPointpostering(
        new Pointpostering(
            idag,
            20,
            "Manuel pointkorrektion",
            null
        )
    );

    Bytteopgave bytteopgave =
        new Bytteopgave(
            "Vinduespudsning",
            idag,
            15
        );

    hanne.tilføjOpgave(bytteopgave);

    assertEquals(5, hanne.beregnPersonligePoint());

    hanne.afslutOpgave(bytteopgave);

    assertEquals(5, hanne.beregnPersonligePoint());
    assertTrue(hanne.getOpgaver().isEmpty());
  }

  @Test
  void nulstillingGiverNulOgBevarerHistorik()
  {
    hanne.tilføjPointpostering(
        new Pointpostering(
            idag,
            20,
            "Manuel pointkorrektion",
            null
        )
    );

    hanne.nulstilPersonligePoint();

    assertEquals(0, hanne.beregnPersonligePoint());
    assertEquals(2, hanne.getPointposteringer().size());
  }

  @Test
  void negativPointposteringAfvisesHvisSaldoBliverNegativ()
  {
    Pointpostering postering =
        new Pointpostering(
            idag,
            -10,
            "Manuel pointkorrektion",
            null
        );

    assertThrows(
        IllegalArgumentException.class,
        () -> hanne.tilføjPointpostering(postering)
    );

    assertEquals(0, hanne.beregnPersonligePoint());
  }

  @Test
  void registrerAfsluttetOpgaveOegerAntal()
  {
    assertEquals(0, hanne.getAntalAfsluttedeOpgaver());

    hanne.registrerAfsluttetOpgave();

    assertEquals(1, hanne.getAntalAfsluttedeOpgaver());
  }

  @Test
  void opgaveMedNulPointAfvises()
  {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Faellesopgave(
            "Slå græs",
            idag,
            0
        )
    );
  }

  @Test
  void beboerUdenNavnAfvises()
  {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Beboer(
            "  ",
            "12",
            LocalDate.of(1980, 5, 1)
        )
    );
  }
}