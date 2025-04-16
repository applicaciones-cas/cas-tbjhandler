/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.guanzon.cas.tbjhandler.previous;

/**
 *
 * @author Administrator
 */
public class TBJHanderFactory {
    private static final String PURCHASE = "PO";
    private static final String PURCHASE_RETURN = "PRTN";
    private static final String SALES = "SLSI";
    private static final String SALES_RETURN = "SRTN";
    private static final String DISBURSEMENT = "DISB";
    private static final String SERVICE_INVOICE = "SVCI";
    private static final String JOB_ORDER = "JO";
    
    public static TBJHandler make(String trans){
        if(trans.equalsIgnoreCase(PURCHASE))
            return new TBJPurchase();
        else if(trans.equalsIgnoreCase(PURCHASE_RETURN)) 
            return new TBJPurchase();
        else if(trans.equalsIgnoreCase(DISBURSEMENT)) 
            return new TBJDisbursement();
        else if(trans.equalsIgnoreCase(SALES)) 
            return new TBJSales();
        else if(trans.equalsIgnoreCase(SALES_RETURN)) 
            return new TBJSales();
        else if(trans.equalsIgnoreCase(SERVICE_INVOICE)) 
            return new TBJReceipt();
        else if(trans.equalsIgnoreCase(JOB_ORDER)) 
            return new TBJReceipt();
        else
            return null;
    }
}
