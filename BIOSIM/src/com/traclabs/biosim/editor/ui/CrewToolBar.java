/*
 * Created on Jan 26, 2005
 *
 */
package com.traclabs.biosim.editor.ui;

import javax.swing.JButton;

import com.traclabs.biosim.editor.base.CmdCreateModuleNode;
import com.traclabs.biosim.editor.graph.crew.CrewGroupNode;

/**
 * @author scott
 * 
 */
public class CrewToolBar extends EditorToolBar {
    private JButton myCrewButton;

    public CrewToolBar() {
        super("Crew");
        add(new CmdCreateModuleNode(CrewGroupNode.class, "EditorBase", "CrewGroup"));
    }
}