package kloeverly.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import kloeverly.KloeverlyApp;
import kloeverly.data.AppData;
import kloeverly.model.Beboer;

public class BeboerController
{
  private final ObservableList<Beboer> beboere =
      AppData.getBeboere();

  @FXML
  private ListView<Beboer> beboerListView;

  @FXML
  public void initialize()
  {
    beboerListView.setItems(beboere);
  }

  @FXML
  private void opretBeboer()
  {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Opret beboer");

    TextField navnField = new TextField();
    TextField husnummerField = new TextField();
    DatePicker foedselsdatoPicker = new DatePicker();

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));

    grid.add(new Label("Navn:"), 0, 0);
    grid.add(navnField, 1, 0);

    grid.add(new Label("Husnummer:"), 0, 1);
    grid.add(husnummerField, 1, 1);

    grid.add(new Label("Fødselsdato:"), 0, 2);
    grid.add(foedselsdatoPicker, 1, 2);

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
          Beboer beboer = new Beboer(
              navnField.getText(),
              husnummerField.getText(),
              foedselsdatoPicker.getValue()
          );

          beboere.add(beboer);
          AppData.gemData();
        }
        catch (IllegalArgumentException e)
        {
          visFejl(e.getMessage());
        }
      }
    });
  }

  @FXML
  private void redigerBeboer()
  {
    Beboer valgt = beboerListView.getSelectionModel().getSelectedItem();

    if (valgt == null)
    {
      visFejl("Vælg en beboer først.");
      return;
    }

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Rediger beboer");

    TextField navnField = new TextField(valgt.getNavn());
    TextField husnummerField = new TextField(valgt.getHusnummer());
    DatePicker foedselsdatoPicker =
        new DatePicker(valgt.getFødselsdato());

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));

    grid.add(new Label("Navn:"), 0, 0);
    grid.add(navnField, 1, 0);

    grid.add(new Label("Husnummer:"), 0, 1);
    grid.add(husnummerField, 1, 1);

    grid.add(new Label("Fødselsdato:"), 0, 2);
    grid.add(foedselsdatoPicker, 1, 2);

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
          valgt.setNavn(navnField.getText());
          valgt.setHusnummer(husnummerField.getText());
          valgt.setFødselsdato(foedselsdatoPicker.getValue());

          AppData.gemData();
          beboerListView.refresh();
        }
        catch (IllegalArgumentException e)
        {
          visFejl(e.getMessage());
        }
      }
    });
  }

  @FXML
  private void sletBeboer()
  {
    Beboer valgt = beboerListView.getSelectionModel().getSelectedItem();

    if (valgt == null)
    {
      visFejl("Vælg en beboer først.");
      return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Slet beboer");
    alert.setHeaderText("Vil du slette " + valgt.getNavn() + "?");

    alert.showAndWait().ifPresent(result ->
    {
      if (result == ButtonType.OK)
      {
        beboere.remove(valgt);
        AppData.gemData();
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