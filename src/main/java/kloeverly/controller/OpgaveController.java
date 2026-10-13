package kloeverly.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import kloeverly.data.AppData;
import kloeverly.data.WebDataManager;
import kloeverly.model.*;

import java.util.ArrayList;
import java.util.List;

public class OpgaveController
{
  @FXML
  private ListView<Opgave> opgaveListView;

  @FXML
  public void initialize()
  {
    opgaveListView.setCellFactory(listView -> new ListCell<>()
    {
      @Override
      protected void updateItem(Opgave opgave, boolean empty)
      {
        super.updateItem(opgave, empty);

        if (empty || opgave == null)
        {
          setText(null);
          return;
        }

        String type;

        if (opgave instanceof Faellesopgave)
        {
          type = "Fællesopgave";
        }
        else if (opgave instanceof GroentTiltag)
        {
          type = "Grønt tiltag";
        }
        else if (opgave instanceof Bytteopgave)
        {
          type = "Bytteopgave";
        }
        else
        {
          type = "Opgave";
        }

        setText(
            type
                + "  |  "
                + opgave.getDato()
                + "  |  "
                + opgave.getBeskrivelse()
                + "  |  "
                + opgave.getPoint()
                + " point"
        );
      }
    });

    opdaterListe();
  }

  private void opdaterListe()
  {
    opgaveListView.getItems().clear();

    // Fællesopgaver og grønne tiltag,
    // som endnu ikke er afsluttet.
    opgaveListView.getItems().addAll(
        AppData.getAktiveOpgaver()
    );

    // Aktive bytteopgaver ligger hos bestilleren.
    for (Beboer beboer : AppData.getBeboere())
    {
      for (Opgave opgave : beboer.getOpgaver())
      {
        if (opgave instanceof Bytteopgave)
        {
          opgaveListView.getItems().add(opgave);
        }
      }
    }
  }

  // --------------------------------------------------
  // OPRET OPGAVE
  // --------------------------------------------------

