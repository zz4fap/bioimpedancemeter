package dsp;

import java.util.EventObject;
import fft.*;

/**
 * This class represents a EventObject, which is sent from SignalProviders to the listeners.
 * Copied and edited for our purpose from the EKG Projekt from Leo Kohler
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class SignalEvent extends EventObject 
{
	private static final long serialVersionUID = 1L;
	private double[] signalLeft;
	private double[] signalRight;
	private double[] signalFFT;
	private FFTHelper fftHelperObj;

  public SignalEvent(SignalProvider provider) 
  {
    super(provider);
  }
  /**
   * Returns the signal as array of double values.
   * @return double signalLeft
   */
  public double[] getLeftSignal() 
  {
    return signalLeft;
  }

  public double[] getRightSignal() 
  {
	  return signalRight;
  }
  
  public double[] getFFTSignal() 
  {
	  return signalFFT;
  }
  
  public FFTHelper getfftHelperObj()
  {
	  return fftHelperObj;
  }

  /**
   * Sets the signal as array of doubles.
   * @param left
   * @param right
   */
  public void setSignal(double[] left, double[] right) 
  {
    signalLeft = left;
    signalRight = right;
  }
  
  /**
   * Sets the signal as array of doubles.
   * @param left
   * @param right
   * @param fft
   */
  public void setSignal(double[] left, double[] right, double[] fft ) 
  {
    signalLeft = left;
    signalRight = right;
    signalFFT = fft;
  }
  
  /**
   * Sets the signal as array of doubles.
   * @param left
   * @param right
   * @param fftHelperObj
   */
  public void setSignal(double[] left, double[] right, FFTHelper fftHelperObj) 
  {
    signalLeft = left;
    signalRight = right;
    this.fftHelperObj = fftHelperObj;
  }
}