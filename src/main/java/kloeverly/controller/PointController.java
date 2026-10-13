package kloeverly.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import kloeverly.KloeverlyApp;
import kloeverly.data.AppData;
import kloeverly.model.Beboer;
import kloeverly.model.Bytteopgave;
import kloeverly.model.Opgave;
import kloeverly.model.Pointpostering;

import java.time.LocalDate;

public class PointController
{
  @FXML
  private ComboBox<Beboer> beboerComboBox;

  @FXML
  private Label personligePointLabel;

  @FXML
  private Label faellesPointLabel;

  @FXML
  private ListView<Pointpostering> pointposteringListView;

  @FXML
  public void initialize()
  {
    beboerComboBox.setItems(AppData.getBeboere());

    beboerComboBox.setOnAction(e -> opdaterVisning());

    if (!AppData.getBeboere().isEmpty())
    {
      beboerComboBox.getSelectionModel().selectFirst();
      opdaterVisning();
    }
    else
    {
      opdaterFaellesPoint();
    }
  }

  private void opdaterVisning()
  {
    Beboer valgt = beboerComboBox.getValue();

    if (valgt == null)
    {
      personligePointLabel.setText("0");
      pointposteringListView.getItems().clear();
      return;
    }

    personligePointLabel.setText(
        String.valueOf(valgt.beregnPersonligePoint())
    );

    int sidsteNulstilling = -1;

    for (int i = 0; i < valgt.getPointposteringer().size(); i++)
    {
      Pointpostering postering = valgt.getPointposteringer().get(i);

      if (postering.getÅrsag().equals("Nulstilling af personlige point"))
      {
        sidsteNulstilling = i;
      }
    }

    pointposteringListView.getItems().clear();

    for (int i = sidsteNulstilling + 1;
         i < valgt.getPointposteringer().size();
         i++)
    {
      pointposteringListView.getItems().add(
          valgt.getPointposteringer().get(i)
      );
    }

    opdaterFaellesPoint();
  }

  private void opdaterFaellesPoint()
  {
    faellesPointLabel.setText(
        String.valueOf(
            AppData.getFaellesPointpulje().beregnSamledePoint()
        )
    );
  }

  @FXML
  private void registrerPointaendring()
  {
    Beboer valgt = beboerComboBox.getValue();

    if (valgt == null)
    {
      visFejl("Vælg en beboer først.");
      return;
    }

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Registrer pointændring");

    TextField antalField = new TextField();
    TextField aarsagField = new TextField();
    DatePicker datoPicker = new DatePicker(LocalDate.now());

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));

    grid.add(new Label("Point:"), 0, 0);
    grid.add(antalField, 1, 0);

    grid.add(new Label("Årsag:"), 0, 1);
    grid.add(aarsagField, 1, 1);

    grid.add(new Label("Dato:"), 0, 2);
    grid.add(datoPicker, 1, 2);

    dialog.getDialogPane().setContent(grid);

    dialog.getDialogPane().getButtonTypes().addAll(
        ButtonType.OK,
        ButtonType.CANCEL
    );

    dialog.showAndWait().ifPresent(result ->
    {
      if (result == ButtonType.OK)
      {
        try
        {
          int antalPoint =
              Integer.parseInt(antalField.getText());

          if (antalPoint == 0)
          {
            throw new IllegalArgumentException(
                "Pointændringen må ikke være 0."
            );
          }

          Pointpostering postering =
              new Pointpostering(
                  datoPicker.getValue(),
                  antalPoint,
                  aarsagField.getText(),
                  null
              );

          valgt.tilføjPointpostering(postering);
          AppData.gemData();
          opdaterVisning();
        }
        catch (NumberFormatException e)
        {
          visFejl("Point skal være et tal.");
        }
        catch (IllegalArgumentException e)
        {
          visFejl(e.getMessage());
        }
      }
    });
  }

  @FXML
  private void nulstilPoint()
  {
    Beboer valgt = beboerComboBox.getValue();

    if (valgt == null)
    {
      visFejl("Vælg en beboer først.");
      return;
    }

    for (Opgave opgave : valgt.getOpgaver())
    {
      if (opgave instanceof Bytteopgave)
      {
        visFejl(
            valgt.getNavn()
                + " har en aktiv bytteopgave. "
                + "Afslut eller slet den før point nulstilles."
        );
        return;
      }
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

    alert.setTitle("Nulstil point");
    alert.setHeaderText(
        "Vil du nulstille point for " + valgt.getNavn() + "?"
    );

    alert.showAndWait().ifPresent(result ->
    {
      if (result == ButtonType.OK)
      {
        valgt.nulstilPersonligePoint();
        AppData.gemData();
        opdaterVisning();
      }
    });
  }

  private void visFejl(String besked)
  {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setHeaderText(null);
    alert.setContentText(besked);
    alert.showAndWait();
  }
}