package biosim.server.sensor.food;

import biosim.server.sensor.framework.*;
import biosim.idl.sensor.food.*;
import biosim.idl.framework.*;

public class FoodInFlowRateSensorImpl extends GenericSensorImpl implements FoodInFlowRateSensorOperations{
	private FoodConsumer myConsumer;
	private int myIndex;
	
	public FoodInFlowRateSensorImpl(int pID){
		super(pID);
	}

	protected void gatherData(){
		float preFilteredValue = getInput().getFoodInputActualFlowRate(myIndex);
		myValue = randomFilter(preFilteredValue);
	}
	
	protected void notifyListeners(){
		//does nothing right now
	}

	public void setInput(FoodConsumer pConsumer, int pIndex){
		myConsumer = pConsumer;
		myIndex = pIndex;
	}
	
	public float getMax(){
		return myConsumer.getFoodInputMaxFlowRate(myIndex);
	}
	
	public FoodConsumer getInput(){
		return myConsumer;
	}
	
	protected BioModule getInputModule(){
		return (BioModule)(myConsumer);
	}
	
	public int getIndex(){
		return myIndex;
	}
	
	/**
	* Returns the name of this module (FoodInFlowRateSensor)
	* @return the name of the module
	*/
	public String getModuleName(){
		return "FoodInFlowRateSensor"+getID();
	}
}
