package com.traclabs.biosim.client.control;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.traclabs.biosim.client.util.BioHolder;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.actuator.framework.GenericActuator;
import com.traclabs.biosim.idl.simulation.air.OGS;
import com.traclabs.biosim.idl.simulation.water.WaterRS;

//javac -classpath
// .:$BIOSIM_HOME/lib/jacorb/jacorb.jar:$BIOSIM_HOME/generated/client/classes
// ActionMap.java

public class ActionMap {

    private Map myMap;

    public static GenericActuator[] actuators;

    public static String[] actuatorNames = { "OGSPotableWater",
            "waterRSdirty", "waterRSgrey" };

    private BioHolder myBioHolder;

    private boolean isbest;

    private Logger myLogger;

    public ActionMap() {
        myLogger = Logger.getLogger(this.getClass());
        Random r = new Random();
        int i;
        float maxrate;
        myMap = new TreeMap();
        boolean temp;
        myBioHolder = BioHolderInitializer.getBioHolder();

        WaterRS myWaterRS = (WaterRS) myBioHolder.theWaterRSModules.get(0);
        OGS myOGS = (OGS) myBioHolder.theOGSModules.get(0);
        actuators = new GenericActuator[3];
        actuators[0] = myBioHolder.getActuatorAttachedTo(
                myBioHolder.thePotableWaterInFlowRateActuators, myOGS);
        actuators[1] = myBioHolder.getActuatorAttachedTo(
                myBioHolder.theDirtyWaterInFlowRateActuators, myWaterRS);
        actuators[2] = myBioHolder.getActuatorAttachedTo(
                myBioHolder.theGreyWaterInFlowRateActuators, myWaterRS);

        for (i = 0; i < actuatorNames.length; i++) {
            maxrate = ((GenericActuator) actuators[i]).getMax();
            temp = r.nextBoolean();
            myLogger.debug(temp + "");
            if (temp)
                myMap.put(actuatorNames[i], new Float(10));
            else
                myMap.put(actuatorNames[i], new Float(0));
        }
        isbest = false;
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("Selection generated by constructor");
            myLogger.debug(myMap.toString());
        }

    }

    public ActionMap(int[] onoffs) {
        myLogger = Logger.getLogger(this.getClass());
        int i;
        float maxrate;

        myBioHolder = BioHolderInitializer.getBioHolder();

        WaterRS myWaterRS = (WaterRS) myBioHolder.theWaterRSModules.get(0);
        OGS myOGS = (OGS) myBioHolder.theOGSModules.get(0);
        actuators = new GenericActuator[4];
        actuators[0] = myBioHolder.getActuatorAttachedTo(
                myBioHolder.thePotableWaterInFlowRateActuators, myOGS);
        actuators[1] = myBioHolder.getActuatorAttachedTo(
                myBioHolder.theDirtyWaterInFlowRateActuators, myWaterRS);
        actuators[2] = myBioHolder.getActuatorAttachedTo(
                myBioHolder.theGreyWaterInFlowRateActuators, myWaterRS);

        myMap = new TreeMap();
        for (i = 0; i < actuatorNames.length; i++) {
            maxrate = ((GenericActuator) actuators[i]).getMax();
            if (onoffs[i] > 0.5)
                myMap.put(actuatorNames[i], new Float(10));
            else
                myMap.put(actuatorNames[i], new Float(0));
        }

    }

    public static int size() {
        return actuators.length;
    }

    public GenericActuator[] getActuators() {
        return actuators;
    }

    public static String[] getActuatorNames() {
        return actuatorNames;
    }

    public float getActuatorValue(String name) {
        return ((Float) myMap.get(name)).floatValue();
    }

    public void setBestFlag(boolean best) {
        isbest = best;
    }

    public void printMe() {
        myLogger.info(myMap);
    }

    public boolean isBest() {
        return isbest;
    }

    public Map getMap() {
        return myMap;
    }

}