package kloeverly.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tester reglerne for personlige point (FR10, FR15, FR16, FR18, FR07).
 */
class BeboerTest
{
  private Beboer hanne;
  private LocalDate idag;

  @BeforeEach
  void setUp()
  {
    hanne = new Beboer("Hanne", "12", LocalDate.of(1980, 5, 1));
    idag = LocalDate.now();
  }

  @Test
  void nyBeboerHarNulPoint()
  {
    assertEquals(0, hanne.beregnPersonligePoint());
  }

  @Test
  void faellesopgaveGiverPersonligePoint()
  {
    hanne.tilføjOpgave(new Faellesopgave("Slå græs", idag, 20));
    assertEquals(20, hanne.beregnPersonligePoint());
  }

  @Test
  void bytteopgaveTraekkerPoint()
  {
    hanne.tilføjOpgave(new Faellesopgave("Slå græs", idag, 20));
    hanne.tilføjOpgave(new Bytteopgave("Vinduespudsning", idag, 15));
    assertEquals(5, hanne.beregnPersonligePoint());
  }

  @Test
  void bytteopgaveAfvisesVedForFaaPoint()
  {
    hanne.tilføjOpgave(new Faellesopgave("Slå græs", idag, 10));
    assertThrows(IllegalArgumentException.class,
        () -> hanne.tilføjOpgave(new Bytteopgave("Vinduespudsning", idag, 15)));
    assertEquals(10, hanne.beregnPersonligePoint());
    assertEquals(1, hanne.getOpgaver().size());
  }

  @Test
  void groentTiltagGiverIkkePersonligePoint()
  {
    hanne.tilføjOpgave(new GroentTiltag("Kompostering", idag, 30));
    assertEquals(0, hanne.beregnPersonligePoint());
    assertEquals(1, hanne.getOpgaver().size());
  }

  @Test
  void sletningFjernerOpgavensPoint()
  {
    Opgave graes = new Faellesopgave("Slå græs", idag, 20);
    hanne.tilføjOpgave(graes);
    hanne.fjernOpgave(graes);
    assertEquals(0, hanne.beregnPersonligePoint());
    assertTrue(hanne.getOpgaver().isEmpty());
  }

  @Test
  void sletningAfvisesHvisSaldoBliverNegativ()
  {
    Opgave graes = new Faellesopgave("Slå græs", idag, 20);
    hanne.tilføjOpgave(graes);
    hanne.tilføjOpgave(new Bytteopgave("Vinduespudsning", idag, 15));
    assertThrows(IllegalArgumentException.class, () -> hanne.fjernOpgave(graes));
    assertEquals(5, hanne.beregnPersonligePoint());
  }

  @Test
  void nulstillingGiverNulOgBevarerHistorik()
  {
    hanne.tilføjOpgave(new Faellesopgave("Slå græs", idag, 20));
    hanne.nulstilPersonligePoint();
    assertEquals(0, hanne.beregnPersonligePoint());
    assertEquals(2, hanne.getPointposteringer().size());
  }

  @Test
  void opgaveMedNulPointAfvises()
  {
    assertThrows(IllegalArgumentException.class,
        () -> new Faellesopgave("Slå græs", idag, 0));
  }

  @Test
  void beboerUdenNavnAfvises()
  {
    assertThrows(IllegalArgumentException.class,
        () -> new Beboer("  ", "12", LocalDate.of(1980, 5, 1)));
  }
}