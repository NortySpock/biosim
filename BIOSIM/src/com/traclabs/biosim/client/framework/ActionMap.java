package biosim.client.framework;


import java.util.*;
import java.io.*;
import biosim.idl.framework.BioDriver;
import biosim.idl.framework.BioDriverHelper;
import biosim.idl.framework.BioModule;
import biosim.idl.sensor.air.*;
import biosim.idl.sensor.crew.*;
import biosim.idl.sensor.environment.*;
import biosim.idl.sensor.food.*;
import biosim.idl.sensor.framework.*;
import biosim.idl.sensor.power.*;
import biosim.idl.sensor.water.*;
import biosim.idl.actuator.air.*;
import biosim.idl.actuator.crew.*;
import biosim.idl.actuator.environment.*;
import biosim.idl.actuator.food.*;
import biosim.idl.actuator.framework.*;
import biosim.idl.actuator.power.*;
import biosim.idl.actuator.water.*;
import biosim.idl.simulation.crew.*;
import biosim.idl.simulation.water.*;
import biosim.idl.simulation.air.*;
import biosim.idl.simulation.food.*;
import biosim.idl.simulation.power.*;
import biosim.idl.simulation.environment.*;
import biosim.idl.simulation.framework.*;
import biosim.client.util.*;



//javac -classpath .:$BIOSIM_HOME/lib/jacorb/jacorb.jar:$BIOSIM_HOME/generated/client/classes ActionMap.java

public class ActionMap{

	private Map myMap;
	public static GenericActuator[] actuators; 
	public static String[] actuatorNames = {"airRSCO2", "airRSpotable", "waterRSdirty", "waterRSgrey"};
	BioHolder myBioHolder; 
	boolean isbest; 
	
	ActionMap() {
		Random r = new Random();
		int i;
		float maxrate;
		myMap = new TreeMap(); 
		boolean temp; 
		myBioHolder = BioHolderInitializer.getBioHolder();

		WaterRS myWaterRS = (WaterRS)myBioHolder.theWaterRSModules.get(0);
		AirRS myAirRS = (AirRS)myBioHolder.theAirRSModules.get(0);
		actuators = new GenericActuator[4]; 
		actuators[0] = myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2InFlowRateActuators, myAirRS); 
		actuators[1] = myBioHolder.getActuatorAttachedTo(myBioHolder.thePotableWaterInFlowRateActuators, myAirRS); 
		actuators[2] = myBioHolder.getActuatorAttachedTo(myBioHolder.theDirtyWaterInFlowRateActuators, myWaterRS); 
		actuators[3] = myBioHolder.getActuatorAttachedTo(myBioHolder.theGreyWaterInFlowRateActuators, myWaterRS); 

		for (i = 0;i<actuatorNames.length;i++) {
			maxrate = ((GenericActuator)actuators[i]).getMax();
			temp = r.nextBoolean();
//			System.out.println(temp); 
			if (temp) myMap.put(actuatorNames[i], new Float(20));
			else myMap.put(actuatorNames[i], new Float(0));
		}
		isbest = false; 
//		System.out.println("Selection generated by constructor"); 
//		printMe(); 
		
	}

		
	ActionMap(int[] onoffs) {
		int i;
		float maxrate;

		myBioHolder = BioHolderInitializer.getBioHolder();
		
		WaterRS myWaterRS = (WaterRS)myBioHolder.theWaterRSModules.get(0);
		AirRS myAirRS = (AirRS)myBioHolder.theAirRSModules.get(0);
		actuators = new GenericActuator[4]; 
		actuators[0] = myBioHolder.getActuatorAttachedTo(myBioHolder.theCO2InFlowRateActuators, myAirRS); 
		actuators[1] = myBioHolder.getActuatorAttachedTo(myBioHolder.thePotableWaterInFlowRateActuators, myAirRS); 
		actuators[2] = myBioHolder.getActuatorAttachedTo(myBioHolder.theDirtyWaterInFlowRateActuators, myWaterRS); 
		actuators[3] = myBioHolder.getActuatorAttachedTo(myBioHolder.theGreyWaterInFlowRateActuators, myWaterRS); 

		myMap = new TreeMap();
		for (i = 0;i<actuatorNames.length;i++) {
			maxrate = ((GenericActuator)actuators[i]).getMax();
			if (onoffs[i] > 0.5) myMap.put(actuatorNames[i], new Float(20));
			else myMap.put(actuatorNames[i], new Float(0));
		}

		
	}
	
	public static int size() { 
		return actuators.length;	
	} 
	
	public GenericActuator[] getActuators() { 
		return actuators; 
	} 
	
	public static String[] getActuatorNames() { return actuatorNames;} 
	
	public float getActuatorValue(String name) { 
		return ((Float)myMap.get(name)).floatValue(); 	
	}
	public void printMe() { System.out.println(myMap);}
	public void setBestFlag(boolean best) { isbest = best;} 
	public boolean isBest() {return isbest;} 
	
	public Map getMap() {return myMap;} 

}