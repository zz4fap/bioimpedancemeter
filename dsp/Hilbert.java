package dsp;

/**
 * Transforms the signal in a real and a imaginary signal. For a sine signal
 * the imaginary signal is a sine signal with 90 degree delay.
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class Hilbert 
{    
    private double[] cacheBuffer;
    private double[] realBuffer;
    private double[] imagBuffer;
    private int periodSize;
    public Hilbert(int periodSize) {
        this.periodSize = periodSize;
        cacheBuffer = new double[periodSize/4];
    }
  
    public double[] getReal() {
        return realBuffer;
    }
    
    public double[] getImag() {
        return imagBuffer;
    }
    
    /**
     * Calculates the real and imag Signal from the source signal
     * @param sourceBuffer
     * @param bufferSize
     */
    public void calculate(double[] sourceBuffer, int bufferSize) 
    {
    	realBuffer = new double[bufferSize];
        imagBuffer = new double[bufferSize];

        for(int i=0; i < (bufferSize); i++) 
        {
            realBuffer[i] = sourceBuffer[i];
            if(i < periodSize/4) 
            {
                imagBuffer[i] = cacheBuffer[i];
            }
            else 
            {
                imagBuffer[i] = sourceBuffer[i - periodSize/4]; 
            }
            // cut a quarter of a period
            if(i < (periodSize/4)) 
            {
                cacheBuffer[i] = sourceBuffer[periodSize - periodSize/4 + i];
            }
        }
    }
}
