package biosim.client.simulation.water.gui;

import javax.swing.*;
import java.awt.*;
import biosim.client.simulation.framework.gui.*;

/**
 * This is the JPanel that displays a chart about the Water
 *
 * @author    Scott Bell
 */
public class WaterChartPanel extends UpdatablePanel
{
	private WaterStorePanel myWaterStorePanel;

	public WaterChartPanel() {
		setLayout(new BorderLayout());
		myWaterStorePanel = new WaterStorePanel();
		add(myWaterStorePanel, BorderLayout.CENTER);
	}
	
	public void visibilityChange(boolean nowVisible){
		myWaterStorePanel.visibilityChange(nowVisible);
	}
	
	public void refresh(){
		myWaterStorePanel.refresh();
	}
}
