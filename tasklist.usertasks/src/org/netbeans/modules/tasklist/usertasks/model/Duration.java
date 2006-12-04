package org.netbeans.modules.tasklist.usertasks.model;

/**
 * A duration class.
 */
public class Duration {    
    public int weeks, days, hours, minutes;
    
    /**
     * Constructor.
     * 
     * @param minutes duration in minutes
     * @param hoursPerDay working hours per day
     * @param daysPerWeek working days per week
     */
    public Duration(int weeks, int days, int hours, int minutes) {
        this.weeks = weeks;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }
    
    /**
     * Constructor. 
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
     * Constructor. 
     * 
     * @param minutes duration in minutes
     * @param minutesPerDay working minutes per day
     * @param daysPerWeek working days per week
     * @param ignored will be ignored
     */
    public Duration(int minutes, int minutesPerDay, int daysPerWeek,
            boolean ignored) {
        this.minutes = minutes % minutesPerDay;
        this.hours = this.minutes / 60;
        this.minutes = this.minutes % 60;
        
        this.days = minutes / minutesPerDay;
        this.weeks = this.days / daysPerWeek;
        this.days = this.days % daysPerWeek;
    }

    /**
     * Converts the value to minutes.
     * 
     * @param hoursPerDay hours per day
     * @param daysPerWeek days per week 
     */
    public int toMinutes(int hoursPerDay, int daysPerWeek) {
        return ((weeks * daysPerWeek + days) * hoursPerDay + hours) * 60 +
                minutes;
    }

    /**
     * Converts the value to minutes.
     * 
     * @param minutesPerDay minutes per day
     * @param daysPerWeek days per week 
     * @param ignore will be ignored
     */
    public int toMinutes(int minutesPerDay, int daysPerWeek, boolean ignore) {
        return (weeks * daysPerWeek + days) * minutesPerDay + hours * 60 +
                minutes;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Duration))
            return false;
        Duration d = (Duration) obj;
        return d.weeks == weeks && d.days == days && d.hours == hours &&
                d.minutes == minutes;
    }
}
