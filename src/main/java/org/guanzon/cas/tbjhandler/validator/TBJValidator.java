package org.guanzon.cas.tbjhandler.validator;

import java.util.ArrayList;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.iface.GValidator;
import org.guanzon.cas.tbjhandler.Model.Model_TBJ_Detail;
import org.guanzon.cas.tbjhandler.Model.Model_TBJ_Master;
import org.guanzon.cas.tbjhandler.constant.TBJ_Constant;
import org.json.simple.JSONObject;

/**
 * TBJValidator ------------------------------------------------------------
 * Handles validation logic for Transaction Book Journal (TBJ) transactions.
 * Ensures that master and detail records meet required criteria before saving,
 * confirming, or processing other transaction states.
 * 
 * Supported transaction statuses include OPEN, CONFIRMED, PAID, CANCELLED, VOID,
 * POSTED, and RETURNED. Validation rules may differ per status.
 * 
 * @author Teejei De Celis (mdot223)
 * @since 2026-02-05
 * @startDate 2026-02-05
 * @endDate   2026-02-06
 * @version 1.0
 */
public class TBJValidator implements GValidator {

    private GRiderCAS poGrider;
    private String psTranStat;
    private JSONObject poJSON;

    private Model_TBJ_Master poMaster;
    private ArrayList<Model_TBJ_Detail> poDetail = new ArrayList<>();

    /**
     * Sets the application driver for validation.
     * 
     * @param applicationDriver instance of GRiderCAS
     */
    @Override
    public void setApplicationDriver(Object applicationDriver) {
        poGrider = (GRiderCAS) applicationDriver;
    }

    /**
     * Sets the transaction status that will determine which validation rules to apply.
     * 
     * @param transactionStatus transaction status (OPEN, CONFIRMED, PAID, etc.)
     */
    @Override
    public void setTransactionStatus(String transactionStatus) {
        psTranStat = transactionStatus;
    }

    /**
     * Sets the master record that will be validated.
     * 
     * @param value master record object of type Model_TBJ_Master
     */
    @Override
    public void setMaster(Object value) {
        poMaster = (Model_TBJ_Master) value;
    }

    /**
     * Sets the detail records that will be validated.
     * 
     * @param value ArrayList of detail objects of type Model_TBJ_Detail
     */
    @Override
    public void setDetail(ArrayList<Object> value) {
        poDetail.clear();
        for (Object obj : value) {
            poDetail.add((Model_TBJ_Detail) obj);
        }
    }

    /**
     * Sets additional data objects that may be required for validation.
     * Currently not supported.
     * 
     * @param value ArrayList of additional objects
     * @throws UnsupportedOperationException always, as this feature is not implemented
     */
    @Override
    public void setOthers(ArrayList<Object> value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Performs validation based on the current transaction status.
     * Delegates to specific private validation methods.
     * 
     * @return JSONObject containing validation result ("success" or "error") and message if applicable
     */
    @Override
    public JSONObject validate() {
        switch (psTranStat) {
            case TBJ_Constant.OPEN:
                return validateNew();
            case TBJ_Constant.CONFIRMED:
                return validateConfirmed();
            case TBJ_Constant.VOID:
                return validateVoid();
            default:
                poJSON = new JSONObject();
                poJSON.put("result", "success");
        }
        return poJSON;
    }

    /**
     * Validates a new (OPEN) transaction.
     * Ensures that required master fields are filled and at least one detail account exists.
     * 
     * @return JSONObject containing validation result and message if validation fails
     */
    private JSONObject validateNew() {
        poJSON = new JSONObject();

        if (poMaster.getSourceCode() == null || poMaster.getSourceCode().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Source Code is not set.");
            return poJSON;
        }

        if (poMaster.getIndustryID() == null || poMaster.getIndustryID().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Industry is not set! Please contact MIS Department!");
            return poJSON;
        }

        if (poMaster.getCategoryID() == null || poMaster.getCategoryID().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Category is not set.");
            return poJSON;
        }
        
       
        String remarks = poMaster.getRemarks();

        if (remarks == null || remarks.trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Remarks is not set.");
            return poJSON;
        }

        if (remarks.trim().length() < 10) {
            poJSON.put("result", "error");
            poJSON.put("message", "Please enter a remarks description with a minimum of 10 characters.");
            return poJSON;
        }

        
        int lnDetailCount = 0;

        for (int lnCtr = 0; lnCtr < poDetail.size(); lnCtr++) {
            Model_TBJ_Detail detail = poDetail.get(lnCtr);

            // Check if the entire row is empty
            boolean isRowEmpty
                    = (detail.getTableNm()== null || detail.getTableNm().trim().isEmpty())
                    && (detail.getDerivedField() == null || detail.getDerivedField().trim().isEmpty())
                    && (detail.getAccountType()== null || detail.getAccountType().trim().isEmpty())
                    && (detail.getAccountNo()== null || detail.getAccountNo().trim().isEmpty());

            if (!isRowEmpty) {
                lnDetailCount++;
            }
        }

        // Special check: only default row exists and it's empty
        if (poDetail.size() == 1 && lnDetailCount == 0) {
            poJSON.put("result", "error");
            poJSON.put("message", "Details must have at least one record.");
            return poJSON;
        }

        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Validates a transaction that is being confirmed.
     * Placeholder for rules specific to CONFIRMED status.
     * 
     * @return JSONObject containing validation result
     */
    private JSONObject validateConfirmed() {
        poJSON = new JSONObject();

        if (poMaster.getSourceCode() == null || poMaster.getSourceCode().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Source Code is not set.");
            return poJSON;
        }

        if (poMaster.getIndustryID() == null || poMaster.getIndustryID().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Industry is not set! Please contact MIS Department!");
            return poJSON;
        }

        if (poMaster.getCategoryID() == null || poMaster.getCategoryID().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Category is not set.");
            return poJSON;
        }
        
       
        String remarks = poMaster.getRemarks();

        if (remarks == null || remarks.trim().isEmpty()) {
            poJSON.put("result", "error");
            poJSON.put("message", "Remarks is not set.");
            return poJSON;
        }

        if (remarks.trim().length() < 10) {
            poJSON.put("result", "error");
            poJSON.put("message", "Please enter a remarks description with a minimum of 10 characters.");
            return poJSON;
        }

        
        int lnDetailCount = 0;

        for (int lnCtr = 0; lnCtr < poDetail.size(); lnCtr++) {
            Model_TBJ_Detail detail = poDetail.get(lnCtr);

            // Check if the entire row is empty
            boolean isRowEmpty
                    = (detail.isActive() == false);

            if (!isRowEmpty) {
                lnDetailCount++;
            }
        }

        // Special check: only default row exists and it's empty
        if (poDetail.size() == 1 && lnDetailCount == 0) {
            poJSON.put("result", "error");
            poJSON.put("message", "Details must have at least one record active.");
            return poJSON;
        }

        poJSON.put("result", "success");
        return poJSON;
    }
    
    /**
     * Validates a transaction that is being void.
     * Placeholder for rules specific to void status.
     * 
     * @return JSONObject containing validation result
     */
    private JSONObject validateVoid() {
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }
}
