package io;

import java.io.IOException;
import javax.sound.sampled.SourceDataLine;

import dsp.AlgTyp;

/**
 * This class writes the byteBuffer which contains the sinussignal to the soundcard
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class Output implements Runnable
{	
	private boolean running;
	private int byteBufferlength;
	private byte[] byteBuffer;
	private SourceDataLine line = null;
	private AlgTyp algTyp;
	
	public Output(DeviceChoice dc, AlgTyp palgTyp)
	{
		line = dc.getSourceDataLine();
		line.start();
		running =true;
		algTyp = palgTyp;
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
				this.write();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(algTyp.equals(AlgTyp.Ohmmeter))
			{
				running = false;
			}
		}
		line.drain();
		line.flush();
		line.stop();
	}
	
	public void write() throws IOException
	{
		line.write(this.byteBuffer, 0, byteBufferlength);
	}

	public void setByteBuffer(byte[] byteBuffer) 
	{
		this.byteBuffer = byteBuffer;
		this.byteBufferlength = byteBuffer.length;
	}
}
