package biosim.server.sensor.environment;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.environment.*;
import biosim.idl.simulation.environment.*;

public class OtherAirLevelSensorImpl extends EnvironmentSensorImpl implements OtherAirLevelSensorOperations{
	public OtherAirLevelSensorImpl(int pID){
		super(pID);
	}

	protected void gatherData(){
		float preFilteredValue = getInput().getOtherLevel();
		myValue = randomFilter(preFilteredValue);
	}
	
	protected void notifyListeners(){
	}
	
	/**
	* Returns the name of this module (OtherAirLevelSensor)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "OtherAirLevelSensor"+getID();
	}
}