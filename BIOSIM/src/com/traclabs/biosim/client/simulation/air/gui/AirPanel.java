package biosim.client.air.gui;

import biosim.client.framework.*;
import biosim.client.framework.gui.*;
import javax.swing.*;
import java.awt.*;
/** 
 * This is the JPanel that displays information about the Air
 *
 * @author    Scott Bell
 */

public class AirPanel extends BioTabbedPanel
{
	protected void createPanels(){
		myTextPanel = new AirTextPanel();
		myChartPanel = new BioTabPanel();
		mySchematicPanel = new BioTabPanel();
	}
}
