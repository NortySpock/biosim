package biosim.server.sensor.water;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.water.*;
import biosim.idl.framework.*;

public class PotableWaterStoreLevelSensorImpl extends PotableWaterStoreSensorImpl implements PotableWaterStoreLevelSensorOperations{
	public PotableWaterStoreLevelSensorImpl(int pID, String pName){
		super(pID, pName);
	}

	protected void gatherData(){
		float preFilteredValue = getInput().getLevel();
		myValue = randomFilter(preFilteredValue);
	}
	
	protected void notifyListeners(){
	}
}
