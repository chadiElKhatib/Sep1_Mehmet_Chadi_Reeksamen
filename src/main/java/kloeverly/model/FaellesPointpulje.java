package kloeverly.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kløverlys fælles pointpulje (FR11, FR14).
 * Modtager point fra grønne tiltag. Puljen kan kun vokse.
 */
public class FaellesPointpulje
{
  private List<Pointpostering> pointposteringer;

  public FaellesPointpulje()
  {
    pointposteringer = new ArrayList<>();
  }

  public void tilføjPointpostering(Pointpostering postering)
  {
    if (postering == null)
    {
      throw new IllegalArgumentException("Posteringen mangler.");
    }
    if (postering.getAntalPoint() <= 0)
    {
      throw new IllegalArgumentException("Fællespuljen kan kun modtage positive point.");
    }
    pointposteringer.add(postering);
  }

  public int beregnSamledePoint()
  {
    int sum = 0;
    for (Pointpostering postering : pointposteringer)
    {
      sum += postering.getAntalPoint();
    }
    return sum;
  }

  /**
   * Returnerer en liste, der ikke kan ændres udefra.
   * Så kan ingen komme uden om reglerne i tilføjPointpostering().
   */
  public List<Pointpostering> getPointposteringer()
  {
    return Collections.unmodifiableList(pointposteringer);
  }
}
