/**
 * BreezySLAM: Simple, efficient SLAM in Java
 * <p>
 * Scan.java - Java code for Scan class
 * <p>
 * Copyright (C) 2014 Simon D. Levy
 * <p>
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this code.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.wlu.cs.levy.breezyslam.components;


import ca.uoit.crobot.util.LibraryUtils;

/**
 * A class for Lidar scans.
 */
public class Scan {

    static {
        LibraryUtils.loadLibrary("libjnibreezyslam_components");
    }

    private native void init(
            int span,
            int scan_size,
            double scan_rate_hz,
            double detection_angle_degrees,
            double distance_no_detection_mm,
            int detection_margin,
            double offset_mm);

    private long native_ptr;

    public native String toString();

    /**
     * Returns a string representation of this Scan object.
     */
    public native void update(
            int[] lidar_mm,
            double hole_width_mm,
            double poseChange_dxy_mm,
            double poseChange_dtheta_degrees);


    /**
     * Builds a Scan object.
     *
     * @param laser laser parameters
     * @param span  supports spanning laser scan to cover the space better.
     */
    public Scan(Laser laser, int span) {
        this.init(span,
                laser.scan_size,
                laser.scan_rate_hz,
                laser.detection_angle_degrees,
                laser.distance_no_detection_mm,
                laser.detection_margin,
                laser.offset_mm);
    }

    /**
     * Builds a Scan object.
     *
     * @param laser laser parameters
     */
    public Scan(Laser laser) {
        this(laser, 1);
    }

    /**
     * Updates this Scan object with new values from a Lidar scan.
     *
     * @param scanvals_mm            scanned Lidar distance values in millimeters
     * @param hole_width_millimeters hole width in millimeters
     * @param poseChange             forward velocity and angular velocity of robot at scan time
     */
    public void update(int[] scanvals_mm, double hole_width_millimeters, PoseChange poseChange) {
        this.update(scanvals_mm, hole_width_millimeters, poseChange.dxy_mm, poseChange.dtheta_degrees);
    }
}

