package dsp;

import java.util.ArrayList;
import java.util.List;

import fft.FFTHelper;

/**
 * This abstract class represents the base functionality for all classes which provides signals.
 * Signals will be sent to all classes which are registered as listeners.
 * Copied and edited for our purpose from the EKG Projekt from Leo Kohler
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public abstract class SignalProvider 
{
//        Vector listeners = new Vector();
        List<SignalListener> listeners = new ArrayList<SignalListener>();

        /**
         * Adds a SignalListener to this class.
         * @param signalListener class which implements the interface SignalListener.
         */
        public synchronized void addSignalListener(SignalListener signalListener) 
        {
                listeners.add(signalListener);
        }

        /**
         * Removes a SignalListener from this class.
         * @param signalListener class which implements the interface SignalListener.
         */
        public synchronized void removeSignalListener(SignalListener signalListener) 
        {
        		listeners.remove(signalListener);
        }

        /**
         * Sends array of doubles to all listeners.
         * @param signalLeft array of signal values as doubles.
         */
        public synchronized void sendSignal(double[] signalLeft, double[] signalRight) 
        {
                SignalEvent event = new SignalEvent(this);
                event.setSignal(signalLeft, signalRight);
                for (SignalListener sl : listeners) 
                {
					sl.signalReady(event);
				}
        }
        
        /**
         * Sends array of doubles to all listeners.
         * @param signalLeft array of signal values as doubles.
         */
        public synchronized void sendSignal(double[] signalLeft, double[] signalRight, double[] signalFFT) 
        {
                SignalEvent event = new SignalEvent(this);
                event.setSignal(signalLeft, signalRight, signalFFT);
                for (SignalListener sl : listeners) 
                {
					sl.signalReady(event);
				}
        }
        
        /**
         * Sends array of doubles to all listeners.
         * @param signalLeft array of signal values as doubles.
         */
        public synchronized void sendSignal(double[] signalLeft, double[] signalRight, FFTHelper fftHelperObj) 
        {        	
                SignalEvent event = new SignalEvent(this);
                event.setSignal(signalLeft, signalRight, fftHelperObj);
                for (SignalListener sl : listeners) 
                {
					sl.signalReady(event);
				}
        }
}
