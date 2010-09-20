package dsp;

import org.eclipse.swt.widgets.Display;

/**
 * The AlgController handles the algorithm. It receives the samples.
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public class AlgController extends InfoProvider implements SignalListener, Runnable 
{
	private boolean running;

	private Object lock;

	private Alg alg;

	private int byteBufferSize;

	private int samplesNumber;

	private int countSamples = 0;

	private boolean calculating;
	
	private AlgTyp algTyp;

	private boolean loop;

	private double[] samplesLeft;

	private double[] samplesRight;

	private double[] samplesLeftCollected;

	private double[] samplesRightCollected;

	public AlgController(Object lock, int byteBufferSize, int samplesNumber, boolean loop) 
	{
		this.lock = lock;
		this.byteBufferSize = byteBufferSize;
		this.samplesNumber = samplesNumber;
		this.loop = loop;
		samplesLeft = new double[byteBufferSize / 4];
		samplesRight = new double[byteBufferSize / 4];
		samplesLeftCollected = new double[samplesNumber];
		samplesRightCollected = new double[samplesNumber];
		this.running = true;
	}

	public synchronized void terminate() 
	{
		running = false;
	}

	public synchronized boolean isRunning() {
		return running;
	}

	public void run() 
	{
		while (isRunning()) 
		{
			if(algTyp.equals(AlgTyp.Linearkombiner))  
			{
                // @MK mutex retained
			    synchronized (lock) 
			    {
				    try
				    {
						lock.wait();
					} 
				    catch(InterruptedException e) 
				    {
						e.printStackTrace();
					}
			    }
                // end mutex retained @MK
		    }
			if (loop) {
				Display.getDefault().readAndDispatch();
				calculating = true;
				alg.determine(samplesLeftCollected, samplesRightCollected, samplesNumber);
				Display.getDefault().readAndDispatch();
				sendInfos(alg.getTyp(), alg.getValue(), alg.getUnit(), alg.getAdditonalInfos());
				calculating = false;
			} else {
				if(algTyp.equals(AlgTyp.Linearkombiner)){
				alg.determine(samplesLeftCollected, samplesRightCollected,samplesNumber);}
				sendInfos(alg.getTyp(), alg.getValue(), alg.getUnit(), alg.getAdditonalInfos());
				terminate();
			}
		}
	}

	public void setAlgo(Alg alg) {
		this.alg = alg;
	}
	
	public void setAlgTyp(AlgTyp palgTyp){
		this.algTyp = palgTyp;
	}
	
	
	public void signalReady(SignalEvent event) 
	{
		samplesLeft = event.getLeftSignal();
		samplesRight = event.getRightSignal();
		if (!calculating) {
			// collect samples
			System.arraycopy(samplesLeft, 0, samplesLeftCollected,
					countSamples, byteBufferSize / 4);
			System.arraycopy(samplesRight, 0, samplesRightCollected,
					countSamples, byteBufferSize / 4);
			countSamples = countSamples + byteBufferSize / 4;
			if (countSamples >= samplesNumber) {
				countSamples = 0;
				synchronized (lock) {
					lock.notify();
				}
			}
		}
	}

}
