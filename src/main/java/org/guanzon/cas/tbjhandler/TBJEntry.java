/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.tbjhandler;

/**
 *
 * @author Administrator
 */
public class TBJEntry {
    private final String account;
    private final double debit;
    private final double credit;
    private final String description;

    public TBJEntry(String account, double debit, double credit, String description) {
        this.account = account;
        this.debit = debit;
        this.credit = credit;
        this.description = description;
    }
    
    public String getAccount(){
        return this.account;
    }
    public double getDebit(){
        return this.debit;
    }
    public double getCredit(){
        return this.credit;
    }
    public String getDescription(){
        return this.description;
    }
}
