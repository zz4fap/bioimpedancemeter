package view;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Calculate the Color of the ring of a resistor. Is used to display 
 * a schema from a resistor by the gui
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Joël Gruber
 */
public class Widerstand {

	private Color ring1C;
	private Color ring2C;
	private Color ringMC;
	private double eReihe[] = {3, 6, 12, 24, 48, 96, 192};
	private double widerstandsreihe[] = new double[381];
	private Display display;
	private Color colors[] = new Color[12];
	
	public Widerstand() {
		//color setzten
		display = Display.getDefault();
		colors[0] = new Color(display, 192,192,192); //silver
		colors[1] =  new Color(display, 255,204,0); //gold
		colors[2] =  new Color(display, 0,0,0); //black
		colors[3] =  new Color(display, 153,51,0); //brown
		colors[4] =  new Color(display, 255,0,0); //red
		colors[5] =  new Color(display, 255,101,0); //orange
		colors[6] =  new Color(display, 255,255,0); //yellow
		colors[7] =  new Color(display, 0,127,0); //green
		colors[8] =  new Color(display, 0,0,255); //blue
		colors[9] =  new Color(display, 127,0,127); //violet
		colors[10] =  new Color(display, 127,127,127); //grey
		colors[11] =  new Color(display, 255,255,255); //white
		
		//widerstandsreihe berechnen
		int cnt = 0;
		for(int i = 0;i < eReihe.length;i++){
//			System.out.println("---------" + eReihe[i]);
			for(int j = 0;j < eReihe[i];j++){
				widerstandsreihe[cnt] = Math.round(Math.pow(Math.pow(10, j),1/eReihe[i])*10);
//				System.out.println("Pos " + cnt + ": " + widerstandsreihe[cnt]);
				cnt++;
			}
		}
	}
	
	public void calcColors(double resistor) {
		int stringLength;
		int commaIndex;
		String resistorString;
		String resistorWithoutComma;
		resistorString = Double.toString(resistor);
		
		stringLength = resistorString.length();
		System.out.println("Stringlänge: " + stringLength);
		
		commaIndex = resistorString.indexOf(".");
//		ring4 = (int) Math.pow(10, commaIndex-2);
//		System.out.println("Kommastelle: " + commaIndex + "\tFaktor: " + ring4);
		
		
		// Komma aus Zahl entfernen
		resistorWithoutComma = resistorString.substring(0,commaIndex) + 
							resistorString.substring(commaIndex+1);
		System.out.println("Zahl ohne Komma: " + resistorWithoutComma);
		
		//übereinstimmung in widerstandsreihe finden
		int resFirstTwoNumbers = Integer.valueOf(resistorWithoutComma.substring(0,2));
		if(Integer.valueOf(resistorWithoutComma.substring(2,3)) >= 5){
			resFirstTwoNumbers++;
		}
		System.out.println("restwo: " + resFirstTwoNumbers);
		int failureMax = 100;
		int failure;
		int bestPos = 0;
		for(int i=0;i<widerstandsreihe.length;i++){
			failure = Math.abs((int) (resFirstTwoNumbers - widerstandsreihe[i]));
			if(failure < failureMax){
				failureMax = failure;
				bestPos = i;
			}
		}
		System.out.println("failuremax: " + failureMax);
		System.out.println("value: " + widerstandsreihe[bestPos]);
		
		ring1C = colors[Integer.valueOf(String.valueOf(widerstandsreihe[bestPos]).substring(0,1))+2];
		ring2C = colors[Integer.valueOf(String.valueOf(widerstandsreihe[bestPos]).substring(1,2))+2];
		ringMC = colors[commaIndex];
		
		// Index der ersten zu verwertenden Zahl suchen
		for(int i=0; i < resistorWithoutComma.length(); i++) {
			if(resistorWithoutComma.charAt(i) == '0') {
			}else {
				break;
			}
		}
	}
	
	public Color getRing1C() {
		return ring1C;
	}

	public Color getRing2C() {
		return ring2C;
	}

	public Color getRingMC() {
		return ringMC;
	}
	
	// TODO: maximal 2 Nullen nach Komma -> sonst Problem wegen E
	//		 maximal 6 Nullen vor Komma  -> sonst Problem wegen E

}
