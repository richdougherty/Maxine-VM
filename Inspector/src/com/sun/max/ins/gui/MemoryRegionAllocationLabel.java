/*
 * Copyright (c) 2007 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to technology embodied in the product
 * that is described in this document. In particular, and without limitation, these intellectual property
 * rights may include one or more of the U.S. patents listed at http://www.sun.com/patents and one or
 * more additional patents or pending patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software. Government users are subject to the Sun
 * Microsystems, Inc. standard license agreement and applicable provisions of the FAR and its
 * supplements.
 *
 * Use is subject to license terms. Sun, Sun Microsystems, the Sun logo, Java and Solaris are trademarks or
 * registered trademarks of Sun Microsystems, Inc. in the U.S. and other countries. All SPARC trademarks
 * are used under license and are trademarks or registered trademarks of SPARC International, Inc. in the
 * U.S. and other countries.
 *
 * UNIX is a registered trademark in the U.S. and other countries, exclusively licensed through X/Open
 * Company, Ltd.
 */
package com.sun.max.ins.gui;

import java.awt.*;
import java.awt.event.*;
import java.lang.management.*;

import com.sun.max.ins.*;
import com.sun.max.tele.*;

/**
 * A label that displays the allocation percentage of a known memory region and acts as a drag source.
 *
 * @author Michael Van De Vanter
 */
public final class MemoryRegionAllocationLabel extends AbstractMemoryRegionLabel implements Prober {

    private enum DisplayMode {
        PERCENT,
        ALLOCATED;
    }

    private DisplayMode displayMode = DisplayMode.PERCENT;
    private final Component parent;

    /**
     * Returns a that displays the name of a known memory region
     * and acts as a drag source.
     *
     * @param inspection
     * @param memoryRegion a memory region in the VM
     * @param parent a component that should be repainted when the display state is toggled;
     */
    public MemoryRegionAllocationLabel(Inspection inspection, MaxMemoryRegion memoryRegion, Component parent) {
        super(inspection, memoryRegion);
        this.parent = parent;
        addMouseListener(new InspectorMouseClickAdapter(inspection()) {
            @Override
            public void procedure(MouseEvent mouseEvent) {
                switch (Inspection.mouseButtonWithModifiers(mouseEvent)) {
                    case MouseEvent.BUTTON2: {
                        final InspectorAction cycleAction = getCycleDisplayTextAction();
                        if (cycleAction != null) {
                            cycleAction.perform();
                        }
                        break;
                    }
                    case MouseEvent.BUTTON3: {
                        final InspectorPopupMenu menu = new InspectorPopupMenu();
                        final InspectorAction cycleAction = getCycleDisplayTextAction();
                        if (cycleAction != null) {
                            menu.add(cycleAction);
                        }
                        menu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        });
        redisplay();
        refresh(true);
    }

    private InspectorAction getCycleDisplayTextAction() {
        DisplayMode alternateDisplayMode = displayMode;
        switch(displayMode) {
            case ALLOCATED:
                alternateDisplayMode = DisplayMode.PERCENT;
                break;
            case PERCENT:
                alternateDisplayMode = DisplayMode.ALLOCATED;
                break;
        }
        if (alternateDisplayMode != displayMode) {
            final DisplayMode newDisplayMode = alternateDisplayMode;
            return new InspectorAction(inspection(), "Cycle display (Middle-Button)") {

                @Override
                public void procedure() {
                    //Trace.line(TRACE_VALUE, "WVL: " + displayMode.toString() + "->" + newValueKind);
                    MemoryRegionAllocationLabel label = MemoryRegionAllocationLabel.this;
                    label.displayMode = newDisplayMode;
                    label.redisplay();
                    label.refresh(true);
                    if (label.parent != null) {
                        label.parent.repaint();
                    }
                }
            };
        }
        return null;
    }

    public void redisplay() {
        switch(displayMode) {
            case ALLOCATED:
                setFont(style().hexDataFont());
                break;
            case PERCENT:
                setFont(style().primitiveDataFont());
                break;

        }

    }

    public void refresh(boolean force) {
        final MemoryUsage usage = memoryRegion.getUsage();
        final long size = usage.getCommitted();
        if (size == 0) {
            setText(inspection().nameDisplay().unavailableDataShortText());
            setToolTipText(inspection().nameDisplay().unavailableDataLongText());
        } else {
            final long used = usage.getUsed();
            switch(displayMode) {
                case ALLOCATED:
                    setText("0x" + Long.toHexString(used));
                    setToolTipText(memoryRegion.regionName() + " allocated=" + Long.toString(100 * used / size) + "% (" + used + "/" + size + ")");
                    break;
                case PERCENT:
                    setText(Long.toString(100 * used / size) + "%");
                    setToolTipText(memoryRegion.regionName() + " allocated= 0x" + Long.toHexString(used) + "/0x" + Long.toHexString(size) + " bytes (" + used + "/" + size + ")");
                    break;
            }
        }

    }
}

