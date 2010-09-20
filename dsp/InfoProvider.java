package dsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This abstract class represents the base functionality for all classes which provides signals.
 * Signals will be sent to all classes which are registered as listeners.
 * Copied and edited for our purpose from the EKG Projekt from Leo Kohler
 * 
 * This class is a part of the Project "RLC-Messbruecke fuer 5 Franken"
 * University of Applied Sciences Lucerne -  Switzerland
 * @author Martin Klaper & Christian Kohler & Jo�l Gruber
 */
public abstract class InfoProvider 
{
//        Vector listeners = new Vector();
        List<InfoListener> listeners = new ArrayList<InfoListener>();

        /**
         * Adds a SignalListener to this class.
         * @param infoListener class which implements the interface SignalListener.
         */
        public void addInfoListener(InfoListener infoListener) 
        {
                listeners.add(infoListener);
        }

        /**
         * Removes a SignalListener from this class.
         * @param infoListener class which implements the interface SignalListener.
         */
        public void removeInfoListener(InfoListener infoListener) 
        {
                listeners.remove(infoListener);
        }

        /**
         * Sends array of doubles to all listeners.
         * @param value signal array of signal values as doubles.
         */
        public void sendInfos(ComponentTyp typ, double value, Unit unit, Map<String, Double> additonalInfos)
        {
                InfoEvent event = new InfoEvent(this);
                event.setInfo(typ, value, unit, additonalInfos);
                for (InfoListener sl : listeners) 
                {
					sl.infoReady(event);
				}
        }
}
