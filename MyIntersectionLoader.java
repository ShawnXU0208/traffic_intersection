package traffic.diy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;


import traffic.core.Intersection;
import traffic.core.Phase;
import traffic.core.TrafficStream;
import traffic.load.TrafficIOException;
import traffic.load.TrafficSyntaxException;
import traffic.misc.Detector;
import traffic.misc.RandomDetector;
import traffic.phaseplan.FullyActuatedPhasePlan;
import traffic.phaseplan.PhasePlan;
import traffic.phaseplan.PretimedPhasePlan;
import traffic.util.State;
import traffic.util.TrafficDirection;
import traffic.signal.SignalFace; 
import traffic.load.Tag;
import traffic.load.TrafficException;

/**
 * Read an intersection description file and build an intersection from the data
 * it contains.
 *
 */
public class MyIntersectionLoader {
	
	//those are boolean variables for checking whether the processor has done each part
	//of the intersection transcript.
	private boolean intersectionComplete = false;
	private boolean trafficStreamComplete = false;
	private boolean phasePlanComplete = false;
	private boolean phaseComplete = false;
	private boolean signalFacesComplete = false;
	
	private String pretimedPhasePlan = "phase plan = pretimed phase plan";
	private String fullyActuatedPhasePlan = "phase plan = fully actuated phase plan";
	private boolean fullyActuated = false;
	
	private String intersectionName;
	private String intersectionDes;
	
	//An array list for storing the traffic stream read from the file.
	private ArrayList<TrafficStream> myTrafficStreams = new ArrayList<TrafficStream>();
	
	private BufferedReader myBufferedReader;
	private Intersection myIntersection;
	
