package org.guanzon.cas.tbjhandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.SQLUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author kalyptus
 * Purpose:
 *  This object is responsible for determining the appropriate 
 *  account entries for transactions according to the assignments provided by the accounting office. 
 * History: 
 *  2025.04.07 10:00am - kalyptus
 *      Started creating this object
 */
public class TBJTransaction {
    private GRiderCAS poInstance;
    private final String psSourceCD;
    private final String psIndstCde;
    private final String psCategrCd;
    private JSONObject poData = null;
    private List<TBJEntry> paEntries;
    private final ScriptEngine paEngine = new ScriptEngineManager().getEngineByName("JavaScript");
    
    public TBJTransaction(String fsSourceCD, String fsIndstCde, String fsCategrCd){
        psSourceCD = fsSourceCD;
        psIndstCde = fsIndstCde;
        psCategrCd = fsCategrCd;
    }
    
    public void setGRiderCAS(GRiderCAS grider) {
        poInstance = grider;
    }

    public void setData(JSONObject json) {
        poData = json;
        paEntries = new ArrayList<>();
    }

    public JSONObject processRequest() throws SQLException, ScriptException {
        JSONObject loRes = new JSONObject();

        //Reset paEntries
        paEntries = new ArrayList<>();

        //Extract tables related to the transaction
        String lsSQL = "SELECT sTableNme, cTableTyp" + 
                      " FROM xxxTransactionSourceTable" +
                      " WHERE sSourceCD = " + SQLUtil.toSQL(psSourceCD);
        System.out.println(lsSQL);
        ResultSet loRS = poInstance.executeQuery(lsSQL);
        while(loRS.next()){
            boolean lbHasdata = false;
            JSONObject loOne;
            JSONArray loMany;

            if(poData.containsKey(loRS.getString("sTableNme"))){			
                lbHasdata = true;		
            }			

            //load entries for the table
            lsSQL = "SELECT a.cIsReqrdx" +
                         ", a.cAcctNoSr" +
                         ", a.sAcctNoxx" + 
                         ", a.sFieldSrc" + 
                         ", a.sPreCondx" +
                         ", a.cAcctType" +
                         ", a.sDervdFld" +
                   " FROM TBJ_Detail a " + 
                        " LEFT JOIN TBJ_Master b ON a.sGLTranNo = b.sGLTranNo" + 
                   " WHERE b.sSourceCD = " + SQLUtil.toSQL(psSourceCD) + 
                     " AND b.sIndstCde = " + SQLUtil.toSQL(psIndstCde) + 
                     " AND b.sCategrCd = " + SQLUtil.toSQL(psCategrCd) + 
                     " AND a.sTableNme = " + SQLUtil.toSQL(loRS.getString("sTableNme")) + 
                     " AND a.cActivexx = '1'";
            ResultSet loRSDetail = poInstance.executeQuery(lsSQL);
            System.out.println(lsSQL);
            while(loRSDetail.next()){
                //extract if data is required value
                boolean lbIsRequired = loRSDetail.getString("cIsReqrdx").equalsIgnoreCase("1");
                //check element has data
                if(!lbHasdata){
                    if(lbIsRequired){
                        if(loRes.isEmpty()){
                            loRes.put("result", "failed");
                            ArrayList<String> itemList = new ArrayList<>();      
                            itemList.add("Data for required account " + loRSDetail.getString("sAcctNoxx") + " not found.");
                            loRes.put("message", itemList);
                        }
                        else{
                            ArrayList<String> itemList = (ArrayList<String>) loRes.get("message");
                            itemList.add("Data for required account " + loRSDetail.getString("sAcctNoxx") + " not found.");
                            loRes.put("message", itemList);
                        }
                    }
                    continue;
                }

                //has required data
                //work with one row only/json object 
                if(loRS.getString("cTableTyp").equalsIgnoreCase("0")){
                    //extract the element from poData
                    loOne = getJSONObjectFromData(loRS.getString("sTableNme"));

                    //extract row to HashMap
                    Map<String, Object> row = TBJUtil.Row2HashMap(loRSDetail );
                    
                    //evaluate Entry
                    evaluateEntry(loOne, row, loRes, lbIsRequired);
                }
                //work with multiple row/json array
                else{
                    loMany = getJSONArrayFromData(loRS.getString("sTableNme"));
                    
                    //process JSONArray
                    for (Object loJSON : loMany) {
                        //process one row at a time from loMany
                        loOne = (JSONObject) loJSON;

                        //extract row to HashMap
                        Map<String, Object> row = TBJUtil.Row2HashMap(loRSDetail );

                        //evaluate Entry
                        evaluateEntry(loOne, row, loRes, lbIsRequired);
                    }
                }
            }
        }
        
        //If no entry then set value mapping of result = success
        if(loRes.isEmpty()){
            loRes.put("result", "success");
        }
        
        return loRes;
    }
    
