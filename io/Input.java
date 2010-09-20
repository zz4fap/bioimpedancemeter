package io;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;

import dsp.AlgTyp;
import dsp.Application;
import dsp.SignalProvider;

import fft.FFT;
import fft.Complex;
import fft.FFTHelper;

/**
 * The Input Thread get data from the input-line from the soundcard and
 * send these to the listeners (algController and TraceCanvas).
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber & Marcel Kluser
 */
public class Input extends SignalProvider implements Runnable 
{
	
	private boolean running;

    private int byteBufferSize;
    private TargetDataLine line = null;  
    private int sizeLSamples = 141120; //samples to read for ohmmeter algorithm
    private double[] lSamples;
    private double[] rSamples;
    private double[] absSignal; 
    private AlgTyp algTyp;
    private Application appl;
    private boolean startFiltering = false;
    private boolean leftFiltering = false;
    private boolean rightFiltering = false;
    private int numOfTaps = 1;
    private String filename = "/home/zz4fap/Desktop/fft.dat";
	private FFTHelper fft;

    public Input(Object lock, DeviceChoice dc, AudioFormat audioFormat, int byteBufferSize, AlgTyp palgTyp, Application pAppl)
    {
    	this.byteBufferSize = byteBufferSize;
        line = dc.getTargetDataLine();
        line.start();
        running = true;
        algTyp = palgTyp;
        appl = pAppl;
        if(algTyp.equals(AlgTyp.Ohmmeter))
        {
        	this.byteBufferSize = sizeLSamples;
        }
        lSamples = new double[this.byteBufferSize/4];
        rSamples = new double[this.byteBufferSize/4];
        
        fft = new FFTHelper(1024,filename, audioFormat.getSampleRate());
    }
    
    public synchronized void terminate() 
    {
    	if(running == true)
    		running = false;
    }
    
    public synchronized boolean isRunning() 
    {
		return running;
	}
    
	public void run() 
	{
		while(isRunning())
		{
			try 
			{
				this.read();
				fft.calculateFFT(rSamples);
				//System.out.println("AQUI!!!!! - 1");
				sendSignal(lSamples, rSamples, fft);
				if(algTyp.equals(AlgTyp.Ohmmeter))
				{
					appl.setRLCDataSamples(lSamples, rSamples);
					running = false;
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
    
    public void read() throws IOException 
    {
        byte tempBuffer[] = new byte[byteBufferSize];
        int cnt = line.read(tempBuffer, 0, byteBufferSize);
            if(cnt > 0)
            {
              //save data to samples left and right
              for(int i=0; i < byteBufferSize/4-3; i++)
              {
            	  if(algTyp.equals(AlgTyp.Ohmmeter))
            	  {
            		  lSamples[i] = ( (tempBuffer[4*i] & 0xFF)| (tempBuffer[4*i +1] << 8) )/ 32768.0F;
            		  rSamples[i] = (( (tempBuffer[4*i +2] & 0xFF)| (tempBuffer[4*i +3] << 8) )/ 32768.0F);
            	  }
            	  else
            	  {
            		  lSamples[i] = ( (tempBuffer[4*i] & 0xFF)| (tempBuffer[4*i +1] << 8) )/ 32768.0F;
            		  rSamples[i] = (( (tempBuffer[4*i +2] & 0xFF)| (tempBuffer[4*i +3] << 8) )/ 32768.0F)- lSamples[i];
            	  }  
              }
            }
    }
    
    public boolean startFiltering()
    {
    	if(startFiltering)
    	{
    		startFiltering = false;
    	}
    	else
    	{
    		startFiltering = true;
    	}
    	return startFiltering;
    }
    
    public boolean enableLeftFiltering()
    {
    	if(leftFiltering)
    	{
    		leftFiltering = false;
    	}
    	else
    	{
    		leftFiltering = true;
    	}
    	System.out.println("leftFiltering: "+leftFiltering);
    	return leftFiltering;
    }
    
    public boolean enableRightFiltering()
    {
    	if(rightFiltering)
    	{
    		rightFiltering = false;
    	}
    	else
    	{
    		rightFiltering = true;
    	}
    	System.out.println("rightFiltering: "+rightFiltering);
    	return rightFiltering;
    }
    
    public void setTaps(int taps)
    {
    	numOfTaps = taps;
    }
}
