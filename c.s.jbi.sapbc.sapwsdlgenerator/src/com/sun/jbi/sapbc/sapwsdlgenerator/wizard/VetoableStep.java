package com.sun.jbi.sapbc.sapwsdlgenerator.wizard;

/**
 * Represents a revokable stage.
 *
 * @author Noel Ang <nang@sun.com>
 */
public interface VetoableStep {
    
    /**
     * Transition from the present step to the next.
     *
     * @return <code>true</code> if the transition is successful,
     *         otherwise <code>false</code>.
     */
    boolean transpire();
}
