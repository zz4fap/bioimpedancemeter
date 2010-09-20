package dsp;

import java.util.Map;
import java.util.TreeMap;

/**
 * Additional Algorithmus without any logic. 
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Joël Gruber
 */
public class AlgNew extends Alg {

	@Override
	public boolean determine(double[] valuesLeft, double[] valuesRight,
			int length) {
		System.out.println("alg kollege ausgeführt");
		setTyp(ComponentTyp.INDUCTOR);
		setUnit(Unit.F);
		setValue(34.32);
		Map<String, Double> additionalInfos = new TreeMap<String, Double>();
		additionalInfos.put("Jahr", 2007d);
		additionalInfos.put("Monat", 6d);
		additionalInfos.put("Tag", 22d);
		setAdditonalInfos(additionalInfos);
		return true;
	}

}
