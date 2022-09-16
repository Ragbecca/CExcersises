package com.ragbecca.utils;

import javax.swing.*;
import java.awt.*;

public class Utils {

    /**
     * Sets constraints (Viewable place on frame) for the component
     *
     * @param springLayout The global layout used, one initialization
     * @param component    The object being placed to a certain place
     * @param padNorth     Padding on the north side, relative to the north side
     * @param padSouth     Padding on the south side, relative to the north side (height)
     * @param padEast      Padding on the east side, relative to the west side (width)
     * @param padWest      Padding on the west side, relative to the west side
     * @param container    Container, so the frame that is being used
     */
    public void putConstraint(SpringLayout springLayout, Component component, int padNorth, int padSouth, int padEast, int padWest, Container container) {
        if (padNorth > 0) {
            springLayout.putConstraint(SpringLayout.NORTH, component, padNorth, SpringLayout.NORTH, container);
        }
        if (padSouth > 0) {
            springLayout.putConstraint(SpringLayout.SOUTH, component, padSouth, SpringLayout.NORTH, container);
        }
        if (padEast > 0) {
            springLayout.putConstraint(SpringLayout.EAST, component, padEast, SpringLayout.WEST, container);
        }
        if (padWest > 0) {
            springLayout.putConstraint(SpringLayout.WEST, component, padWest, SpringLayout.WEST, container);
        }
    }
}
