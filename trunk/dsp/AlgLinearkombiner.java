package dsp;

import java.util.Map;
import java.util.TreeMap;

import javax.sound.sampled.AudioFormat;

/**
 * The AlgLinearkombiner is the main algorithm of this project. 
 * It uses an LMS-Algorithm to determine the values.
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class AlgLinearkombiner extends Alg
{
	private double mikro;
	private int frequency;
	private double w0;
	private double w1;
	private double[] real;
	private double[] imag;
	private double[] desired;
	private double res;
	private double failure = 1.0;
	private double referenceR = 1000;	// reference-resistor
	private double tolerance = 2;		// tolerance for type-recognition
	private double impedance = 0.0;		// impedance
	private Hilbert hilbert;
	
	public AlgLinearkombiner(double mikro, int frequency, AudioFormat audioformat)
	{
		this.mikro = mikro; //0.05
		this.frequency = frequency; //1250
		hilbert = new Hilbert((int)(audioformat.getSampleRate()/frequency));
	}
	
	public boolean determine(double[] valuesLeft, double[] valuesRight, int length)
	{
		hilbert.calculate(valuesRight, length);
		desired = valuesLeft;
		real = hilbert.getReal();
		imag = hilbert.getImag();

		for(int i=0; i < length; i++)
		{
			//calculate
			w0 = w0 + failure * mikro * real[i];
			w1 = w1 + failure * mikro * imag[i];
			res = w0 * real[i] + w1 * imag[i];
			failure = desired[i] - res ;
		}	
		calcValues();
		return true;
	}
	
	public void calcValues() 
	{		
		double phi = Math.atan(w1/w0)*(180/Math.PI); // (180/pi) converts to degrees.
		impedance = Math.sqrt( Math.pow(w0*referenceR, 2) + Math.pow(w1*referenceR, 2) );
		//double real = impedance * Math.cos(Math.abs(phi) * Math.PI/180); // (pi/180) converts to radians.
		//double imag = impedance * Math.sin(Math.abs(phi) * Math.PI/180); // (pi/180) converts to radians.
		
		double real = impedance * Math.cos(phi * Math.PI/180); // (pi/180) converts to radians.
		double imag = impedance * Math.sin(phi * Math.PI/180); // (pi/180) converts to radians.

		// Typ bestimmen
		if(phi < (0.0 - tolerance)) 
		{
			// capacitor
			setTyp(ComponentTyp.CAPACITOR);
			setUnit(Unit.nF);
			setValue(1 / (2 * Math.PI * frequency * imag) * Math.pow(10, 9));
		}
		else if(phi > (0 + tolerance))
		{
			// inductor
			setTyp(ComponentTyp.INDUCTOR);
			setUnit(Unit.mH);
			setValue((imag / (2 * Math.PI * frequency))*1000);
		}
		else 
		{
			// resistor
			setTyp(ComponentTyp.RESISTOR);
			setUnit(Unit.Ohm);
			setValue(impedance);
		}
		
		//additional infos
		Map<String, Double> additionalInfos = new TreeMap<String, Double>();
		additionalInfos.put("weight 0", w0);
		additionalInfos.put("weight 1", w1);
		additionalInfos.put("impedance", impedance);
		additionalInfos.put("phi", phi);
		additionalInfos.put("real", real);
		additionalInfos.put("imag", imag);
		setAdditonalInfos(additionalInfos);
	}
	
	public void setReferenceR(double referenceR) 
	{
		this.referenceR = referenceR;
	}	
	
	public void setTolerance(double tolerance)
	{
		this.tolerance = tolerance;
	}
	
}
	