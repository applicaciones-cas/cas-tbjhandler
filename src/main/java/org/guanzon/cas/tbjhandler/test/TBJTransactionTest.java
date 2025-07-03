/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.tbjhandler.test;

import java.sql.SQLException;
import java.util.List;
import javax.script.ScriptException;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.guanzon.cas.tbjhandler.TBJEntry;
import org.guanzon.cas.tbjhandler.TBJTransaction;

/**
 *
 * @author Administrator
 */
public class TBJTransactionTest {
    private GRiderCAS poInstance;
    public static void main(String args[]){
        try {
            System.setProperty("sys.default.path.config", "D:/GGC_Maven_Systems");
            
            //Test the different object initialization
            //GRiderCAS instance = new GRiderCAS();
            //instance.loadEnv("gRider");
            
            GRiderCAS instancex = new GRiderCAS("gRider");
            if (!instancex.logUser("gRider", "M001000001")){ 
                System.err.println(instancex.getMessage());
                System.exit(1);
            }
            
            JSONObject jsonmaster = new JSONObject();
            jsonmaster.put("nWTaxTotl", 0.00);
            jsonmaster.put("nDiscTotl", 500.00);
            jsonmaster.put("nNetTotal", 4500.00);
            jsonmaster.put("cPaymType", "0");
            
            JSONArray jsondetails = new JSONArray();
            
            JSONObject jsondetail = new JSONObject();
            jsondetail.put("sAcctCode", "2101010");
            jsondetail.put("nAmtAppld", 4000.00);
            
            jsondetails.add(jsondetail);
                  
//            jsondetail = new JSONObject();
//            jsondetail.put("sAcctCode", "5201000");
//            jsondetail.put("nAmtAppld", 1000);
//            jsondetails.add(jsondetail);

            jsondetail = new JSONObject();
            jsondetail.put("Disbursement_Master", jsonmaster);
            jsondetail.put("Disbursement_Detail", jsondetails);
            
            TBJTransaction tbj = new TBJTransaction("DISB", "01", "");
            tbj.setGRiderCAS(instancex);
            tbj.setData(jsondetail);
            jsonmaster = tbj.processRequest();
            
            if(jsonmaster.get("result").toString().equalsIgnoreCase("success")){
                List<TBJEntry> xlist = tbj.getJournalEntries();
                for (TBJEntry xlist1 : xlist) {
                    System.out.println("Account:" + xlist1.getAccount() );
                    System.out.println("Debit:" + xlist1.getDebit());
                    System.out.println("Credit:" + xlist1.getCredit());
                }
            }
            else{
                System.out.println(jsonmaster.toJSONString());
            }
        } catch (SQLException | GuanzonException | ScriptException ex) {
            ex.printStackTrace();
        }
    }
}
