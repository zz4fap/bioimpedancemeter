package fft;

import org.eclipse.swt.widgets.Display;

import fft.*;

import dsp.AlgTyp;
import dsp.InfoProvider;
import dsp.SignalEvent;
import dsp.SignalListener;
import java.io.*;

public class FFTHelper
{
	private Complex[] complexSignal;
	private Complex[] y;
	private int N;
	private FileOutputStream fos; 
	private DataOutputStream dos;
	private int counter = 0;
	private double[] absSignal;
	private double peakFreq = 0;
	private float samplingFreq;
	private int peakPos = 0;
	
	public FFTHelper(int N, String filename, float fs) 
	{
		this.N = N;
		complexSignal = new Complex[N];
		absSignal = new double[N/2];
		samplingFreq = fs;
		
	    try
	    {
	    	File file= new File(filename);
	    	fos = new FileOutputStream(file);
	    	dos = new DataOutputStream(fos);
	    }
	    catch (IOException e)
	    {
	    	System.out.println("IOException : " + e);
	    }
	}
	
	public double[] calculateFFT(double[] signal)
	{			
		double max = 0.0;
		
		for(int i=0; i<N; i++)
		{
			if(i < signal.length)
			{
				complexSignal[i] = new Complex(signal[i],0.0);
			}
			else
			{
				complexSignal[i] = new Complex(0.0,0.0);
			}
		}

		y = FFT.fft(complexSignal);
		
		if(counter == 0)
		{
			writeFFTtoFile();
			counter++;
		}
		
		calculateAbsSignal();
		max = getMaxAbsSignal();
		
		for(int i=0; i < (N/2); i++)
		{
			 absSignal[i] = absSignal[i]/max;
		}
		
		return absSignal;
	}
	
	public double[] getFFTSignal()
	{
		return absSignal;
	}
	
	private void calculateAbsSignal()
	{
		for(int i=0; i < (N/2); i++)
		{
			 absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
		}
	}
	
	private double getMaxAbsSignal()
	{
		double max = absSignal[0];
		peakPos = 0;
		
		for(int i=1; i < (N/2); i++)
		{
			 if(absSignal[i] > max)
			 {
				 max = absSignal[i];
				 peakPos = i;
			 }
		}
		
		peakFreq = peakPos*((double)samplingFreq/N);
		return max;
	}
	
	public int getPeakPosition()
	{
		return peakPos;
	}
	
	public double getPeakFrequency()
	{
		return peakFreq;
	}
	
	public int getFFTSize()
	{
		return N;
	}
	
	public float getSamplingFreq()
	{
		return samplingFreq;
	}
	
	private void writeFFTtoFile()
	{
		try
		{
			for(int i=0; i<y.length; i++)
			{
				dos.writeDouble(y[i].re());
				dos.writeDouble(y[i].im());
			}
		    dos.close();
		}
		catch (Exception e)
		{
			//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}