package kloeverly.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kloeverly.model.Beboer;
import kloeverly.model.FaellesPointpulje;
import kloeverly.model.Opgave;

public class AppData
{
  private static final ObservableList<Opgave> aktiveOpgaver =
      FXCollections.observableArrayList();

  private static final ObservableList<Beboer> beboere =
      FXCollections.observableArrayList();

  private static FaellesPointpulje faellesPointpulje =
      new FaellesPointpulje();

  public static ObservableList<Beboer> getBeboere()
  {
    return beboere;
  }

  public static FaellesPointpulje getFaellesPointpulje()
  {
    return faellesPointpulje;
  }

  public static ObservableList<Opgave> getAktiveOpgaver()
  {
    return aktiveOpgaver;
  }

  public static void gemData()
  {
    DataContainer data =
        new DataContainer(
            beboere,
            aktiveOpgaver,
            faellesPointpulje
        );

    FileDataManager.gem(data);

    WebDataManager.opdaterHjemmeside();
  }

  public static void hentData()
  {
    DataContainer data = FileDataManager.hent();

    if (data != null)
    {
      beboere.setAll(data.getBeboere());
      aktiveOpgaver.setAll(data.getAktiveOpgaver());
      faellesPointpulje = data.getFaellesPointpulje();
    }
  }
}