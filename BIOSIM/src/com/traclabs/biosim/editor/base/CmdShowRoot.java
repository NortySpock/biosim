/*
 * Copyright � 2004 S&K Technologies, Inc, 56 Old Hwy 93, St Ignatius, MT 98865
 * All rights reserved.
 * U.S. Government Rights - Commercial software.  Government users are subject
 * to S&K Technologies, Inc, standard license agreement and applicable 
 * provisions of the FAR and its supplements.
 * Use is subject to license terms.
 */
package com.traclabs.biosim.editor.base;

import javax.swing.ImageIcon;

import org.tigris.gef.base.Cmd;
import org.tigris.gef.base.Globals;

/**
 * Displays the root graph within the current editor.
 */
public class CmdShowRoot extends Cmd {
    public CmdShowRoot() {
        super("VesprBase", "ShowRoot");
    }

    public CmdShowRoot(ImageIcon icon) {
        super(null, "VesprBase", "ShowRoot", icon);
    }

    public void doIt() {
        VesprEditor editor = (VesprEditor) Globals.curEditor();
        editor.showRoot();
    }

    public void undoIt() {
    }
}