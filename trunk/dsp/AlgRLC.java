package dsp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * The AlgRLC is one of the two algorithm of this project. 
 * The cross-correlation is used to calculate the values.
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland 
 * HSR Hochschule für Technik, Rapperswil
 * @author Marcel Kluser
 */
public class AlgRLC extends Alg{
	// Variablen FreqSweep 	
	private final static int INPUT_IMPEDANCE_SOUNDCARD = 9000;
	private final static int RESISTANCE_CONNECTOR_BOX = 10000;
	private final static double  K = RESISTANCE_CONNECTOR_BOX *(RESISTANCE_CONNECTOR_BOX+INPUT_IMPEDANCE_SOUNDCARD)/2/(2*RESISTANCE_CONNECTOR_BOX + INPUT_IMPEDANCE_SOUNDCARD);
	private double samplingDuration;
	private double[] refVec = {10.8828,10.8458,10.3727,9.8328,10.0912,8.7845};		
	private double[] sumInputSignalDCFree;
	private double[] [] tValue;
	private double xcorrMax;
	private double [] var_vect = new double[refVec.length];
	private int[] freq; 
	private int samplingRate;
	private boolean isCalibration = false;
	
	//construktor
	public AlgRLC(int pSamplingRate, double pSamplingDuration, int[] pFreq) {
		this.samplingRate = pSamplingRate;
		this.samplingDuration = pSamplingDuration;
		this.freq = pFreq;
	}
	
	@Override
	public boolean determine(double[] signalLeft, double[] signalRight,int length){
		
		sumInputSignalDCFree = new double[(((int)(samplingDuration/(1.0/samplingRate)))/4)*8];
		double[] pTmpSignalLeft = new double[length];
		double[] pTmpSignalRight = new double[length];
		double[] pTmpSignalLeftDCFree = new double[length];
		double[] pTmpSignalRightDCFree = new double[length];
		
		double pTmpSummeLeft;
		double pTmpSummeRight;
		double pTmpAverageLeft;
		double pTmpAverageRight;

			//sum of input signal (DC free)
			for(int i=0; i<signalLeft.length; i++){
				pTmpSignalLeft[i] = signalLeft[i];
				pTmpSignalRight[i] = signalRight[i];
			}
				
			pTmpSummeLeft = 0;
			pTmpSummeRight = 0;

			for(int m=0; m<pTmpSignalLeft.length;m++){
				pTmpSummeLeft += pTmpSignalLeft[m];
				pTmpSummeRight += pTmpSignalRight[m];
			}
	
			pTmpAverageLeft = pTmpSummeLeft/(pTmpSignalLeft.length);
			pTmpAverageRight = pTmpSummeRight/(pTmpSignalRight.length);
			
			for(int k=0; k<pTmpSignalLeft.length;k++){
				pTmpSignalLeftDCFree[k] = pTmpSignalLeft[k]- pTmpAverageLeft;
				pTmpSignalRightDCFree[k] = pTmpSignalRight[k]- pTmpAverageRight;	
			}
			
			for(int l=0, s=0; l<pTmpSignalLeftDCFree.length;l++, s++){
				sumInputSignalDCFree[s] = pTmpSignalLeftDCFree[l] + pTmpSignalRightDCFree[l];				
			}				
			//writeTXTFile(sumInputSignalDCFree, "javadtot.txt");								
			
			//Calculate cross-correlation 				
			for(int i=0; i<6; i++){
				calculateKKF(i+1);
				this.var_vect[i] = xcorrMax;
			}	
				
			//Calculate RLC Value
			if(!isCalibration){calculateRLCFromVar_vect();}
				
		return true;
		}
	
