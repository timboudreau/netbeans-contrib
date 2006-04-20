package org.netbeans.modules.tasklist.usertasks.model;

import java.text.MessageFormat;
import org.openide.util.NbBundle;

/**
 * A duration class.
 */
public class Duration {    
    public int weeks, days, hours, minutes;
    
    /**
     * Splits a duration value. 
     * 
     * @param minutes duration in minutes
     * @param hoursPerDay working hours per day
     * @param daysPerWeek working days per week
     */
    public Duration(int minutes, int hoursPerDay, int daysPerWeek) {
        this.minutes = minutes % 60;
        minutes /= 60;
        this.hours = minutes % hoursPerDay;
        minutes /= hoursPerDay;
        this.days = minutes % daysPerWeek;
        minutes /= daysPerWeek;
        this.weeks = minutes;
    }

}
