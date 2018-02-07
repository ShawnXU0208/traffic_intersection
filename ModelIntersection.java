package traffic.diy;


import javax.swing.JOptionPane;

import traffic.core.Intersection;
import traffic.core.Phase;
import traffic.core.TrafficStream;
import traffic.misc.Detector;
import traffic.misc.RandomDetector;
import traffic.phaseplan.FullyActuatedPhasePlan;
import traffic.phaseplan.PhasePlan;
import traffic.phaseplan.PretimedPhasePlan;
import traffic.signal.SignalFace;
import traffic.util.State;
import traffic.util.TrafficDirection;

/**
 * "Manually" create an intersection by assembling the various elements. Use the
 * classes provided in the traffic packages to construct an intersection which
 * can be displayed in the monitor.
 *
 */
public class ModelIntersection {

	/**
	 * A demo intersection made fusing the packages provided.  It has 
	 * one or more pre-timed phase plans.
	 * @return the intersection I made.
	 */
	
	
	public static Intersection preTimedIntersection() {
		String name = "Creyke & Clyde Roads";
		String description = "Note Right turn arrow for southbound traffic on Clyde Road (2-phase, pretimed";
		Intersection intersection = new Intersection(name, description);
		
		PretimedPhasePlan phasePlan = new PretimedPhasePlan();
		
		//create phases pbjects
		Phase phase1 = new Phase("EW", "includes all turning streams");
		Phase phase2 = new Phase("EW", "includes all turning streams");
		Phase phase3 = new Phase("NS-R", "includes protected right turn");
		Phase phase4 = new Phase("NS-Ry", "protected right turn ending");
		Phase phase5 = new Phase("NS-RX", "Right turn prohibited");
		Phase phase6 = new Phase("NS", "includes all turning streams");
		Phase phase7 = new Phase("NS-Y", "NS ending");
		
		//create traffic streams objects
		TrafficStream stream1 = new TrafficStream("N->W", "N->W(North on Clyde, turning west)");
		TrafficStream stream2 = new TrafficStream("N->S|E", "North on Clyde, continuing south or turning east");
		TrafficStream stream3 = new TrafficStream("N->S|E|W", "(North on Clyde, continuing south or turning east or west)");
		TrafficStream stream4 = new TrafficStream("S->N|E|W", "(South on Clyde, coninuing North or turning east or west)");
		TrafficStream stream5 = new TrafficStream("W->E|N|S", "(West on Cryke, continuing east or turning north or south)");
		TrafficStream stream6 = new TrafficStream("W->W|N|S", "(East on Cryke, continuing west or turning north or south)");
		
		//create signal faces objects
		SignalFace signalFace1 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.NORTH, SignalFace.STANDARD);
		SignalFace signalFace2 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.NORTH, SignalFace.STANDARD);
		SignalFace signalFace3 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.NORTH, SignalFace.STANDARD);
		SignalFace signalFace4 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.NORTH, SignalFace.RIGHT_ARROW);
		
		SignalFace signalFace5 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.SOUTH, SignalFace.STANDARD);
		SignalFace signalFace6 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.SOUTH, SignalFace.STANDARD);
		SignalFace signalFace7 = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.SOUTH, SignalFace.STANDARD);
		
		SignalFace signalFace8 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.EAST, SignalFace.STANDARD);
		SignalFace signalFace9 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.EAST, SignalFace.STANDARD);
		SignalFace signalFace10 = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.EAST, SignalFace.STANDARD);
		
		SignalFace signalFace11 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.WEST, SignalFace.STANDARD);
		SignalFace signalFace12 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.WEST, SignalFace.STANDARD);
		SignalFace signalFace13 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.WEST, SignalFace.STANDARD);
		
		//add traffic streams to all phases
		phase1.addStream(stream1, State.RED);
		phase1.addStream(stream2, State.RED);
		phase1.addStream(stream3, State.OFF);
		phase1.addStream(stream4, State.RED);
		phase1.addStream(stream5, State.GREEN);
		phase1.addStream(stream6, State.GREEN);
		
		phase2.addStream(stream1, State.RED);
		phase2.addStream(stream2, State.RED);
		phase2.addStream(stream3, State.OFF);
		phase2.addStream(stream4, State.RED);
		phase2.addStream(stream5, State.YELLOW);
		phase2.addStream(stream6, State.YELLOW);
		
		phase3.addStream(stream1, State.GREEN);
		phase3.addStream(stream2, State.GREEN);
		phase3.addStream(stream3, State.OFF);
		phase3.addStream(stream4, State.RED);
		phase3.addStream(stream5, State.RED);
		phase3.addStream(stream6, State.RED);
		
		phase4.addStream(stream1, State.YELLOW);
		phase4.addStream(stream2, State.GREEN);
		phase4.addStream(stream3, State.OFF);
		phase4.addStream(stream4, State.GREEN);
		phase4.addStream(stream5, State.RED);
		phase4.addStream(stream6, State.RED);
		
		phase5.addStream(stream1, State.RED);
		phase5.addStream(stream2, State.GREEN);
		phase5.addStream(stream3, State.OFF);
		phase5.addStream(stream4, State.GREEN);
		phase5.addStream(stream5, State.RED);
		phase5.addStream(stream6, State.RED);
		
		phase6.addStream(stream1, State.OFF);
		phase6.addStream(stream2, State.OFF);
		phase6.addStream(stream3, State.GREEN);
		phase6.addStream(stream4, State.GREEN);
		phase6.addStream(stream5, State.RED);
		phase6.addStream(stream6, State.RED);
		
		phase7.addStream(stream1, State.OFF);
		phase7.addStream(stream2, State.OFF);
		phase7.addStream(stream3, State.YELLOW);
		phase7.addStream(stream4, State.YELLOW);
		phase7.addStream(stream5, State.RED);
		phase7.addStream(stream6, State.RED);
		
		
		//add the corresponding observers to traffic stream 
		stream1.addObserver(signalFace4);
		
		stream2.addObserver(signalFace1);
		stream2.addObserver(signalFace2);
		stream2.addObserver(signalFace3);
		
		stream3.addObserver(signalFace1);
		stream3.addObserver(signalFace2);
		stream3.addObserver(signalFace3);
		
		stream4.addObserver(signalFace5);
		stream4.addObserver(signalFace6);
		stream4.addObserver(signalFace7);
		
		stream5.addObserver(signalFace11);
		stream5.addObserver(signalFace12);
		stream5.addObserver(signalFace13);
		
		stream6.addObserver(signalFace8);
		stream6.addObserver(signalFace9);
		stream6.addObserver(signalFace10);
		
		//add phases to phase plan
		phasePlan.add(phase1);
		phasePlan.add(phase2);
		phasePlan.add(phase3);
		phasePlan.add(phase4);
		phasePlan.add(phase5);
		phasePlan.add(phase6);
		phasePlan.add(phase7);
		
		intersection.addPlan(phasePlan);
		
		//add signal faces to my intersection
		intersection.addSignalFace(signalFace1);
		intersection.addSignalFace(signalFace2);
		intersection.addSignalFace(signalFace3);
		intersection.addSignalFace(signalFace4);
		intersection.addSignalFace(signalFace5);
		intersection.addSignalFace(signalFace6);
		intersection.addSignalFace(signalFace7);
		intersection.addSignalFace(signalFace8);
		intersection.addSignalFace(signalFace9);
		intersection.addSignalFace(signalFace10);
		intersection.addSignalFace(signalFace11);
		intersection.addSignalFace(signalFace12);
		intersection.addSignalFace(signalFace13);
		
		return intersection;
	}
	
	/**
	 * A demo intersection made fusing the packages provided.  It has 
	 * one or more fully-actuated phase plans.
	 * @return the intersection I made.
	 */
	public static Intersection fullyActivatedIntersection() {
		String name = "Creyke & Clyde Roads";
		String description = "Note Right turn arrow for southbound traffic on Clyde Road.";
		Intersection intersection = new Intersection(name, description);
		
		FullyActuatedPhasePlan phasePlan = new FullyActuatedPhasePlan();
		
		//create phase objects 
		Phase phase1 = new Phase("EW", "includes all turning streams");
		Phase phase2 = new Phase("EW", "includes all turning streams");
		Phase phase3 = new Phase("NS-R", "includes protected right turn");
		Phase phase4 = new Phase("NS-Ry", "protected right turn ending");
		Phase phase5 = new Phase("NS-RX", "Right turn prohibited");
		Phase phase6 = new Phase("NS", "includes all turning streams");
		Phase phase7 = new Phase("NS-Y", "NS ending");
		
		//set the durations of each phase
		phase1.setMinGreenInterval(3);
		phase2.setMinGreenInterval(3);
		phase3.setMinGreenInterval(3);
		phase4.setMinGreenInterval(3);
		phase5.setMinGreenInterval(3);
		phase6.setMinGreenInterval(3);
		phase7.setMinGreenInterval(3);
		
		//create traffic streams objects
		TrafficStream stream1 = new TrafficStream("N->W", "N->W(North on Clyde, turning west)");
		TrafficStream stream2 = new TrafficStream("N->S|E", "North on Clyde, continuing south or turning east");
		TrafficStream stream3 = new TrafficStream("N->S|E|W", "(North on Clyde, continuing south or turning east or west)");
		TrafficStream stream4 = new TrafficStream("S->N|E|W", "(South on Clyde, coninuing North or turning east or west)");
		TrafficStream stream5 = new TrafficStream("W->E|N|S", "(West on Cryke, continuing east or turning north or south)");
		TrafficStream stream6 = new TrafficStream("W->W|N|S", "(East on Cryke, continuing west or turning north or south)");
		
		//create signal face objects
		SignalFace signalFace1 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.NORTH, SignalFace.STANDARD);
		SignalFace signalFace2 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.NORTH, SignalFace.STANDARD);
		SignalFace signalFace3 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.NORTH, SignalFace.STANDARD);
		SignalFace signalFace4 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.NORTH, SignalFace.RIGHT_ARROW);
		
		SignalFace signalFace5 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.SOUTH, SignalFace.STANDARD);
		SignalFace signalFace6 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.SOUTH, SignalFace.STANDARD);
		SignalFace signalFace7 = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.SOUTH, SignalFace.STANDARD);
		
		SignalFace signalFace8 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.EAST, SignalFace.STANDARD);
		SignalFace signalFace9 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.EAST, SignalFace.STANDARD);
		SignalFace signalFace10 = new SignalFace(TrafficDirection.NORTHWEST, TrafficDirection.EAST, SignalFace.STANDARD);
		
		SignalFace signalFace11 = new SignalFace(TrafficDirection.SOUTHWEST, TrafficDirection.WEST, SignalFace.STANDARD);
		SignalFace signalFace12 = new SignalFace(TrafficDirection.NORTHEAST, TrafficDirection.WEST, SignalFace.STANDARD);
		SignalFace signalFace13 = new SignalFace(TrafficDirection.SOUTHEAST, TrafficDirection.WEST, SignalFace.STANDARD);
		
		//create random detector objects
		RandomDetector detector1 = new RandomDetector();
		RandomDetector detector2 = new RandomDetector();
		RandomDetector detector3 = new RandomDetector();
		RandomDetector detector4 = new RandomDetector();
		RandomDetector detector5 = new RandomDetector();
		RandomDetector detector6 = new RandomDetector();

		//add traffic streams to all phases
		phase1.addStream(stream1, State.RED);
		phase1.addStream(stream2, State.RED);
		phase1.addStream(stream3, State.OFF);
		phase1.addStream(stream4, State.RED);
		phase1.addStream(stream5, State.GREEN);
		phase1.addStream(stream6, State.GREEN);
		
		phase2.addStream(stream1, State.RED);
		phase2.addStream(stream2, State.RED);
		phase2.addStream(stream3, State.OFF);
		phase2.addStream(stream4, State.RED);
		phase2.addStream(stream5, State.YELLOW);
		phase2.addStream(stream6, State.YELLOW);
		
		phase3.addStream(stream1, State.GREEN);
		phase3.addStream(stream2, State.GREEN);
		phase3.addStream(stream3, State.OFF);
		phase3.addStream(stream4, State.RED);
		phase3.addStream(stream5, State.RED);
		phase3.addStream(stream6, State.RED);
		
		phase4.addStream(stream1, State.YELLOW);
		phase4.addStream(stream2, State.GREEN);
		phase4.addStream(stream3, State.OFF);
		phase4.addStream(stream4, State.GREEN);
		phase4.addStream(stream5, State.RED);
		phase4.addStream(stream6, State.RED);
		
		phase5.addStream(stream1, State.RED);
		phase5.addStream(stream2, State.GREEN);
		phase5.addStream(stream3, State.OFF);
		phase5.addStream(stream4, State.GREEN);
		phase5.addStream(stream5, State.RED);
		phase5.addStream(stream6, State.RED);
		
		phase6.addStream(stream1, State.OFF);
		phase6.addStream(stream2, State.OFF);
		phase6.addStream(stream3, State.GREEN);
		phase6.addStream(stream4, State.GREEN);
		phase6.addStream(stream5, State.RED);
		phase6.addStream(stream6, State.RED);
		
		phase7.addStream(stream1, State.OFF);
		phase7.addStream(stream2, State.OFF);
		phase7.addStream(stream3, State.YELLOW);
		phase7.addStream(stream4, State.YELLOW);
		phase7.addStream(stream5, State.RED);
		phase7.addStream(stream6, State.RED);
		
		//add corresponding observers to each traffic stream
		stream1.addObserver(signalFace4);
		
		stream2.addObserver(signalFace1);
		stream2.addObserver(signalFace2);
		stream2.addObserver(signalFace3);
		
		stream3.addObserver(signalFace1);
		stream3.addObserver(signalFace2);
		stream3.addObserver(signalFace3);
		
		stream4.addObserver(signalFace5);
		stream4.addObserver(signalFace6);
		stream4.addObserver(signalFace7);
		
		stream5.addObserver(signalFace11);
		stream5.addObserver(signalFace12);
		stream5.addObserver(signalFace13);
		
		stream6.addObserver(signalFace8);
		stream6.addObserver(signalFace9);
		stream6.addObserver(signalFace10);
		
		//add detector to each stream
		stream1.addDetector(detector1);
		stream2.addDetector(detector2);
		stream3.addDetector(detector3);
		stream4.addDetector(detector4);
		stream5.addDetector(detector5);
		stream6.addDetector(detector6);
		
		phasePlan.add(phase1);
		phasePlan.add(phase2);
		phasePlan.add(phase3);
		phasePlan.add(phase4);
		phasePlan.add(phase5);
		phasePlan.add(phase6);
		phasePlan.add(phase7);
		
		intersection.addPlan(phasePlan);
		
		//add signal faces to my intersection
		intersection.addSignalFace(signalFace1);
		intersection.addSignalFace(signalFace2);
		intersection.addSignalFace(signalFace3);
		intersection.addSignalFace(signalFace4);
		intersection.addSignalFace(signalFace5);
		intersection.addSignalFace(signalFace6);
		intersection.addSignalFace(signalFace7);
		intersection.addSignalFace(signalFace8);
		intersection.addSignalFace(signalFace9);
		intersection.addSignalFace(signalFace10);
		intersection.addSignalFace(signalFace11);
		intersection.addSignalFace(signalFace12);
		intersection.addSignalFace(signalFace13);
		
		return intersection;
	}
}