	/**
	 * Calculate RLC value from variable vector.
	 */
	public void calculateRLCFromVar_vect(){
		
		double[] Zelementwise;
		double[] Zimag_elementwise;
		double[] Lelementwise;
		double[] Celementwise;
		double residual_Z;
		double residual_L;
		double residual_C;
		double meanZZelementwise = 0;
		double meanLelementwise = 0;
		double meanLLelementwise = 0;
		double meanCelementwise = 0;
		double meanCCelementwise = 0;
		double[] Z;
		double[] L;
		double[] C;
		double sumVar_Vect = 0;
		double sumRefVecVar_Vect = 0;
			
		Zelementwise = new double[var_vect.length];
		Zimag_elementwise = new double[var_vect.length];
		Lelementwise = new double[var_vect.length];
		Celementwise = new double[var_vect.length];
		Z = new double[var_vect.length];
		L = new double[var_vect.length];
		C = new double[var_vect.length];
					
		//Berechnung residual_Z
		for(int i=0; i<var_vect.length; i++){
			sumVar_Vect +=var_vect[i];	
			sumRefVecVar_Vect += (refVec[i] - var_vect[i]);
			Zelementwise[i] = K * var_vect[i]/(refVec[i]-var_vect[i]);
			
		}
		
		for(int i=0; i<var_vect.length; i++){
			Z[i] = K * sumVar_Vect / sumRefVecVar_Vect;
		}
		
		for(int i=0; i<Z.length; i++){
			meanZZelementwise +=Math.pow(Z[i]-Zelementwise[i] , 2);			
		}
		residual_Z =  (meanZZelementwise/var_vect.length) / Math.pow(Z[0], 2);
		
		
		//Berechnung residual_L
		for(int i=0; i<var_vect.length; i++){
			Zimag_elementwise[i] = K * var_vect[i] / Math.sqrt(Math.abs(Math.pow(refVec[i], 2)- Math.pow(var_vect[i], 2)));		
		}
		
		for(int i=0; i<var_vect.length; i++){
			Lelementwise[i] = Zimag_elementwise[i] / (2 * Math.PI * freq[i+1]);	
		}
		
		for(int i=0; i<var_vect.length; i++){
			meanLelementwise += Lelementwise[i];
		}
		
		for(int i=0; i<var_vect.length; i++){
			L[i] = meanLelementwise/var_vect.length;
		}
		
		for(int i=0; i<var_vect.length; i++){
			meanLLelementwise += Math.pow(L[i]-Lelementwise[i],2 );
		}
		residual_L = (meanLLelementwise/var_vect.length) / Math.pow(L[0], 2);
		
		
		//Berechnung residual_C
		for(int i=0; i<var_vect.length; i++){
			Celementwise[i] = 1/(2*Math.PI*freq[i+1])/Zimag_elementwise[i];		
		}
		
		for(int i=0; i<var_vect.length; i++){
			meanCelementwise += Celementwise[i];
		}
		
		for(int i=0; i<var_vect.length; i++){
			C[i] = meanCelementwise/var_vect.length;
		}
		
		for(int i=0; i<var_vect.length; i++){
			meanCCelementwise += Math.pow(C[i]-Celementwise[i],2 );
		}
		residual_C = (meanCCelementwise/var_vect.length) / Math.pow(C[0], 2);
		
		
		//element is most like an inductivity
		if(residual_L<0.1){
			System.out.println("Inductor");
			setTyp(ComponentTyp.INDUCTOR);
			//L in the mH range
			if(L[0]>1E-3){
				setUnit(Unit.mH);
				setValue(((double)Math.round(L[0]*1E4))/10);
				System.out.println(((double)Math.round(L[0]*1E4))/10 + " mH");
			}
			//L in the uH range
			else if (L[0]>1E-6) {
				setUnit(Unit.uH);
				setValue(((double)Math.round(L[0]*1E7))/10);
				System.out.println(((double)Math.round(L[0]*1E7))/10 + " uH");
			}
			//L in the nH range
			else {
				setUnit(Unit.nH);
				setValue(((double)Math.round(L[0]*1E10))/10);
				System.out.println(((double)Math.round(L[0]*1E10))/10 + " nH");
			}
			System.out.println("");
		}
		
		//element is most like an capacitor
		else if(residual_C<0.1){
			System.out.println("Capacitor");
			setTyp(ComponentTyp.CAPACITOR);
			//C in the uF range
			if(C[0]>1E-6){
				setUnit(Unit.uF);
				setValue(((double)Math.round(C[0]*1E7))/10);
				System.out.println(((double)Math.round(C[0]*1E7))/10 + " uF");
			}
			//C in the nF range
			else if (C[0]>1E-9) {
				setUnit(Unit.nF);
				setValue(((double)Math.round(C[0]*1E10))/10);
				System.out.println(((double)Math.round(C[0]*1E10))/10 + " nF");
			}
			//C in the pF range
			else {
				setUnit(Unit.pF);
				setValue(((double)Math.round(C[0]*1E13))/10);
				System.out.println(((double)Math.round(C[0]*1E13))/10 + " pF");
			}
			System.out.println("");
		}
		
		//element is most like an resistor 
		else if(residual_Z<1){
			//Z unendlich
			//System.out.println(Z[0]);
			System.out.println("Resistor ");
			setTyp(ComponentTyp.RESISTOR);
			if (Z[0]<0){
				setUnit(Unit.unendlich);
				System.out.println("Unendlich");
			}
			//Z in the kOhm range
			else if(Z[0]>1E3){
				setUnit(Unit.kOhm);
				setValue(((double)Math.round(Z[0]/1E1))/100);
				System.out.println(((double)Math.round(Z[0]/1E1))/100 + " kOhm");
			}
			//Z in the Ohm range  
			else {
				setUnit(Unit.Ohm);
				setValue(((double)Math.round(Z[0])));
				System.out.println(((double)Math.round(Z[0]))+ " Ohm");
			}
			System.out.println("");
			
		}
		
		//probably not connected
		else {
			
			setUnit(Unit.unendlich);
			System.out.println("unendlich oder nicht angeschlossen");
		}
		
	}
	
	/**
	 * Cross-correlation of input signal
	 * 
	 * @param pVect Vector of sine signal
	 */
	public void  calculateKKF (int pVect){
		int n, m;
		int kkf_max = 0;
		double[] kkf = new double[(sumInputSignalDCFree.length)- tValue.length]; 

		for (n = (tValue.length*(pVect-1)); n < ((tValue.length*(pVect-1))+(2*tValue.length)); n++) 
		{
			kkf[n] = 0;	
			for (m = 0; m < tValue.length; m++){								
																							
				kkf[n] += sumInputSignalDCFree[n + m] * tValue[m][pVect];
			}
			if (kkf[n] > kkf[kkf_max]){
				kkf_max = n;
			}		
		}	
		System.out.println("var_vect_max: " + kkf[kkf_max]);
		xcorrMax = kkf[kkf_max];			
	}
	
	public void settValue(double[][] tValue){
		this.tValue = tValue;		
	}
	
	/**
	 * Set reference vector after calibration
	 */
	public void setRefVec() {		
		System.arraycopy(this.var_vect, 0, this.refVec, 0, this.var_vect.length);
	}
	
	public void setCalibration(boolean isCalibration) {
		this.isCalibration = isCalibration;
	}

	/**
	 * Generates text file for debug purpose.
	 * 
	 * @param daten double Value
	 * @param filename Name of the text file
	 */
	public void writeTXTFile(double[] daten, String filename ) {
		FileOutputStream schreibeTextFileSum = null;
		try {
			schreibeTextFileSum = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
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
					e.printStackTrace();
				}
			}	
	
			for( int r=0; r<newline.length();r++){
				try {
					schreibeTextFileSum.write((byte)newline.charAt(r));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}	
	}
	
}


