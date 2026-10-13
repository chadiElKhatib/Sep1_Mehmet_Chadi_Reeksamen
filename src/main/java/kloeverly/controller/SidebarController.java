package kloeverly.controller;

import javafx.fxml.FXML;
import kloeverly.KloeverlyApp;

public class SidebarController
{
  @FXML
  private void visDashboard()
  {
    KloeverlyApp.skiftScene("MainView.fxml");
  }

  @FXML
  private void visBeboere()
  {
    KloeverlyApp.skiftScene("BeboerView.fxml");
  }

  @FXML
  private void visOpgaver()
  {
    KloeverlyApp.skiftScene("OpgaveView.fxml");
  }

  @FXML
  private void visPoint()
  {
    KloeverlyApp.skiftScene("PointView.fxml");
  }

  @FXML
  private void visRapport()
  {
    KloeverlyApp.skiftScene("RapportView.fxml");
  }
}