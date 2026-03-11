package org.guanzon.cas.tbjhandler.constant;

/**
 * TBJ_Constant ----------------------------------------------------------
 * Defines constant values for Transaction Book Journal (TBJ) status codes.
 * 
 * VOID = "3"       : Transaction is VOID
 * OPEN = "0"       : Transaction has OPEN
 * CONFIRMED = "1"  : Transaction has been CONFIRMED
 */
public class TBJ_Constant {
    /** Transaction is OPEN */
    public static final String OPEN = "0";
    /** Transaction has been CONFIRMED */
    public static final String CONFIRMED = "1";
    /** Transaction is VOID */
    public static final String VOID = "3";
    
}