   public List<TBJEntry> getJournalEntries() {
        return paEntries;
    }
   private JSONObject getJSONObjectFromData(String tableName) {
       Object obj = poData.get(tableName);
       return obj instanceof JSONObject ? (JSONObject) obj : null;
   }

   private JSONArray getJSONArrayFromData(String tableName) {
       Object obj = poData.get(tableName);
       return obj instanceof JSONArray ? (JSONArray) obj : null;
   }   
   
   private void evaluateEntry(JSONObject loOne, Map<String, Object> row, JSONObject foRes, boolean fbIsRequired) throws ScriptException{
        //set default value of field condition to true
        boolean lbCond = true;

        String lsFormula = (String)row.get("sPreCondx"); 
        ArrayList<String> variables;
        //extract and process field condition if there is any
        if(!lsFormula.isEmpty()){
            System.out.println("Evaluating formula:");
            variables = TBJUtil.extractFormula(lsFormula);

            for (String variable : variables) {
                lsFormula = lsFormula.replace(variable, loOne.get(variable).toString());
            }
            lbCond = (boolean)paEngine.eval(lsFormula);  // Evaluate expression
        }    
        
        //if upon evaluation of the field condition and the result is negative then go to next record to evaluate
        if(!lbCond){
            return;
        }

        //compute the value
        lsFormula = (String)row.get("sDervdFld"); 
        variables = TBJUtil.extractFormula(lsFormula);
        for (String variable : variables) {
            lsFormula = lsFormula.replace(variable, loOne.get(variable).toString());
        }
        double ldValue = Double.parseDouble(paEngine.eval(lsFormula).toString());  // Evaluate expression

        if(ldValue == 0){
            if(fbIsRequired){
                ArrayList<String> itemList = (ArrayList<String>) foRes.getOrDefault("message", new ArrayList<String>());                
                itemList.add("Data for required account " + (String) row.get("sAcctNoxx") + " is empty.");
                foRes.put("message", itemList);
            }
            return;
        }

        String lsAcctNoxx = row.get("sAcctNoxx").toString(); 
        if(lsAcctNoxx.isEmpty()){
            String lsField = row.get("sFieldSrc").toString();
            lsAcctNoxx = loOne.get(lsField).toString();
        }

        //check if lsAcctNoxx is empty
        if(lsAcctNoxx.isEmpty()){
            ArrayList<String> itemList = (ArrayList<String>) foRes.getOrDefault("message", new ArrayList<String>());                
            itemList.add("A required account has no account code. Please check value in the tables Account_TR_Detail/Particular/" +  (String) row.get("sTableNme"));
            foRes.put("message", itemList);
            return;
        }

        //determine if entry is a debit or credit entry
        double lnCredit;
        double lnDebit;
        String lcAcctType = (String) row.get("cAcctType");
        if ("C".equalsIgnoreCase(lcAcctType)) {
            lnCredit = ldValue > 0 ? ldValue : 0;
            lnDebit = ldValue < 0 ? -ldValue : 0;
        } else {
            lnDebit = ldValue > 0 ? ldValue : 0;
            lnCredit = ldValue < 0 ? -ldValue : 0;
        }        
        
        paEntries.add(new TBJEntry(lsAcctNoxx, lnDebit, lnCredit, ""));
   }
}

