package dsp;

/**
 * This interface defines the method which will be called, if a SignalEvent is
 * sent to the class which implement this interface.
 * Copied and edited for our purpose from the EKG Projekt from Leo Kohler
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Joël Gruber
 */
public interface InfoListener {
  /**
   * Method to call if a new signal is ready.
   * @param event PulseEvent
   */
  public void infoReady(InfoEvent event);
}