  @FXML
  private void opretOpgave()
  {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Opret opgave");

    ComboBox<String> typeBox =
        new ComboBox<>(FXCollections.observableArrayList(
            "Fællesopgave",
            "Grønt tiltag",
            "Bytteopgave"
        ));

    ComboBox<Beboer> beboerBox =
        new ComboBox<>(AppData.getBeboere());

    // Ingen beboer er nødvendig,
    // før Bytteopgave vælges.
    beboerBox.setDisable(true);

    TextField beskrivelseField = new TextField();
    DatePicker datoPicker = new DatePicker();
    TextField pointField = new TextField();

    Label pointLabel = new Label("Point:");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));

    grid.add(new Label("Type:"), 0, 0);
    grid.add(typeBox, 1, 0);

    grid.add(new Label("Beboer:"), 0, 1);
    grid.add(beboerBox, 1, 1);

    grid.add(new Label("Beskrivelse:"), 0, 2);
    grid.add(beskrivelseField, 1, 2);

    grid.add(new Label("Dato:"), 0, 3);
    grid.add(datoPicker, 1, 3);

    grid.add(pointLabel, 0, 4);
    grid.add(pointField, 1, 4);

    typeBox.setOnAction(e ->
    {
      String type = typeBox.getValue();

      if ("Fællesopgave".equals(type))
      {
        beboerBox.setDisable(true);
        beboerBox.setValue(null);

        pointLabel.setText("Belønning:");
      }
      else if ("Grønt tiltag".equals(type))
      {
        beboerBox.setDisable(true);
        beboerBox.setValue(null);

        pointLabel.setText("Point til fælles pulje:");
      }
      else if ("Bytteopgave".equals(type))
      {
        beboerBox.setDisable(false);

        pointLabel.setText("Pris i personlige point:");
      }
    });

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
          String type = typeBox.getValue();

          if (type == null)
          {
            throw new IllegalArgumentException(
                "Vælg en opgavetype."
            );
          }

          int point =
              Integer.parseInt(pointField.getText());

          Opgave opgave;

          if (type.equals("Fællesopgave"))
          {
            opgave = new Faellesopgave(
                beskrivelseField.getText(),
                datoPicker.getValue(),
                point
            );

            // Ledig opgave uden beboer.
            AppData.getAktiveOpgaver().add(opgave);
          }
          else if (type.equals("Grønt tiltag"))
          {
            opgave = new GroentTiltag(
                beskrivelseField.getText(),
                datoPicker.getValue(),
                point
            );

            // Grønt tiltag er aktivt.
            // Ingen point gives endnu.
            AppData.getAktiveOpgaver().add(opgave);
          }
          else
          {
            Beboer bestiller = beboerBox.getValue();

            if (bestiller == null)
            {
              throw new IllegalArgumentException(
                  "Vælg en beboer."
              );
            }

            opgave = new Bytteopgave(
                beskrivelseField.getText(),
                datoPicker.getValue(),
                point
            );

            // Bestilleren betaler pointene med det samme.
            bestiller.tilføjOpgave(opgave);
          }

          AppData.gemData();
          WebDataManager.opdaterGroenneTiltag();
          opdaterListe();
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

  // --------------------------------------------------
  // REDIGER OPGAVE
  // --------------------------------------------------

  @FXML
  private void redigerOpgave()
  {
    Opgave valgt =
        opgaveListView.getSelectionModel().getSelectedItem();

    if (valgt == null)
    {
      visFejl("Vælg en opgave først.");
      return;
    }

    boolean gammelErAktiv =
        AppData.getAktiveOpgaver().contains(valgt);

    Beboer gammelEjer =
        findBeboerForOpgave(valgt);

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Rediger opgave");

    ComboBox<String> typeBox =
        new ComboBox<>(FXCollections.observableArrayList(
            "Fællesopgave",
            "Grønt tiltag",
            "Bytteopgave"
        ));

    ComboBox<Beboer> beboerBox =
        new ComboBox<>(AppData.getBeboere());

    Label pointLabel = new Label("Point:");

    if (valgt instanceof Faellesopgave)
    {
      typeBox.setValue("Fællesopgave");
      beboerBox.setDisable(true);
      pointLabel.setText("Belønning:");
    }
    else if (valgt instanceof GroentTiltag)
    {
      typeBox.setValue("Grønt tiltag");
      beboerBox.setDisable(true);
      pointLabel.setText("Point til fælles pulje:");
    }
    else if (valgt instanceof Bytteopgave)
    {
      typeBox.setValue("Bytteopgave");
      beboerBox.setDisable(false);
      beboerBox.setValue(gammelEjer);
      pointLabel.setText("Pris i personlige point:");
    }

    TextField beskrivelseField =
        new TextField(valgt.getBeskrivelse());

    DatePicker datoPicker =
        new DatePicker(valgt.getDato());

    TextField pointField =
        new TextField(
            String.valueOf(valgt.getPoint())
        );

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));

    grid.add(new Label("Type:"), 0, 0);
    grid.add(typeBox, 1, 0);

    grid.add(new Label("Beboer:"), 0, 1);
    grid.add(beboerBox, 1, 1);

    grid.add(new Label("Beskrivelse:"), 0, 2);
    grid.add(beskrivelseField, 1, 2);

    grid.add(new Label("Dato:"), 0, 3);
    grid.add(datoPicker, 1, 3);

    grid.add(pointLabel, 0, 4);
    grid.add(pointField, 1, 4);

    typeBox.setOnAction(e ->
    {
      String type = typeBox.getValue();

      if ("Fællesopgave".equals(type))
      {
        beboerBox.setDisable(true);
        beboerBox.setValue(null);

        pointLabel.setText("Belønning:");
      }
      else if ("Grønt tiltag".equals(type))
      {
        beboerBox.setDisable(true);
        beboerBox.setValue(null);

        pointLabel.setText("Point til fælles pulje:");
      }
      else if ("Bytteopgave".equals(type))
      {
        beboerBox.setDisable(false);

        pointLabel.setText("Pris i personlige point:");
      }
    });

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
          String type = typeBox.getValue();

          if (type == null)
          {
            throw new IllegalArgumentException(
                "Vælg en opgavetype."
            );
          }

          int nyePoint =
              Integer.parseInt(pointField.getText());

          String nyBeskrivelse =
              beskrivelseField.getText();

          Opgave nyOpgave;
          Beboer nyEjer = null;

          if (type.equals("Fællesopgave"))
          {
            nyOpgave = new Faellesopgave(
                nyBeskrivelse,
                datoPicker.getValue(),
                nyePoint
            );
          }
          else if (type.equals("Grønt tiltag"))
          {
            nyOpgave = new GroentTiltag(
                nyBeskrivelse,
                datoPicker.getValue(),
                nyePoint
            );
          }
          else
          {
            nyEjer = beboerBox.getValue();

            if (nyEjer == null)
            {
              throw new IllegalArgumentException(
                  "Vælg en beboer til bytteopgaven."
              );
            }

            nyOpgave = new Bytteopgave(
                nyBeskrivelse,
                datoPicker.getValue(),
                nyePoint
            );
          }

          /*
           * Fjern den gamle version.
           */
          if (gammelErAktiv)
          {
            AppData.getAktiveOpgaver()
                .remove(valgt);
          }
          else if (gammelEjer != null)
          {
            gammelEjer.fjernOpgave(valgt);
          }
          else
          {
            throw new IllegalArgumentException(
                "Kunne ikke finde den gamle opgave."
            );
          }

          /*
           * Tilføj den nye version.
           * Hvis noget går galt, lægger vi den gamle tilbage.
           */
          try
          {
            if (nyOpgave instanceof Bytteopgave)
            {
              nyEjer.tilføjOpgave(nyOpgave);
            }
            else
            {
              AppData.getAktiveOpgaver()
                  .add(nyOpgave);
            }
          }
          catch (IllegalArgumentException e)
          {
            // Rollback
            if (gammelErAktiv)
            {
              AppData.getAktiveOpgaver()
                  .add(valgt);
            }
            else if (gammelEjer != null)
            {
              gammelEjer.tilføjOpgave(valgt);
            }

            throw e;
          }

          AppData.gemData();
          WebDataManager.opdaterGroenneTiltag();
          opdaterListe();
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

  // --------------------------------------------------
  // SLET OPGAVE
  // --------------------------------------------------

  @FXML
  private void sletOpgave()
  {
    Opgave valgt =
        opgaveListView.getSelectionModel().getSelectedItem();

    if (valgt == null)
    {
      visFejl("Vælg en opgave først.");
      return;
    }

    Alert alert =
        new Alert(Alert.AlertType.CONFIRMATION);

    alert.setTitle("Slet opgave");
    alert.setHeaderText("Vil du slette opgaven?");
    alert.setContentText(valgt.getBeskrivelse());

    alert.showAndWait().ifPresent(result ->
    {
      if (result == ButtonType.OK)
      {
        try
        {
          if (AppData.getAktiveOpgaver()
              .contains(valgt))
          {
            // Fællesopgave eller grønt tiltag.
            AppData.getAktiveOpgaver()
                .remove(valgt);
          }
          else
          {
            // Bytteopgave.
            Beboer bestiller =
                findBeboerForOpgave(valgt);

            if (bestiller == null)
            {
              throw new IllegalArgumentException(
                  "Kunne ikke finde bestilleren."
              );
            }

            /*
             * fjernOpgave fjerner også den negative
             * pointpostering, så bestilleren får
             * sine point tilbage.
             */
            bestiller.fjernOpgave(valgt);
          }

          AppData.gemData();
          WebDataManager.opdaterGroenneTiltag();
          opdaterListe();
        }
        catch (IllegalArgumentException e)
        {
          visFejl(e.getMessage());
        }
      }
    });
  }

  // --------------------------------------------------
  // AFSLUT OPGAVE
  // --------------------------------------------------

  @FXML
  private void afslutOpgave()
  {
    Opgave valgt =
        opgaveListView.getSelectionModel().getSelectedItem();

    if (valgt == null)
    {
      visFejl("Vælg en opgave først.");
      return;
    }

    /*
     * FÆLLESOPGAVE
     *
     * Vælg hvem der udførte opgaven.
     * Personen får personlige point.
     */
    if (valgt instanceof Faellesopgave)
    {
      if (AppData.getBeboere().isEmpty())
      {
        visFejl("Der er ingen beboere registreret.");
        return;
      }

      ChoiceDialog<Beboer> dialog =
          new ChoiceDialog<>(
              AppData.getBeboere().get(0),
              AppData.getBeboere()
          );

      dialog.setTitle("Afslut fællesopgave");
      dialog.setHeaderText(
          "Hvem udførte opgaven?"
      );
      dialog.setContentText("Beboer:");

      dialog.showAndWait().ifPresent(udfoerer ->
      {
        try
        {
          Pointpostering postering =
              new Pointpostering(
                  valgt.getDato(),
                  valgt.getPoint(),
                  "Fællesopgave: "
                      + valgt.getBeskrivelse(),
                  valgt
              );

          udfoerer.tilføjPointpostering(postering);
          udfoerer.registrerAfsluttetOpgave();

          AppData.getAktiveOpgaver()
              .remove(valgt);

          AppData.gemData();
          opdaterListe();
        }
        catch (IllegalArgumentException e)
        {
          visFejl(e.getMessage());
        }
      });

      return;
    }

    /*
     * BYTTEOPGAVE
     *
     * Bestilleren har allerede betalt point.
     * Nu vælges den beboer, der udførte opgaven,
     * og denne person modtager pointene.
     */
    if (valgt instanceof Bytteopgave)
    {
      Beboer bestiller =
          findBeboerForOpgave(valgt);

      if (bestiller == null)
      {
        visFejl("Kunne ikke finde bestilleren.");
        return;
      }

      List<Beboer> muligeUdfoerere =
          new ArrayList<>(AppData.getBeboere());

      // Bestilleren skal ikke kunne betale sig selv.
      muligeUdfoerere.remove(bestiller);

      if (muligeUdfoerere.isEmpty())
      {
        visFejl(
            "Der skal være mindst én anden beboer "
                + "til at udføre bytteopgaven."
        );
        return;
      }

      ChoiceDialog<Beboer> dialog =
          new ChoiceDialog<>(
              muligeUdfoerere.get(0),
              muligeUdfoerere
          );

      dialog.setTitle("Afslut bytteopgave");
      dialog.setHeaderText(
          "Hvem udførte \""
              + valgt.getBeskrivelse()
              + "\"?"
      );
      dialog.setContentText("Udfører:");

      dialog.showAndWait().ifPresent(udfoerer ->
      {
        try
        {
          Pointpostering postering =
              new Pointpostering(
                  valgt.getDato(),
                  valgt.getPoint(),
                  "Udført bytteopgave: "
                      + valgt.getBeskrivelse(),
                  valgt
              );

          udfoerer.tilføjPointpostering(postering);

          // Opgaven fjernes fra bestilleren,
          // men bestillerens betaling beholdes.
          bestiller.afslutOpgave(valgt);

          udfoerer.registrerAfsluttetOpgave();

          AppData.gemData();
          opdaterListe();
        }
        catch (IllegalArgumentException e)
        {
          visFejl(e.getMessage());
        }
      });

      return;
    }

    /*
     * GRØNT TILTAG
     *
     * Ingen beboer er tilknyttet.
     * Når tiltaget afsluttes, får fællespuljen point,
     * og tiltaget bliver synligt anonymt på hjemmesiden.
     */
    if (valgt instanceof GroentTiltag)
    {
      Alert alert =
          new Alert(Alert.AlertType.CONFIRMATION);

      alert.setTitle("Afslut grønt tiltag");
      alert.setHeaderText(
          "Er det grønne tiltag udført?"
      );
      alert.setContentText(
          valgt.getBeskrivelse()
      );

      alert.showAndWait().ifPresent(result ->
      {
        if (result == ButtonType.OK)
        {
          try
          {
            Pointpostering postering =
                new Pointpostering(
                    valgt.getDato(),
                    valgt.getPoint(),
                    "Grønt tiltag: "
                        + valgt.getBeskrivelse(),
                    valgt
                );

            AppData.getFaellesPointpulje()
                .tilføjPointpostering(postering);

            AppData.getAktiveOpgaver()
                .remove(valgt);

            AppData.gemData();

            WebDataManager
                .opdaterGroenneTiltag();

            opdaterListe();
          }
          catch (IllegalArgumentException e)
          {
            visFejl(e.getMessage());
          }
        }
      });
    }
  }

  // --------------------------------------------------
  // HJÆLPEMETODER
  // --------------------------------------------------

  private void visFejl(String besked)
  {
    Alert alert =
        new Alert(Alert.AlertType.ERROR);

    alert.setHeaderText(null);
    alert.setContentText(besked);
    alert.showAndWait();
  }

  private Beboer findBeboerForOpgave(Opgave opgave)
  {
    for (Beboer beboer : AppData.getBeboere())
    {
      if (beboer.getOpgaver().contains(opgave))
      {
        return beboer;
      }
    }

    return null;
  }
}