package kloeverly.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public class FaellesPointpulje implements Serializable
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
      throw new IllegalArgumentException(
          "Fællespuljen kan kun modtage positive point.");
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

  public List<Pointpostering> getPointposteringer()
  {
    return Collections.unmodifiableList(pointposteringer);
  }

  public void fjernPointposteringerForOpgave(Opgave opgave)
  {
    pointposteringer.removeIf(
        postering -> postering.getOpgave() == opgave
    );
  }
}