	/**
	 * Constructor for class.
	 * 
	 * @param br
	 *            where to read data from.
	 */
	public MyIntersectionLoader(BufferedReader br) {
		myBufferedReader = br;
	}

	
	/**
	 * Build intersection from description in file. Read line at a time and
	 * process rather than parse using grammar.
	 * 
	 * @return the intersection or null if something went wrong
	 * @throws TrafficException 
	 */
	public Intersection buildIntersection() {
		
		try{
			processTranscript();
		}catch(TrafficException e){
			e.printStackTrace();
		}
	
		try {
			myBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return myIntersection;
	}
	
	
	//These two functions are used to read one useful line of the file (skip empty lines and comment lines)
	private String readNextUsefulLine(){
		String currentLineText = null;
		try {
			currentLineText = myReadLine();
		}catch (IOException|TrafficIOException e) {
			e.printStackTrace();
			System.out.println("unable to find name and description for intersection");
		}
		return currentLineText;
	}
	
	
	private String myReadLine() throws IOException, TrafficIOException{
		String content = null;
		content = myBufferedReader.readLine();
		
		if(content == null){
			return null;
		}else if(content.equals("")){
			content = myReadLine();
		}else if(content.startsWith("//")){
			content = myReadLine();
		}
		
		return content;
	}

	
	//will be called if a <Intersection> tag is found
	private void processInterection(String line) throws TrafficSyntaxException{
		String[] fields = line.split("	");
		
		if(fields.length != 2){
			throw new TrafficSyntaxException();
		}else{
			intersectionName = fields[0];
			intersectionDes = fields[1];
		}
		
		
	}	
	
	//will be called if a <TrafficStream> tag is found
	private void processTrafficStream(String line) throws TrafficSyntaxException{
		String[] fields = line.split("	");
		String streamName;
		String streamDes;
		
		if(fields.length != 2){
			throw new TrafficSyntaxException();
		}else{
			streamName = fields[0];
			streamDes = fields[1];
			TrafficStream newStream = new TrafficStream(streamName, streamDes);
			myTrafficStreams.add(newStream);
		}
	}
	
	
	//will be called if a <Intersection> tag is found
	//And return a phase
	private Phase processPhase(String line) throws TrafficSyntaxException{
		
		Phase newPhase;
		String name;
		String des;
		int duration;
		
		//check whether the information of phase is right
		String[] fields = line.split("	");
		if(fields.length != 4){
			throw new TrafficSyntaxException();
		}else{
			name = fields[0];
			des = fields[1];
			duration = Integer.parseInt(fields[3]);
		}
		
		//create a new phase and set its duration
		newPhase = new Phase(name, des);
		newPhase.setMinGreenInterval(duration);

		//add streams to the new phase created
		int numberOfStreams = myTrafficStreams.size();
		ArrayList<State> myStates = new ArrayList<State>();
		try{
			myStates = processStateString(fields[2], numberOfStreams);
		}catch(TrafficSyntaxException e){
			e.printStackTrace();
			System.out.println("the syntax of state text is wrong");
		}
		
		for(int i = 0; i < numberOfStreams; i++){
			newPhase.addStream(myTrafficStreams.get(i), myStates.get(i));
		}
		
		return newPhase;
	}
	
	
	//will be called if a <SignalFaces> tag is found
	private void processSignalFaces(String line) throws TrafficSyntaxException{
		String[] fields = line.split("	");
		TrafficDirection location = null;
		TrafficDirection orientation = null;
		int kind = 0;
		
		//check whether the information of signal face is right
		if(fields.length != 4){
			throw new TrafficSyntaxException();
		}else{
			try{
				location = convertToDirection(fields[0]);
				orientation = convertToDirection(fields[1]);
			}catch(TrafficSyntaxException e){
				e.printStackTrace();
				System.out.println("the direction does not exist");
			}
			
			//try to get the signal face's kind
			try{
				kind = convertToKind(fields[2]);
			}catch(TrafficSyntaxException e){
				e.printStackTrace();
				System.out.println("the kind does not exist");
			}

			SignalFace newSignalFace;
			newSignalFace = myIntersection.addSignalFace(location, orientation, kind);
			
			//add corresponding observers
			for(TrafficStream currentStream: myTrafficStreams){
				if(currentStream.getname().equals(fields[3])){
					currentStream.addObserver(newSignalFace);
				}
			}
			
			
		}
	}
	
	
	//the function for reading the information within <Intersection> and </Intersection> tags
	private void enterIntersectionBody(){
		String currentLineText = readNextUsefulLine();
		
		try{
			processInterection(currentLineText);
		}catch(TrafficSyntaxException e){
			e.printStackTrace();
			System.out.println("the syntax of content in <intersection> body is wrong");
		}
		
		myIntersection = new Intersection(intersectionName, intersectionDes);
	}
	
	
	//the function for keep reading the information within <TrafficStream> and </TrafficStream> tags
	private void enterTrafficStreamsBody(String token){
		String currentLineText = readNextUsefulLine();
		
		while(!currentLineText.equals(token)){
			try{
				processTrafficStream(currentLineText);
			}catch(TrafficSyntaxException e){
				e.printStackTrace();
				System.out.println("the syntax of content in <trafficStream> body is wrong");
			}
		
			currentLineText = readNextUsefulLine();
			
			if(currentLineText.equals(token)){
				trafficStreamComplete = true;
			}

		}
	}
	
	
	//the function for creating a new phase plan
	//also call the other function to create phases inside
	private PhasePlan createNewPhasePlan(String token) throws TrafficException{
		String currentLineText = readNextUsefulLine();
		PhasePlan newPhasePlan = null;
		
		//to decide the phase is a pre-timed or fully actuated
		if(currentLineText.equals(pretimedPhasePlan)){
			newPhasePlan = new PretimedPhasePlan();
		}else if(currentLineText.equals(fullyActuatedPhasePlan)){
			newPhasePlan = new FullyActuatedPhasePlan();
			fullyActuated = true;
			addDetectors();
		}else{
			throw new TrafficException();
		}

		currentLineText = readNextUsefulLine();

		while(!currentLineText.equals(token)){
			try{
				newPhasePlan.add(processPhase(currentLineText));
			}catch(TrafficSyntaxException e){
				e.printStackTrace();
				System.out.println("the syntax in <phase> body is wrong");
			}
			
			currentLineText = readNextUsefulLine();
			
			if(currentLineText.equals(token)){
				phaseComplete = true;
			}
		}

		return newPhasePlan;
	}
	
	
	//the function for keep reading the information within <SignalFaces> and </SignalFaces> tags
	private void enterSignalFacesBody(String token){
		String currentLineText = readNextUsefulLine();
		while(!currentLineText.equals(token)){

			try{
				processSignalFaces(currentLineText);
			}catch(TrafficSyntaxException e){
				e.printStackTrace();
				System.out.println("the synatx in <signalFaces> body is wrong");
			}

			currentLineText = readNextUsefulLine();
			
			if(currentLineText.equals(token)){
				signalFacesComplete = true;
			}
		}
	}
	
	
	//the function that read file from the beginning to the end
	private void processTranscript() throws TrafficException{
		String currentLineText = null;
		
		//read till the end
		while((currentLineText = readNextUsefulLine()) != null){
			
			//a <Intersection> tag found
			if(currentLineText.equals(Tag.INTERSECTION)){
				
				if(!intersectionComplete){
					enterIntersectionBody();
					currentLineText = readNextUsefulLine();
					if(!currentLineText.equals(Tag.END_INTERSECTION )){
						throw new TrafficException("a </intersection> tag is needed");
					}
					
				}else{
					throw new TrafficException("there must be only one <intersection> tag");
				}
			}
			
			//a <TrafficStream> tag found
			else if(currentLineText.equals(Tag.TRAFFIC_STREAMS)){
				if(!trafficStreamComplete){
					enterTrafficStreamsBody(Tag.END_TRAFFIC_STREAMS);
					if(!trafficStreamComplete){
						throw new TrafficException("a </trafficStreams> tag is needed");
					}
					
				}else{
					throw new TrafficException("there must be only one <trafficStreams> tag");
				}
			}
			
			//a <PhasePlan> tag found
			else if(currentLineText.equals(Tag.PHASEPLAN)){
				if(!phasePlanComplete){
					while((currentLineText = readNextUsefulLine()).equals(Tag.PHASES)){
						try{
							myIntersection.addPlan(createNewPhasePlan(Tag.END_PHASES));
						}catch(TrafficException e){
							e.printStackTrace();
							System.out.println("Please enter the model of itnersection phase plan");
						}
						if(!phaseComplete){
							throw new TrafficException("a </phase> tag is needed");
						}
					}

					if(!currentLineText.equals(Tag.END_PHASEPLAN)){
						throw new TrafficException("a </phasePlan> tag is needed");
					}
					
				}else{
					throw new TrafficException("there must be only one <phasePlan> tag");
				}
			}
			
			//a <SignalFaces> tag found
			else if(currentLineText.equals(Tag.SIGNAL_FACES)){
				if(!signalFacesComplete){
					enterSignalFacesBody(Tag.END_SIGNAL_FACES);
					if(!signalFacesComplete){
						throw new TrafficException("a </signalFaces> tag is needed");
					}
					
				}else{
					throw new TrafficException("there must be only one <signalFaces> tag");
				}
			}
			
			//the other invalid line
			else{
				throw new TrafficException("An illegal word appears in this intersection transcript");
			}
		}
	}
	
	
	private void addDetectors(){
		for(TrafficStream stream: myTrafficStreams){
			stream.addDetector(new RandomDetector());
		}
	}
	
	//the function used to convert the string read from file to the traffic direction
	private TrafficDirection convertToDirection(String name) throws TrafficSyntaxException{
		TrafficDirection direction = null;
		switch(name){
			case "E":
				direction = TrafficDirection.EAST;
				break;
			case "N":
				direction = TrafficDirection.NORTH;
				break;
			case "NE":
				direction = TrafficDirection.NORTHEAST;
				break;
			case "NW":
				direction = TrafficDirection.NORTHWEST;
				break;
			case "S":
				direction = TrafficDirection.SOUTH;
				break;
			case "SE":
				direction = TrafficDirection.SOUTHEAST;
				break;
			case "SW":
				direction = TrafficDirection.SOUTHWEST;
				break;
			case "W":
				direction = TrafficDirection.WEST;
				break;
		}
		
		if(direction.equals(null)){
			throw new TrafficSyntaxException();
		}
		return direction;
	}
	
	
	//the function used to convert the string read from file to the signal face kind
	private int convertToKind(String name) throws TrafficSyntaxException{
		int kind = 0;
		switch(name){
			case "LEFT_ARROW":
				kind = SignalFace.LEFT_ARROW;
				break;
			case "RIGHT_ARROW":
				kind = SignalFace.RIGHT_ARROW;
				break;
			case "STANDARD":
				kind = SignalFace.STANDARD;
				break;
		}
		
		if(kind == 0){
			throw new TrafficSyntaxException();
		}
		
		return kind;
	}
	
	
	//return an array list of states from the String read
	private ArrayList<State> processStateString(String text, int number) throws TrafficSyntaxException{
		ArrayList<State> states = new ArrayList<State>();
		
		if(text.length() != number){
			throw new TrafficSyntaxException();
		}else{
			for(char ch: text.toCharArray()){
				
				try{
					states.add(convertToState(ch));
				}catch(TrafficSyntaxException e){
					e.printStackTrace();
					System.out.println("The state does not exist");
				}
			}
		}
		
		return states;
	}
	
	
	//the function used to convert the string read from file to the state
	private State convertToState(char ch) throws TrafficSyntaxException{
		State theState = null;
		switch(ch){
			case 'R':
				theState = State.RED;
				break;
			case 'G':
				theState = State.GREEN;
				break;
			case 'Y':
				theState = State.YELLOW;
				break;
			case 'X':
				theState = State.OFF;
				break;
		}
		if(theState == null){
			throw new TrafficSyntaxException();
		}
		
		return theState;	
	}
}