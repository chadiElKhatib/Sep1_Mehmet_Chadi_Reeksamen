package kloeverly.data;

import kloeverly.model.Beboer;
import kloeverly.model.FaellesPointpulje;
import kloeverly.model.Opgave;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataContainer implements Serializable
{
  private List<Beboer> beboere;
  private FaellesPointpulje faellesPointpulje;
  private List<Opgave> aktiveOpgaver;

  public DataContainer(List<Beboer> beboere,
      List<Opgave> aktiveOpgaver,
      FaellesPointpulje faellesPointpulje)
  {
    this.beboere = new ArrayList<>(beboere);
    this.aktiveOpgaver = new ArrayList<>(aktiveOpgaver);
    this.faellesPointpulje = faellesPointpulje;
  }

  public List<Beboer> getBeboere()
  {
    return beboere;
  }

  public FaellesPointpulje getFaellesPointpulje()
  {
    return faellesPointpulje;
  }

  public List<Opgave> getAktiveOpgaver()
  {
    return aktiveOpgaver;
  }
}