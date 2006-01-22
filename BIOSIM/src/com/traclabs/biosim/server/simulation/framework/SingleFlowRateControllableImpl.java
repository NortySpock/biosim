package com.traclabs.biosim.server.simulation.framework;

import org.apache.log4j.Logger;

import com.traclabs.biosim.idl.simulation.framework.SingleFlowRateControllablePOA;

/**
 * @author Scott Bell
 */

public abstract class SingleFlowRateControllableImpl extends
        SingleFlowRateControllablePOA {
    private float[] myMaxFlowRates;

    private float[] myActualFlowRates;

    private float[] myDesiredFlowRates;
    
    protected Logger myLogger;

    public SingleFlowRateControllableImpl() {
    	myLogger = Logger.getLogger(this.getClass());
        myMaxFlowRates = new float[0];
        myActualFlowRates = new float[0];
        myDesiredFlowRates = new float[0];
    }

    public void setMaxFlowRate(float value, int index) {
        myMaxFlowRates[index] = value;
    }

    public float getMaxFlowRate(int index) {
        return myMaxFlowRates[index];
    }

    public void setDesiredFlowRate(float value, int index) {
        myDesiredFlowRates[index] = value;
    }

    public float getDesiredFlowRate(int index) {
        return myDesiredFlowRates[index];
    }

    public float getActualFlowRate(int index) {
        return myActualFlowRates[index];
    }

    public float[] getMaxFlowRates() {
        return myMaxFlowRates;
    }

    public float[] getDesiredFlowRates() {
        return myDesiredFlowRates;
    }

    public float[] getActualFlowRates() {
        return myActualFlowRates;
    }

    public void setMaxFlowRates(float[] pMaxFlowRates) {
        myMaxFlowRates = pMaxFlowRates;
    }

    public void setDesiredFlowRates(float[] pDesiredFlowRates) {
        myDesiredFlowRates = pDesiredFlowRates;
    }

    public void setActualFlowRates(float[] pActualFlowRates) {
        myActualFlowRates = pActualFlowRates;
    }
    
    public int getFlowRateCardinality(){
    	return myActualFlowRates.length;
    }
    
    public float getTotalMaxFlowRate(){
    	float totalMaxFlowRate = 0;
    	for (float maxFlowRate : myMaxFlowRates)
    		totalMaxFlowRate += maxFlowRate;
    	return totalMaxFlowRate;
    }
    
    public float getTotalDesiredFlowRate(){
    	float totalDesiredFlowRate = 0;
    	for (float maxFlowRate : myDesiredFlowRates)
    		totalDesiredFlowRate += maxFlowRate;
    	return totalDesiredFlowRate;
    }
    
    public float getTotalActualFlowRate(){
    	float totalActualFlowRate = 0;
    	for (float actualFlowRate : myActualFlowRates)
    		totalActualFlowRate += actualFlowRate;
    	return totalActualFlowRate;
    }
    
    public float getAveragePercentageFull(){
    	float totalDesiredFlowRate = getTotalDesiredFlowRate();
    	if (totalDesiredFlowRate <= 0)
    		return 1f;
    	return getTotalActualFlowRate() / totalDesiredFlowRate;
    }
    
    public float getPercentageFull(int index){
    	if (myDesiredFlowRates[index] <= 0)
    		return 1f;
    	return myActualFlowRates[index] / myDesiredFlowRates[index];
    }
}