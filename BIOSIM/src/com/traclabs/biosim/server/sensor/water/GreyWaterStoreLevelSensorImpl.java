package biosim.server.sensor.water;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.water.*;
import biosim.idl.simulation.water.*;

public class GreyWaterStoreLevelSensorImpl extends GreyWaterStoreSensorImpl implements GreyWaterStoreLevelSensorOperations{
	public GreyWaterStoreLevelSensorImpl(int pID){
		super(pID);
	}

	protected void gatherData(){
		float preFilteredValue = getInput().getLevel();
		myValue = randomFilter(preFilteredValue);
	}
	
	protected void notifyListeners(){
	}
}
