 package dsp;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class generates the frequency-sweep over the specified
 * frequency array.
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland 
 * @author Marcel Kluser
 */
public class FreqSweep{
	// Variablen FreqSweep 
	private final static int SAMPLING_RATE = 44100;
	private final static double SAMPLE_DURATION = 0.4;
	private final static int[] FREQ = {0,400,800,1600,3200,6400,12800,0};
	private final static double AMPLITUDE_MULTIPLICATOR = 0.2; 
	private final static int T_LAENGE = (int)(SAMPLE_DURATION/(1.0/SAMPLING_RATE));
	private byte[] byteData = new byte[T_LAENGE * FREQ.length];
	private double[][] tValue = new double[T_LAENGE /4][FREQ.length];
	private AlgRLC algRLC;
	
	
	public FreqSweep(){
		generateFrequenzSweepData();
		algRLC = new AlgRLC(SAMPLING_RATE, SAMPLE_DURATION, FREQ);
		algRLC.settValue(tValue);	
	}
	
	/**
	 * Generate Data for Frequency Sweep
	 */
	public void generateFrequenzSweepData(){
		double ptempValue;
		double t = 0;

		for(int m=0; m < FREQ.length; m++){	
			for(int i = m*T_LAENGE, tValueSize=0; i < ((m+1)*T_LAENGE)-3; i=i+4, tValueSize++){
				//sinusvalue
				ptempValue = Math.sin( 2 * Math.PI * FREQ[m] * t ) * AMPLITUDE_MULTIPLICATOR;
				t= t + (1.0/SAMPLING_RATE);
				//saturdation
				ptempValue = Math.min(1.0, Math.max(-1.0, ptempValue));
				tValue[tValueSize][m] = ptempValue;
				//scaling and conversion to integer
				int nSample = (int) Math.round(ptempValue * 32767.0);
				byte low = (byte) (nSample & 0xFF);
				byte high = (byte) ((nSample >> 8) & 0xFF);
				byteData[i] = low;
				byteData[i+1] = high;
				byteData[i+2] = low;
				byteData[i+3] = high;
			}
		}		
		//writeTXTFile(byteData, "FFF.txt");
		//writetValueFiles();		
	}

	/**
	 * Generates text file for debug purpose.
	 * 
	 * @param daten Data as byte of frequency sweep
	 * @param filename Name of text file
	 */
	public void writeTXTFile(byte[] daten, String filename ){
		FileOutputStream schreibeTextFileSum = null;
		try {
			schreibeTextFileSum = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] tmpDaten = new String[daten.length];
		String newline= "\r\n";
		
		for(int s=0; s<daten.length; s++){
			tmpDaten[s]= (Byte.toString(daten[s]));				
		}
		
		for(int h=0; h<tmpDaten.length; h++){
			for(int u=0; u < tmpDaten[h].length(); u++){
				try {
					schreibeTextFileSum.write((byte)tmpDaten[h].toString().charAt(u));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
	
			for( int r=0; r<newline.length();r++){
				try {
					schreibeTextFileSum.write((byte)newline.charAt(r));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}	
	}

	/**
	 * Generates text file for debug purpose.
	 * 
	 * @param daten double Value
	 * @param filename Name of the text file
	 */
	public void writeTXTFileDouble(double[] daten, String filename ){
		FileOutputStream schreibeTextFileSum = null;
		try {
			schreibeTextFileSum = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] tmpDaten = new String[daten.length];
		String newline= "\r\n";
		
		for(int s=0; s<daten.length; s++){
			tmpDaten[s]= (Double.toString(daten[s]));				
		}
		
		for(int h=0; h<tmpDaten.length; h++){
			for(int u=0; u < tmpDaten[h].length(); u++){
				try {
					schreibeTextFileSum.write((byte)tmpDaten[h].toString().charAt(u));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
	
			for( int r=0; r<newline.length();r++){
				try {
					schreibeTextFileSum.write((byte)newline.charAt(r));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}	
	}
	
	/**
	 * Generates text files of tValue for debug purpose.
	 */
	public void writetValueFiles(){
		double[] pTemptValue = new double[tValue.length];
		System.out.println("TV: " + tValue.length);
		
		for(int i = 0; i<FREQ.length; i++){
			for(int m=0; m<tValue.length; m++){
				pTemptValue[m] = (double)tValue[m][i];
			}
			writeTXTFileDouble(pTemptValue, "TV" + i + ".txt");
		}
		
	}
	
	public byte[] getByteData() {
		return byteData;
	}

	public AlgRLC getAlgRLC() {
		return algRLC;
	}
	
	
	
	
	
}
