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
public class TBJJobOrder implements TBJHandler{
    private GRiderCAS poInstance;
    private String psSourceCD = "JO";
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
        double lnPurchase = (double)loMaster.get("nTranTotl");
        lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "PURCHASE");
        entry = new TBJEntry(lsPayGLAcct, lnPurchase, 0, "Total Purchase") ;
        paEntries.add(entry);
        
        //Check if with advance payment
        double lnAdvance = (double)loMaster.get("nAdvAmtxx");
        if(lnAdvance > 0){
            lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "PREPAID-EXPENSE");
            entry = new TBJEntry(lsPayGLAcct, 0, lnAdvance, "Payment") ;
            paEntries.add(entry);
        }

        //Check for purchase discount
        double lnDisc = ((double)loMaster.get("nDiscount") * (double)loMaster.get("nTranTotl")) + (double)loMaster.get("nAddDiscx");;
        if(lnDisc > 0 ){
            lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "PURCHASE-DISCOUNT");
            entry = new TBJEntry(lsPayGLAcct, 0, lnDisc, "Discount") ;
            paEntries.add(entry);
        }
        
        //Check for accounts payable
        double lnAP = lnPurchase - (lnAdvance + lnDisc);
        if(lnAP > 0 ){
            lsPayGLAcct = TBJUtil.getAccount(poInstance, lsIndstCde, "ACCOUNTS-PAYABLE");
            entry = new TBJEntry(lsPayGLAcct, 0, lnAP, "Accounts Payable") ;
            paEntries.add(entry);
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
