package dsp;

import java.util.EventObject;
import java.util.Map;

/**
 * This class represents a EventObject, which is sent from SignalProviders to the listeners.
 * Copied and edited for our purpose from the EKG Projekt from Leo Kohler
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class InfoEvent extends EventObject 
{
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private ComponentTyp typ;
  private double value;
  private Unit unit;
  private Map<String, Double> additonalInfos;

  public InfoEvent(InfoProvider provider) {
    super(provider);
  }
  /**
   * Returns the signal as array of double values.
   * @return ComponentTyp typ
   */
  	public ComponentTyp getTyp() {
		return typ;
	}

	public Unit getUnit() {
		return unit;
	}

	public double getValue() {
		return value;
	}
	
	public Map<String, Double> getAdditonalInfos(){
		return additonalInfos;
	}

  /**
   * Sets the signal as array of doubles.
   * @param typ
   */
  public void setInfo(ComponentTyp typ, double value, Unit unit, Map<String, Double> additonalInfos) {
	  this.typ = typ;
	  this.value = value;
	  this.unit = unit;
	  this.additonalInfos = additonalInfos;
  }
}