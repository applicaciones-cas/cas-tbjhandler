/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.tbjhandler.previous;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.guanzon.appdriver.base.GRiderCAS;
import org.json.simple.JSONObject;
import org.guanzon.cas.tbjhandler.TBJEntry;
import org.guanzon.cas.tbjhandler.TBJUtil;

/**
 *
 * @author Administrator
 */
public class TBJPurchaseReturn implements TBJHandler{
    private GRiderCAS poInstance;
    private String psSourceCD = "PRTN";
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
        
        //
        String lsPayGLAcct;
        
        //Extract Industry code
        String lsIndstCde = (String)loMaster.get("sIndstCde");
        
        TBJEntry entry;

        //record purchase
        double lnPoReturn = (double)loMaster.get("nTranTotl");
        lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "PURCHASE-RETURN-ALLOWANCE");
        entry = new TBJEntry(lsPayGLAcct,  0,lnPoReturn, "Purchase Return-Allowance") ;
        paEntries.add(entry);
        
        //Check for purchase discount lost
        double lnDisc = ((double)loMaster.get("nDiscount") * (double)loMaster.get("nTranTotl")) + (double)loMaster.get("nAddDiscx");;
        if(lnDisc > 0 ){
            lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "PURCHASE-DISCOUNT-LOST");
            entry = new TBJEntry(lsPayGLAcct, lnDisc,0, "Discount") ;
            paEntries.add(entry);
        }
        
        //record purchase return - pending
        double lnPoPending = lnPoReturn - lnDisc;
        lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "PURCHASE-RETURN-PENDING");
        entry = new TBJEntry(lsPayGLAcct, lnPoPending, 0, "Purchase Return-Pending") ;
        paEntries.add(entry);

        loRes = new JSONObject();
        loRes.put("result", "success");
        
        return loRes;
    }

    
    @Override
    public List<TBJEntry> getJournalEntries() {
        return paEntries;
    }
}
