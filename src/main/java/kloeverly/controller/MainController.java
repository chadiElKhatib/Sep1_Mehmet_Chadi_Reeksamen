package kloeverly.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import kloeverly.KloeverlyApp;
import kloeverly.data.AppData;
import kloeverly.model.Beboer;
import kloeverly.model.Opgave;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainController
{
  @FXML
  private Label antalBeboereLabel;

  @FXML
  private Label antalOpgaverLabel;

  @FXML
  private Label personligePointLabel;

  @FXML
  private Label faellesPointLabel;

  @FXML
  private ListView<String> senesteOpgaverListView;

  @FXML
  public void initialize()
  {
    opdaterDashboard();
  }

  private void opdaterDashboard()
  {
    int antalOpgaver = 0;
    int personligePoint = 0;

    List<Opgave> alleOpgaver = new ArrayList<>();

    for (Beboer beboer : AppData.getBeboere())
    {
      antalOpgaver += beboer.getOpgaver().size();
      personligePoint += beboer.beregnPersonligePoint();

      alleOpgaver.addAll(beboer.getOpgaver());
    }

    antalBeboereLabel.setText(
        String.valueOf(AppData.getBeboere().size())
    );

    antalOpgaverLabel.setText(
        String.valueOf(antalOpgaver)
    );

    personligePointLabel.setText(
        String.valueOf(personligePoint)
    );

    faellesPointLabel.setText(
        String.valueOf(
            AppData.getFaellesPointpulje()
                .beregnSamledePoint()
        )
    );

    alleOpgaver.sort(
        Comparator.comparing(Opgave::getDato).reversed()
    );

    senesteOpgaverListView.getItems().clear();

    int antal = Math.min(5, alleOpgaver.size());

    for (int i = 0; i < antal; i++)
    {
      Opgave opgave = alleOpgaver.get(i);

      senesteOpgaverListView.getItems().add(
          opgave.getDato()
              + "  |  "
              + opgave.getBeskrivelse()
              + "  |  "
              + opgave.getPoint()
              + " point"
      );
    }

    if (alleOpgaver.isEmpty())
    {
      senesteOpgaverListView
          .getItems()
          .add("Ingen opgaver registreret endnu.");
    }
  }
}