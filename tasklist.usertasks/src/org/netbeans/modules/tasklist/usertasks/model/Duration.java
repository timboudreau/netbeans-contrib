package org.netbeans.modules.tasklist.usertasks.model;

import java.text.MessageFormat;
import org.openide.util.NbBundle;

/**
 * A duration class.
 */
public class Duration {
    private static final MessageFormat FORMAT = 
        new MessageFormat(NbBundle.getMessage(Duration.class, 
            "DurationFormat")); // NOI18N
    
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
    
    /**
     * Returns the string representation of this duration.
     *
     * @return internationalized text
     */
    public String format() {
        return FORMAT.format(new Object[] {
            new Integer(weeks),
            new Integer(days), new Integer(hours), new Integer(minutes)
        }).trim();
    }
}
