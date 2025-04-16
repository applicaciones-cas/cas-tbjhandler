/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.tbjhandler.previous;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.SQLUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.guanzon.cas.tbjhandler.TBJEntry;
import org.guanzon.cas.tbjhandler.TBJUtil;

/**
 *
 * @author Administrator
 */
public class TBJSales implements TBJHandler{
    private GRiderCAS poInstance;
    private String psSourceCD = "SLSI";
    private JSONObject poData = null;
    private List<TBJEntry> paEntries;
    
    @Override
    public void setGRiderCAS(GRiderCAS grider) {
        poInstance = grider;
    }

    @Override
    public void setData(JSONObject json) {
        poData = json;
        paEntries = new ArrayList<>();
    }

    @Override
    public JSONObject processRequest() throws SQLException {
        JSONObject loRes;
    
        //Extract the value inside the master table
        JSONObject loMaster = (JSONObject)poData.get("master");
        
        String lsPayGLAcct;
        
        //Extract payment type
        String lsPaymType = (String)loMaster.get("cPaymType");

        //Extract Industry code
        String lsIndstCde = (String)loMaster.get("sIndstCde");
        
        TBJEntry entry;
        
        //is check payment
        if(lsPaymType.equalsIgnoreCase("cash")){
            lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "CASH-ON-HAND");
        }
        //is check payment
        else if(lsPaymType.equalsIgnoreCase("check")){
            lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "CASH-ON-BANK");
        }
        //is digital payment
        else{
            lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "E-WALLET-FUNDS");
        }

        //Add the payment entry
        entry = new TBJEntry(lsPayGLAcct, 0, (double)loMaster.get("nAmountxx"), "Payment") ;
        paEntries.add(entry);

        //Add withholding tax
        if((double)loMaster.get("nWTaxTotl") > 0){
            lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "CREDITABLE-WTAX");
            entry = new TBJEntry(lsPayGLAcct, 0, (double)loMaster.get("nWTaxTotl"), "WTax") ;
            paEntries.add(entry);
        }
        
        //extract details
        JSONArray loJSArray = (JSONArray)poData.get("detail");
        for (Object loDetailx : loJSArray) {
            JSONObject loJson = (JSONObject) loDetailx;
            double lnAmount = (double)loJson.get("nAmtAppld");

            //Extract information of particular id from Particular table
            String lsSQL = "SELECT sDescript, IFNULL(sAcctCode, '') sAcctCode" +
                          " FROM Particular" + 
                          " WHERE sPrtclrID = " + SQLUtil.toSQL((String)loJson.get("sPrtclrID"));
            ResultSet loRS = poInstance.executeQuery(lsSQL);
            //If not existing then
            if(!loRS.next()){
                loRes = new JSONObject();
                loRes.put("result", "failed");
                loRes.put("message", "Particular ID not existing:" + (String)loJson.get("sPrtclrID") + "!"  );
                return loRes;
            }
            
            //if particular has no account code then
            if(loRS.getString("sAcctCode").trim().isEmpty()){
                loRes = new JSONObject();
                loRes.put("result", "failed");
                loRes.put("message", "Particular ID has no Account:" + (String)loJson.get("sPrtclrID") + "!"  );
                return loRes;
            }
            
            if(lnAmount > 0){
                entry = new TBJEntry(lsPayGLAcct, lnAmount, 0, loRS.getString("sDescript")) ;
                paEntries.add(entry);
            }
            else{
                entry = new TBJEntry(lsPayGLAcct, 0, lnAmount, "Payment for ?") ;
                paEntries.add(entry);
            }
        }
        
        loRes = new JSONObject();
        loRes.put("result", "success");
        
        return loRes;
    }
    
    @Override
    public List<TBJEntry> getJournalEntries() {
        return paEntries;
    }
}
