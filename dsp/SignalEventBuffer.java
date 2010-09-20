package dsp;

import java.util.LinkedList;

/**
 * The SignalEventBuffer is the part between the Input and the TraceCanvas
 * It receives and buffers the samples from the input. The TraceCanvas get
 * the samples with polling.
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class SignalEventBuffer implements SignalListener 
{
	private LinkedList<Integer> llleftSamples = new LinkedList<Integer>();
	private LinkedList<Integer> llrightSamples = new LinkedList<Integer>();
	private LinkedList<Integer> llFFTSamples = new LinkedList<Integer>();
	private int maxSize = 570;
	private int maxSizeFFt = 200;
	private int fftSize = 1024; // Number of points of the FFT.
	private SignalEvent event;
	
	private int[] leftSamplesBuffer = new int[1140];//TODO: 570 dynamisch
	private int[] rightSamplesBuffer = new int[1140];//TODO: 570 dynamisch
	private int[] fFTSamplesBuffer = new int[fftSize];//TODO: 570 dynamisch
	private boolean unlocked = true;
	private int cnt = 0;
	
	private int zoomX = 1;
	private int zoomY = 1;
	
	public void signalReady(SignalEvent event) 
	{		
		this.event = event;
		setSignals(event);
		unlocked = true;
	}
	
	public SignalEvent getEventObj()
	{
		return this.event;
	}
	
	//fill signals from event into linked list
	private synchronized void setSignals(SignalEvent event)
	{
		for(int i = 0;i < event.getLeftSignal().length; i++)
		{
			if(llleftSamples.size() < maxSize/zoomX)
			{
				llleftSamples.add((int) (100 - event.getLeftSignal()[i] * 100 * zoomY ));
				llrightSamples.add((int) (100 - event.getRightSignal()[i] * 100 * zoomY));
			}
			else
			{
				llleftSamples.removeFirst();
				llleftSamples.add((int) (100 - event.getLeftSignal()[i] * 100  * zoomY));
				llrightSamples.removeFirst();
				llrightSamples.add((int) (100 - event.getRightSignal()[i] * 100  * zoomY));
			}
		}
		
		for(int i=0;i<event.getfftHelperObj().getFFTSignal().length;i++)
		{		
			if(llFFTSamples.size() < (fftSize/2)/zoomX)
			{
				llFFTSamples.add((int) (maxSizeFFt * event.getfftHelperObj().getFFTSignal()[i] * zoomY ));
			}
			else
			{
				llFFTSamples.removeFirst();
				llFFTSamples.add((int) (maxSizeFFt * event.getfftHelperObj().getFFTSignal()[i] * zoomY));
			}	
		}
		cnt++;
	}

	// after 10 times of set signals, getLeft and getRightSignals gets unlocked for trace canvas
	public boolean getlock()
	{
		if(cnt > 10)
		{
			cnt = 0;
			return unlocked;
		}
		return false;
	}
	
	public synchronized int[] getLeftSignals()
	{
		calcGuiLeftValues();
		unlocked = false;
		return leftSamplesBuffer;	
	}

	public synchronized int[] getRightSignals()
	{
		calcGuiRightValues();
		unlocked = false;
		return rightSamplesBuffer;	
	}
	
	public synchronized int[] getFFTSignals()
	{
		calcGuiFFTValues();
		unlocked = false;
		return fFTSamplesBuffer;	
	}
	
	public void calcGuiFFTValues()
	{
		int i = 0;
		for(Integer value: llFFTSamples)
		{
			fFTSamplesBuffer[2*i] = i*zoomX;
			fFTSamplesBuffer[2*i+1] = (maxSizeFFt-value);
			i++;
		}
	}
	
	public void calcGuiLeftValues()
	{
		int i = 0;
		for(Integer value: llleftSamples){
			leftSamplesBuffer[2*i] = i*zoomX;
			leftSamplesBuffer[2*i+1] = value;
			i++;
		}
	}

	public void calcGuiRightValues()
	{
		int i = 0;
		for(Integer value: llrightSamples){
			rightSamplesBuffer[2*i] = i*zoomX;
			rightSamplesBuffer[2*i+1] = value;
			i++;
		}
	}

	public void setZoomX(int zoomX) 
	{
		this.zoomX = zoomX;
	}

	public void setZoomY(int zoomY) 
	{
		this.zoomY = zoomY;
	}
}