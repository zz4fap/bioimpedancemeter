package dsp;

import java.util.Map;

/**
 * Abstract class. Every algorithm inherits from this class.
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public abstract class Alg
{
	private double value = 0.0;
	private ComponentTyp typ;
	private Unit unit;
	private Map<String, Double> additonalInfos;
	
	public abstract boolean determine(double[] valuesLeft, double[] valuesRight, int length);

	public ComponentTyp getTyp() {
		return typ;
	}

	public Unit getUnit() {
		return unit;
	}

	public double getValue() {
		return value;
	}
	
	public Map<String, Double> getAdditonalInfos() {
		return additonalInfos;
	}

	protected void setAdditonalInfos(Map<String, Double> additionalInfos) {
		this.additonalInfos = additionalInfos;
	}

	protected void setTyp(ComponentTyp typ) {
		this.typ = typ;
	}

	protected void setUnit(Unit unit) {
		this.unit = unit;
	}

	protected void setValue(double value) {
		this.value = value;
	}
	
}
