package kloeverly.data;

import java.io.*;

public class FileDataManager
{
  private static final String FILNAVN = "kloeverly-data.bin";

  public static void gem(DataContainer data)
  {
    try (ObjectOutputStream output =
        new ObjectOutputStream(
            new FileOutputStream(FILNAVN)))
    {
      output.writeObject(data);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public static DataContainer hent()
  {
    File fil = new File(FILNAVN);

    if (!fil.exists())
    {
      return null;
    }

    try (ObjectInputStream input =
        new ObjectInputStream(
            new FileInputStream(fil)))
    {
      return (DataContainer) input.readObject();
    }
    catch (IOException | ClassNotFoundException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}