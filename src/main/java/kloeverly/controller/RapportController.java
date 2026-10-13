package kloeverly.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import kloeverly.KloeverlyApp;
import kloeverly.data.AppData;
import kloeverly.model.Beboer;

public class RapportController
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
  private ListView<String> beboerRapportList;

  @FXML
  public void initialize()
  {
    opdaterRapport();
  }

  private void opdaterRapport()
{
  int antalBeboere = AppData.getBeboere().size();
  int antalAfsluttedeOpgaver = 0;
  int samledePersonligePoint = 0;

  beboerRapportList.getItems().clear();

  for (Beboer beboer : AppData.getBeboere())
  {
    antalAfsluttedeOpgaver +=
        beboer.getAntalAfsluttedeOpgaver();

    samledePersonligePoint +=
        beboer.beregnPersonligePoint();

    beboerRapportList.getItems().add(
        beboer.getNavn()
            + "  |  Udførte opgaver: "
            + beboer.getAntalAfsluttedeOpgaver()
    );
  }

  antalBeboereLabel.setText(
      String.valueOf(antalBeboere)
  );

  antalOpgaverLabel.setText(
      String.valueOf(antalAfsluttedeOpgaver)
  );

  personligePointLabel.setText(
      String.valueOf(samledePersonligePoint)
  );

  faellesPointLabel.setText(
      String.valueOf(
          AppData.getFaellesPointpulje()
              .beregnSamledePoint()
      )
  );
}
}