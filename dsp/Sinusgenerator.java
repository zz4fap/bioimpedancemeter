package dsp;

import io.Output;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;

/**
 * Generates the sinussignal used from the output.
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class Sinusgenerator
{
	private int frequency;
	private double sampling_rate;
	private Output output;
	private double t = 0;
	private byte[] byteData;
	private int byteBufferSize;
	private double amplitudeMultiplicator = 0.8;
	private int rounds = 100;
	
	public Sinusgenerator(Output output, AudioFormat audioformat)
	{
		this.output = output;
		this.sampling_rate = (double)audioformat.getSampleRate();
	}
	
	public void setFrequency(int frequency)
	{
		this.frequency = frequency;
		this.byteBufferSize = (int)Math.round(sampling_rate/frequency*4*rounds);
		this.byteBufferSize = (this.byteBufferSize / 4) * 4 + 4;
		System.out.println("bytebuffersize: " + this.byteBufferSize);
		this.byteData = new byte[byteBufferSize];
		double value;
		for(int i = 0; i < byteBufferSize-3; i=i+4)
		{
			//sinusoidal value
			value = Math.sin( 2 * Math.PI * this.frequency * t ) * this.amplitudeMultiplicator;
			t= t + (1.0/this.sampling_rate);
			//saturation
			value = Math.min(1.0, Math.max(-1.0, value));
			//scaling and conversion to integer
			int nSample = (int) Math.round(value * 32767.0);
			byte low = (byte) (nSample & 0xFF);
			byte high = (byte) ((nSample >> 8) & 0xFF);
			byteData[i] = low;
			byteData[i+1] = high;
			byteData[i+2] = low;
			byteData[i+3] = high;
		}
		//write the buffer to the output object
		output.setByteBuffer(byteData);
	}
	
	public int getByteBufferSize()
	{
		return this.byteBufferSize;
	}
	
	public int read(byte[] b) throws IOException 
	{
    	System.arraycopy(byteData,0,b,0,byteBufferSize);
		return byteBufferSize/4;
    }
	
}
