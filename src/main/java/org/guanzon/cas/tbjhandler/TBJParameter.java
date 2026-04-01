/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.tbjhandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.agent.services.Transaction;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.appdriver.iface.GValidator;
import org.guanzon.cas.parameter.Category;
import org.guanzon.cas.parameter.Industry;
import org.guanzon.cas.parameter.TransactionSource;
import org.guanzon.cas.parameter.TransactionSourceTable;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.guanzon.cas.tbjhandler.Model.Model_TBJ_Detail;
import org.guanzon.cas.tbjhandler.Model.Model_TBJ_Master;
import org.guanzon.cas.tbjhandler.Services.TBJControllers;
import org.guanzon.cas.tbjhandler.Services.TBJModels;
import org.guanzon.cas.tbjhandler.constant.TBJ_Constant;
import org.guanzon.cas.tbjhandler.validator.TBJValidator;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.cashflow.AccountChart;
import ph.com.guanzongroup.cas.cashflow.services.CashflowControllers;

/**
 * TBJParameter ------------------------------------------------------------
 * Handles Transaction Book Journal (TBJ) parameter transactions. Responsible
 * for initializing transaction models, managing detail records, validating
 * entries, saving transactions, and performing lookup/search operations for
 * related parameters, and confirming transactions.
 *
 * @author Teejei De Celis (mdot223)
 * @since 2026-02-05
 * @startDate 2026-02-05
 * @endDate   2026-02-06
 * @version 1.0
 */
public class TBJParameter extends Transaction {

    // ==================== TRANSACTION INITIALIZATION ====================
    /**
     * Initializes the TBJ transaction. Sets source code, prepares master and
     * detail models, and creates an empty detail list.
     *
     * @return JSONObject result of initialization process
     */
    public JSONObject InitTransaction() {
        SOURCE_CODE = "TBJ";
        pbVerifyEntryNo = false;

        poMaster = new TBJModels(poGRider).TBJMaster();
        poDetail = new TBJModels(poGRider).TBJDetail();
        paDetail = new ArrayList<>();

        return initialize();
    }

    // ==================== DETAIL OPERATIONS ====================
    /**
     * Adds a new detail row to the transaction. Prevents adding a new row if
     * the last row has an empty table name.
     *
     * @return JSONObject result indicating success or error
     * @throws CloneNotSupportedException if cloning of model fails
     */
    public JSONObject AddDetail() throws CloneNotSupportedException {
        if (Detail(getDetailCount() - 1).getDerivedField().isEmpty()) {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "Last row has empty item.");
            return poJSON;
        }
        return addDetail();
    }

    /**
     * Returns the TBJ Master model.
     *
     * @return Model_TBJ_Master master record
     */
    @Override
    public Model_TBJ_Master Master() {
        return (Model_TBJ_Master) poMaster;
    }

    /**
     * Returns the TBJ Detail model at the specified row index.
     *
     * @param row index of the detail record
     * @return Model_TBJ_Detail detail record
     */
    @Override
    public Model_TBJ_Detail Detail(int row) {
        return (Model_TBJ_Detail) paDetail.get(row);
    }

    // ==================== TRANSACTION METHODS ====================
    /**
     * Creates a new TBJ transaction.
     *
     * @return JSONObject result of new transaction process
     * @throws CloneNotSupportedException if model cloning fails
     */
    public JSONObject NewTransaction() throws CloneNotSupportedException {
        return newTransaction();
    }

    /**
     * Saves the current TBJ transaction to the database.
     *
     * @return JSONObject result of save operation
     * @throws SQLException if database error occurs
     * @throws CloneNotSupportedException if model cloning fails
     * @throws GuanzonException if business rule validation fails
     */
    public JSONObject SaveTransaction() throws SQLException, CloneNotSupportedException, GuanzonException {
        return saveTransaction();
    }

    /**
     * Opens an existing TBJ transaction using the transaction number.
     *
     * @param transactionNo the transaction number to open
     * @return JSONObject containing the result of the open operation -
     * "success" if the transaction is loaded successfully - "error" if loading
     * fails
     * @throws SQLException if a database error occurs while retrieving the
     * transaction
     * @throws CloneNotSupportedException if cloning the transaction model fails
     * @throws GuanzonException if the transaction cannot be opened due to
     * business rules
     */
    public JSONObject OpenTransaction(String transactionNo)
            throws CloneNotSupportedException, SQLException, GuanzonException {
        return openTransaction(transactionNo);
    }

    /**
     * Puts the current transaction into update mode.
     *
     * @return JSONObject containing the result of the update operation -
     * "success" if the transaction is ready for editing - "error" if the
     * transaction cannot be updated
     */
    public JSONObject UpdateTransaction() {
        return updateTransaction();
    }

    /**
     * Confirms the currently loaded transaction after performing validation and
     * user authorization checks.
     * <p>
     * This method ensures that:
     * <ul>
     * <li>A transaction is currently loaded and in READY state.</li>
     * <li>The transaction has not already been confirmed.</li>
     * <li>All business rule validations pass.</li>
     * <li>The user has sufficient authorization level to approve the
     * confirmation.</li>
     * </ul>
     * Once validated, the transaction status is updated to CONFIRMED and the
     * database transaction is committed. If any step fails, the transaction is
     * rolled back and an error result is returned.
     *
     * @param remarks remarks or notes related to the confirmation of the
     * transaction
     * @return JSONObject containing the result of the operation:
     * <ul>
     * <li><b>result</b> = "success" if the transaction was successfully
     * confirmed</li>
     * <li><b>result</b> = "error" if validation or authorization fails</li>
     * <li><b>message</b> = descriptive message indicating the outcome</li>
     * </ul>
     *
     * @throws ParseException if an error occurs while parsing date or status
     * values
     * @throws SQLException if a database access error occurs during the
     * transaction process
     * @throws GuanzonException if a business rule or system-related error
     * occurs
     * @throws CloneNotSupportedException if cloning of transaction data fails
     */
    public JSONObject ConfirmTransaction(String remarks)
            throws ParseException, SQLException, GuanzonException, CloneNotSupportedException {

        poJSON = new JSONObject();

        String lsStatus = TBJ_Constant.CONFIRMED;
        boolean lbConfirm = true;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transaction was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction was already confirmed.");
            return poJSON;
        }

        // Validator
        poJSON = isEntryOkay(TBJ_Constant.CONFIRMED);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        // User authorization check
        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            poJSON = ShowDialogFX.getUserApproval(poGRider);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER) {
                poJSON.put("result", "error");
                poJSON.put("message", "User is not an authorized approving officer.");
                return poJSON;
            }
        }
        
        poJSON = checkDuplicate(Master().getSourceCode(),
                    Master().getIndustryID(),
                    Master().getCategoryID(),"1");

            if (!"success".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

        // Begin database transaction
        poGRider.beginTrans("UPDATE STATUS", "ConfirmTransaction", SOURCE_CODE, Master().getTransactionNo());
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sGLTranNo"), remarks, lsStatus, !lbConfirm, true);
        if (!"success".equals((String) poJSON.get("result"))) {
            poGRider.rollbackTrans();
            return poJSON;
        }

        poGRider.commitTrans();

        poJSON = new JSONObject();
        poJSON.put("result", "success");

        if (lbConfirm) {
            poJSON.put("message", "Transaction confirmed successfully.");
        } else {
            poJSON.put("message", "Transaction confirmation request submitted successfully.");
        }

        return poJSON;
    }
    /**
     * Voids the currently loaded transaction after performing validation and
     * user authorization checks.
     * <p>
     * This method verifies that:
     * <ul>
     * <li>A transaction is currently loaded and ready for processing.</li>
     * <li>The transaction has not already been voided.</li>
     * <li>All business rule validations pass.</li>
     * <li>The user has sufficient authorization level to approve the void
     * action.</li>
     * </ul>
     * If all conditions are met, the transaction status is updated to VOID and
     * the database transaction is committed. Otherwise, the operation is rolled
     * back and an error result is returned.
     *
     * @param remarks remarks or reason for voiding the transaction
     * @return JSONObject containing the result of the operation:
     * <ul>
     * <li><b>result</b> = "success" if the transaction was successfully
     * voided</li>
     * <li><b>result</b> = "error" if validation or authorization fails</li>
     * <li><b>message</b> = descriptive message of the operation result</li>
     * </ul>
     *
     * @throws ParseException if an error occurs while parsing date or status
     * values
     * @throws SQLException if a database access error occurs during the
     * transaction
     * @throws GuanzonException if a business rule or system error occurs
     * @throws CloneNotSupportedException if cloning of transaction data fails
     */
    public JSONObject VoidTransaction(String remarks)
            throws ParseException, SQLException, GuanzonException, CloneNotSupportedException {

        poJSON = new JSONObject();

        String lsStatus = TBJ_Constant.VOID;
        boolean lbConfirm = true;

        if (getEditMode() != EditMode.READY) {
            poJSON.put("result", "error");
            poJSON.put("message", "No transaction was loaded.");
            return poJSON;
        }

        if (lsStatus.equals((String) poMaster.getValue("cTranStat"))) {
            poJSON.put("result", "error");
            poJSON.put("message", "Transaction was already void.");
            return poJSON;
        }

        // Validator
        poJSON = isEntryOkay(TBJ_Constant.VOID);
        if (!"success".equals((String) poJSON.get("result"))) {
            return poJSON;
        }

        // User authorization check
        if (poGRider.getUserLevel() <= UserRight.ENCODER) {
            poJSON = ShowDialogFX.getUserApproval(poGRider);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER) {
                poJSON.put("result", "error");
                poJSON.put("message", "User is not an authorized approving officer.");
                return poJSON;
            }
        }
          poJSON = checkDuplicate(Master().getSourceCode(),
                    Master().getIndustryID(),
                    Master().getCategoryID(),"3");

            if (!"success".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

        // Begin database transaction
        poGRider.beginTrans("UPDATE STATUS", "Void Transaction", SOURCE_CODE, Master().getTransactionNo());

        
        poJSON = statusChange(poMaster.getTable(), (String) poMaster.getValue("sGLTranNo"), remarks, lsStatus, !lbConfirm, true);
        if (!"success".equals((String) poJSON.get("result"))) {
            poGRider.rollbackTrans();
            return poJSON;
        }

        if (!"success".equals((String) poJSON.get("result"))) {
            poGRider.rollbackTrans();
            return poJSON;
        }

        poGRider.commitTrans();

        poJSON = new JSONObject();
        poJSON.put("result", "success");

        if (lbConfirm) {
            poJSON.put("message", "Transaction void successfully.");
        } else {
            poJSON.put("message", "Transaction void request submitted successfully.");
        }

        return poJSON;
    }

    // ==================== VALIDATION / SAVE HOOKS ====================
    /**
     * Performs system validation and cleanup before saving the transaction.
     * Removes detail records with empty table names and assigns transaction
     * number and entry number to valid detail rows.
     *
     * @return JSONObject result indicating success or failure
     * @throws CloneNotSupportedException if model cloning fails
     * @throws SQLException if database error occurs
     * @throws GuanzonException if validation fails
     */
    @Override
    public JSONObject willSave() throws CloneNotSupportedException, SQLException, GuanzonException {
        poJSON = new JSONObject();
        
        for (int lnCtr = 0; lnCtr <= getDetailCount() - 1; lnCtr++) {

            // Skip fully empty rows (optional)
            boolean isRowEmpty
                    = (Detail(lnCtr).getTableNm() == null || Detail(lnCtr).getTableNm().trim().isEmpty())
                    && (Detail(lnCtr).getDerivedField() == null || Detail(lnCtr).getDerivedField().trim().isEmpty())
                    && (Detail(lnCtr).getAccountType() == null || Detail(lnCtr).getAccountType().trim().isEmpty())
                    && (Detail(lnCtr).getAccountNo() == null || Detail(lnCtr).getAccountNo().trim().isEmpty());

            if (isRowEmpty) {
                continue; // ignore empty row
            }

            // Now check required fields
            if (Detail(lnCtr).getTableNm() == null || Detail(lnCtr).getTableNm().isEmpty()) {
                poJSON.put("message", "Table Name is required at row " + (lnCtr + 1) + ".");
                poJSON.put("result", "error");
                return poJSON;
            }

            if (Detail(lnCtr).getDerivedField() == null || Detail(lnCtr).getDerivedField().isEmpty()) {
                poJSON.put("message", "Derived Field is required at row " + (lnCtr + 1) + ".");
                poJSON.put("result", "error");
                return poJSON;
            }

            if (Detail(lnCtr).getAccountType() == null || Detail(lnCtr).getAccountType().isEmpty()) {
                poJSON.put("message", "Account Type is required at row " + (lnCtr + 1) + ".");
                poJSON.put("result", "error");
                return poJSON;
            }

            if (Detail(lnCtr).getAccountNo() == null || Detail(lnCtr).getAccountNo().isEmpty()) {
                poJSON.put("message", "Account No is required at row " + (lnCtr + 1) + ".");
                poJSON.put("result", "error");
                return poJSON;
            }
            
        }

        Iterator<Model> detail = Detail().iterator();
        while (detail.hasNext()) {
            Model item = detail.next();
            String tableNme = String.valueOf(item.getValue("sTableNme"));

            if (tableNme == null || tableNme.isEmpty()) {
                detail.remove();
            }
        }

        for (int lnCtr = 0; lnCtr <= getDetailCount() - 1; lnCtr++) {
            Detail(lnCtr).setTransactionNo(Master().getTransactionNo());
            Detail(lnCtr).setEntryNo(lnCtr + 1);
        }
        
        if (getEditMode() == EditMode.ADDNEW) {
            poJSON = checkDuplicate(Master().getSourceCode(),
                    Master().getIndustryID(),
                    Master().getCategoryID(),"1");

            if (!"success".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
        }
        if(Master().getTransactionStatus().equals(TBJ_Constant.CONFIRMED)){
            if (poGRider.getUserLevel() <= UserRight.ENCODER) {
                poJSON = ShowDialogFX.getUserApproval(poGRider);
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }
                if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "User is not an authorized approving officer.");
                    return poJSON;
                }
            }
        }
        
        for (int lnCtr = 0; lnCtr <= getDetailCount() - 1; lnCtr++) {
           System.out.println("DETAILS : \n" + Detail(lnCtr).AccountChart().getDescription() +
                   "\n " + Detail(lnCtr).getTableNm()+
                   "\n " + Detail(lnCtr).getDerivedField()  +
                   "\n " + Detail(lnCtr).isActive()  );
        }
        
       
        poJSON.put("result", "success");
        return poJSON;
        }
    /**
     * Checks if a record with the specified combination of sSourceCD, sIndstCde, 
     * and sCategrCd already exists in the TBJ_Master table.
     * <p>
     * This method executes a SELECT query on the TBJ_Master table to determine 
     * whether a row with the same source code, industry code, and category code 
     * exists. If a duplicate is found, it returns a JSONObject with an "error" 
     * result and includes the transaction number (sGLTranNo) of the existing record.
     * If no duplicate is found, it returns a "success" result.
     * </p>
     *
     * <p>
     * ADDED this validation as requested to display if already exist before saving 
     * by the tester (sent via Viber by maam she)
     * </p>
     * <ul>
     *     <li>ADDED BY: TEEJEI</li>
     *     <li>DATE ADDED: March 06, 2026</li>
     *     <li>TIME: 11:33:56 AM</li>
     * </ul>
     *
     * <pre>
     * Example usage:
     * JSONObject response = checkDuplicate("SRC001", "IND123", "CAT001");
     * if (response.getString("result").equals("error")) {
     *     System.out.println(response.getString("message"));
     * } else {
     *     System.out.println("No duplicates found.");
     * }
     * </pre>
     *
     * @param sSourceCD  the source code to check for duplicates (cannot be null)
     * @param sIndstCde  the industry code to check for duplicates (cannot be null)
     * @param sCategrCd  the category code to check for duplicates (cannot be null)
     * @return JSONObject a JSON object containing:
     *         <ul>
     *             <li>"result" - "error" if a duplicate exists, "success" if no duplicate</li>
     *             <li>"message" - user-friendly message including transaction number if duplicate</li>
     *         </ul>
     * @throws SQLException if a database access error occurs while executing the query
     */
     public JSONObject checkDuplicate(String sSourceCD, String sIndstCde, String sCategrCd,String fsStatus) throws SQLException {
        JSONObject poJSON = new JSONObject();
         
        String lsSQL = " SELECT "
                + " sGLTranNo," 
                + " sSourceCD, " 
                + " sIndstCde, " 
                + " sCategrCd, " 
                + " cTranStat " 
                + " FROM TBJ_Master " 
                + " WHERE sSourceCD = " + SQLUtil.toSQL(sSourceCD)
                + " AND sIndstCde = " +  SQLUtil.toSQL(sIndstCde)
                + " AND sCategrCd = " + SQLUtil.toSQL(sCategrCd)                
                + " AND cTranStat = " + SQLUtil.toSQL(fsStatus)  ;
        
         System.out.println("SQL EXECUTED: " + lsSQL);
         ResultSet rs = poGRider.executeQuery(lsSQL);
         if (rs.next()) {
             
             if (fsStatus.equals("1") || fsStatus.equals("3")) {
                 String tranNo = rs.getString("sGLTranNo");
                 poJSON.put("result", "error");
                 poJSON.put("message", "A duplicate record was found with the same Source Code, Industry, Category, and Status.\n"
                         + "Transaction No: " + tranNo + ".\n"
                         + "Please review your entries or use a different combination.");
             }
         } else {
             poJSON.put("result", "success");
         }

         rs.close();
         return poJSON;
    }
    
    /**
     * Validates the current transaction entry and returns the result as a
     * JSONObject.
     * <p>
     * This method checks whether all required master and detail fields are
     * properly filled and returns a JSON object indicating success or failure.
     * It does not persist the data to the database; it only performs
     * validation.
     *
     * @return a JSONObject containing the validation result: - "result":
     * "success" if validation passes, "error" if validation fails - "message":
     * a descriptive message when validation fails
     */
    @Override
    public JSONObject save() {
        String Status = Master().getTransactionStatus();
        return isEntryOkay(Status);
    }

    /**
     * Validates the transaction based on its current status using the
     * TBJValidator.
     *
     * @param status transaction status
     * @return JSONObject validation result
     */
    @Override
    protected JSONObject isEntryOkay(String status) {
        GValidator loValidator = new TBJValidator();
        loValidator.setApplicationDriver(poGRider);
        loValidator.setTransactionStatus(status);
        loValidator.setMaster(Master());
        poJSON = loValidator.validate();
        return poJSON;
    }

    /**
     * Hook method for saving additional related records. Currently not
     * implemented.
     *
     * @return JSONObject result of saveOthers process
     */
    @Override
    public JSONObject saveOthers() {
        poJSON = new JSONObject();
        return poJSON;
    }

    // ==================== SEARCH / LOOKUP ====================
    /**
     * Searches and selects a Transaction Source record.
     *
     * @param value search value
     * @param byCode true if searching by code, false if by description
     * @return JSONObject result of search operation
     * @throws SQLException if database error occurs
     * @throws GuanzonException if search fails
     */
    public JSONObject SearchSourceCode(String value, boolean byCode)
            throws ExceptionInInitializerError, SQLException, GuanzonException {

        TransactionSource object = new ParamControllers(poGRider, logwrapr).TransactionSource();
        object.setRecordStatus("1");

        poJSON = object.searchRecord(value, byCode);

        if ("success".equals((String) poJSON.get("result"))) {
            Master().setSourceCode(object.getModel().getSourceCode());
        }
        return poJSON;
    }
    
    /**
     * Performs a search for an Industry (Category) record and updates the
     * master record if successful.
     *
     * <p>
     * This method uses {@code ParamControllers} to access Industry records and
     * filters by active status ("1"). If a matching record is found, the
     * industry's ID is set in the master record.
     *
     * @param value the value to search for (code or description)
     * @param byCode {@code true} to search by code, {@code false} to search by
     * description
     * @return a {@link JSONObject} containing the result of the search
     * operation. The "result" key indicates "success" or "error".
     * @throws SQLException if a database access error occurs
     * @throws GuanzonException if the search operation fails or encounters
     * validation issues
     * @throws ExceptionInInitializerError if initialization of the controller
     * fails
     */
    public JSONObject SearchIndustry(String value, boolean byCode)
            throws ExceptionInInitializerError, SQLException, GuanzonException {

        Industry object = new ParamControllers(poGRider, logwrapr).Industry();
        object.setRecordStatus("1");

        poJSON = object.searchRecord(value, byCode);

        if ("success".equals((String) poJSON.get("result"))) {
            Master().setIndustryID(object.getModel().getIndustryId());
        }
        return poJSON;
    }

    /**
     * Searches for a Category record under a specific Industry and updates the
     * master record if found.
     *
     * <p>
     * This method uses {@code ParamControllers} to access Category records
     * filtered by active status ("1") and the current Industry ID from the
     * master record. If a matching record is found, the Category ID is set in
     * the master record.
     *
     * @param value the search value (code or description)
     * @param byCode {@code true} to search by code, {@code false} to search by
     * description
     * @return a {@link JSONObject} containing the search result. The "result"
     * key indicates {@code "success"} or {@code "error"}.
     * @throws SQLException if a database access error occurs
     * @throws GuanzonException if the search fails or encounters validation
     * issues
     * @throws ExceptionInInitializerError if initialization of the controller
     * fails
     */
    public JSONObject SearchCategory(String value, boolean byCode)
            throws ExceptionInInitializerError, SQLException, GuanzonException {

        Category object = new ParamControllers(poGRider, logwrapr).Category();
        object.setRecordStatus("1");

        poJSON = object.searchRecord(value, byCode,Master().getIndustryID());

        if ("success".equals((String) poJSON.get("result"))) {
            Master().setCategoryID(object.getModel().getCategoryId());
        }
        return poJSON;
    }

    /**
     * Searches and selects an Account Chart record for a detail row.
     *
     * @param value search value
     * @param byCode true if searching by code, false if by description
     * @param detailRow index of the detail row to update
     * @return JSONObject result of search operation
     * @throws SQLException if database error occurs
     * @throws GuanzonException if search fails
     */
    public JSONObject SearchAccountChart(String value, boolean byCode, int detailRow)
            throws ExceptionInInitializerError, SQLException, GuanzonException {

        AccountChart object = new CashflowControllers(poGRider, logwrapr).AccountChart();
        object.setRecordStatus("1");

        poJSON = object.searchRecord(value, byCode, Master().getIndustryID());

        if ("success".equals((String) poJSON.get("result"))) {
            Detail(detailRow).setAccountNo(object.getModel().getAccountCode());
        }
        return poJSON;
    }

    /**
     * Searches and selects a System Table record for a detail row.
     *
     * @param value search value
     * @param byCode true if searching by code, false if by description
     * @param detailRow index of the detail row to update
     * @return JSONObject result of search operation
     * @throws SQLException if database error occurs
     * @throws GuanzonException if search fails
     */
    public JSONObject SearchSysTable(String value, boolean byCode, int detailRow)
            throws ExceptionInInitializerError, SQLException, GuanzonException {
        SysTable object = new TBJControllers(poGRider, logwrapr).SysTable();
        object.setRecordStatus("1");

        poJSON = object.searchRecord(value, byCode);
        if ("success".equals((String) poJSON.get("result"))) {
            Detail(detailRow).setTableNm(object.getModel().getTableNamex());
            System.out.println("table Name : " + Detail(detailRow).getTableNm());
        }
        return poJSON;
    }

    /**
     * Searches for a Transaction Source Code record and updates the table name
     * of the specified detail row when a match is found.
     *
     * @param value the search value entered by the user
     * @param detailRow the index of the detail row to update
     * @return JSONObject containing the result of the search operation
     * @throws ExceptionInInitializerError if controller initialization fails
     * @throws SQLException if a database access error occurs
     * @throws GuanzonException if the search operation fails
     */
    public JSONObject SearchSourceCodeTable(String value, int detailRow)
            throws ExceptionInInitializerError, SQLException, GuanzonException {
        TransactionSourceTable object = new ParamControllers(poGRider, logwrapr).TransactionSourceTable();
        object.setRecordStatus("1");

        poJSON = object.searchRecord(value, Master().getSourceCode(), false);
        if ("success".equals((String) poJSON.get("result"))) {
            Detail(detailRow).setTableNm(object.getModel().getTableNme());
        }
        return poJSON;
    }

    /**
     * Opens a browse dialog to search Transaction Source Table records and
     * loads the selected transaction source into the specified detail row.
     *
     * @param value the search value entered by the user
     * @param detailRow the index of the detail row to update
     * @return JSONObject containing the result of the browse and load operation
     * @throws ExceptionInInitializerError if controller initialization fails
     * @throws SQLException if a database error occurs
     * @throws GuanzonException if transaction loading fails
     * @throws CloneNotSupportedException if cloning of the transaction object
     * fails
     */
    public JSONObject SearchTransSourceTbl(String value, int detailRow)
            throws ExceptionInInitializerError, SQLException, GuanzonException, CloneNotSupportedException {

        String lsSQL = "SELECT sSourceCD, sTableNme, cTablePri, cTableTyp FROM xxxTransactionSourceTable "
                + "WHERE sSourceCD LIKE " + SQLUtil.toSQL("%" + Master().getSourceCode());
        System.out.print(lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                value,
                "Source Code»Table Name",
                "sSourceCD»sTableNme",
                "sSourceCD»sTableNme",
                1);

        if (poJSON != null) {
            poJSON = openTransaction((String) poJSON.get("sSourceCD"));
            Detail(detailRow).setTableNm((String) poJSON.get("sTableNme"));
            return poJSON;
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }

    /**
     * Searches and selects a System Table Columns record for a detail row.
     *
     * @param value search value
     * @param byCode true if searching by code, false if by description
     * @param detailRow index of the detail row to update
     * @return JSONObject result of search operation
     * @throws SQLException if database error occurs
     * @throws GuanzonException if search fails
     */
    public JSONObject SearchSysColumn(String value, boolean byCode, int detailRow)
            throws ExceptionInInitializerError, SQLException, GuanzonException {
        SysColumn object = new TBJControllers(poGRider, logwrapr).SysColumn();
        object.setRecordStatus("1");
        poJSON = object.searchRecord(value, byCode);

        if ("success".equals((String) poJSON.get("result"))) {
            Detail(detailRow).setDerivedField(object.getModel().getColumnID());
            System.out.println("table Name : " + Detail(detailRow).getTableNm());
        }
        return poJSON;
    }

    /**
     * Initializes the base SQL query for browsing TBJ transactions.
     *
     * <p>
     * This method sets the SQL_BROWSE string with a SELECT statement that
     * retrieves the following fields from the TBJ_Master table and related
     * tables:</p>
     * <ul>
     * <li>a.sGLTranNo - Transaction number from the master table</li>
     * <li>a.sCategrCd - Category code from the master table</li>
     * <li>b.sSourceNm - Source name from the transaction source table</li>
     * <li>a.sIndstCde - Industry code from the master table</li>
     * <li>c.sDescript - Category description from the category table</li>
     * <li>a.sRemarksx - Remarks from the master table</li>
     * <li>a.cTranStat - Record status from the master table</li>
     * </ul>
     *
     * <p>
     * The query uses LEFT JOIN to include related data from the
     * xxxTransactionSource and Category tables. This base query is later
     * modified dynamically to include filters and conditions when performing
     * searches or browsing transactions.</p>
     */
    public void initSQL() {
        SQL_BROWSE = "SELECT "
                + " a.sGLTranNo, "
                + " a.sCategrCd, "
                + " b.sSourceNm, "
                + " a.sIndstCde, "
                + " c.sDescript, "
                + " a.sRemarksx, "
                + " a.cTranStat, "
                + " CASE a.cTranStat "
                + " WHEN '0' THEN 'OPEN'" 
                + " WHEN '1' THEN 'CONFIRMED'" 
                + " WHEN '3' THEN 'VOID'" 
                + " ELSE 'UNKNOWN'" 
                + " END AS status"
                + " FROM TBJ_Master a "
                + " LEFT JOIN xxxTransactionSource b "
                + " ON a.sSourceCD = b.sSourceCD "
                + " LEFT JOIN Category c "
                + " ON a.sCategrCd = c.sCategrCd";
    }

    /**
     * Searches and opens a TBJ transaction based on a given search value and
     * filter type.
     *
     * <p>
     * This method constructs a dynamic SQL query to browse TBJ transactions
     * with optional filters and record status conditions. The query is executed
     * using ShowDialogFX.Browse, which displays the results for selection. If a
     * record is selected, the transaction is opened using its transaction
     * number.</p>
     *
     * <p>
     * The search supports two filter types:</p>
     * <ul>
     * <li>"tfSearchTransaction" - filters by transaction number
     * (sGLTranNo)</li>
     * <li>"tfSearchSource" - filters by source name (sSourceNm)</li>
     * </ul>
     *
     * <p>
     * If multiple record status codes are set in {@code psTranStat}, the SQL
     * will include all specified statuses in the WHERE clause using IN(). If
     * only one status is present, it will use a simple equality check.</p>
     *
     * @param fsValue the value to search for
     * @param byFilter the filter type; either "tfSearchTransaction" or
     * "tfSearchSource"
     * @return JSONObject containing the result of the search operation:
     * <ul>
     * <li>"success" if a transaction was selected and opened</li>
     * <li>"error" if no record was loaded or an error occurred</li>
     * </ul>
     * @throws CloneNotSupportedException if cloning the model fails when
     * opening the transaction
     * @throws SQLException if a database error occurs while searching or
     * opening the transaction
     * @throws GuanzonException if business rules prevent opening the
     * transaction
     */
    public JSONObject SearchTransaction(String fsValue, String byFilter) throws CloneNotSupportedException, SQLException, GuanzonException {
        poJSON = new JSONObject();
        String lsTransStat = "";
        String lsFilter = "";
        if (psTranStat.length() > 1) {
            for (int lnCtr = 0; lnCtr <= psTranStat.length() - 1; lnCtr++) {
                lsTransStat += ", " + SQLUtil.toSQL(Character.toString(psTranStat.charAt(lnCtr)));
            }
            lsTransStat = " a.cTranStat IN (" + lsTransStat.substring(2) + ")";
        } else {
            lsTransStat = " a.cTranStat = " + SQLUtil.toSQL(psTranStat);
        }
        initSQL();

        String lsSQL = MiscUtil.addCondition(SQL_BROWSE, lsTransStat);
        if (byFilter !=null && !byFilter.isEmpty()){
            switch (byFilter) {
            case "tfSearchTransaction":
                lsFilter = " a.sGLTranNo LIKE  " + SQLUtil.toSQL(fsValue + "%");
                break;
            case "tfSearchSource":
                lsFilter = " b.sSourceNm LIKE  " + SQLUtil.toSQL(fsValue + "%");
                break;
            }
             lsSQL = MiscUtil.addCondition(lsSQL, lsFilter);
        }
       
        System.out.println("SQL EXECUTED: " + lsSQL);
        poJSON = ShowDialogFX.Browse(poGRider,
                lsSQL,
                fsValue,
                "Transaction No»Source»Category»Status",
                "a.sGLTranNo»b.sSourceNm»c.sDescript»status",
                "a.sGLTranNo»b.sSourceNm»c.sDescript»status",
                1);

        if (poJSON != null) {
            return OpenTransaction((String) poJSON.get("sGLTranNo"));
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
    }
    /**
     * Displays a modal window for building a computable column formula for a
     * detail row.
     * <p>
     * This method opens a draggable JavaFX modal where users can select columns
     * from a specified table and apply mathematical operations (+, -, *, /) to
     * create a formula. The modal prevents invalid formulas such as:</p>
     * <ul>
     * <li>Starting with an operator</li>
     * <li>Two operators in a row</li>
     * <li>Two columns consecutively without an operator</li>
     * <li>Ending the formula with an operator</li>
     * </ul>
     * <p>
     * The modal includes:</p>
     * <ul>
     * <li>A non-editable TextField to show the current formula</li>
     * <li>Buttons for mathematical operators</li>
     * <li>Buttons for computable columns (fetched dynamically from the
     * database)</li>
     * <li>"Done" button to save the formula to the detail row</li>
     * <li>"Clear" button to reset the formula</li>
     * </ul>
     * <p>
     * The selected formula is saved into the specified detail row using
     * {@code Detail(detailRow).setDerivedField()} once the user clicks
     * "Done".</p>
     * @param tableName the name of the table from which computable columns are
     * fetched
     * @param detailRow the index of the detail row where the formula will be
     * stored
     * @throws SQLException if a database error occurs while fetching computable
     * columns
     */

    public void show(String tableName, int detailRow) throws SQLException {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Computable Columns & Operations");
        modal.initStyle(StageStyle.UTILITY); // removes minimize/close buttons

        VBox modalRoot = new VBox(15);
        modalRoot.setPadding(new Insets(20));
        modalRoot.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" + // semi-transparent white
                "-fx-background-radius: 15;" + // rounded corners
                "-fx-border-radius: 15;"+ 
                "-fx-border-color: #cccccc;"+ // subtle border
                "-fx-border-width: 1;"+ 
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.5, 0, 2);" // soft shadow
        );

        // Variables to track initial mouse position
        final double[] offsetX = {0};
        final double[] offsetY = {0};

        // Make modal draggable by clicking anywhere on the modal root
        modalRoot.setOnMousePressed(event -> {
            offsetX[0] = event.getSceneX();
            offsetY[0] = event.getSceneY();
        });

        modalRoot.setOnMouseDragged(event -> {
            modal.setX(event.getScreenX() - offsetX[0]);
            modal.setY(event.getScreenY() - offsetY[0]);
        });

        // --- TextField at the top ---
        TextField txtFormula = new TextField();
        txtFormula.setPromptText("Click columns and operators to build formula...");
        txtFormula.setEditable(false);
        txtFormula.setMinHeight(30);
        modalRoot.getChildren().add(txtFormula);

        // --- Internal token list (columns and operators) ---
        List<String> formulaTokens = new ArrayList<>();

        // --- Math operation buttons ---
        GridPane mathOps = new GridPane();
        mathOps.setHgap(10);
        mathOps.setVgap(10);
        String[] operations = {"+", "-", "*", "/"};
        for (int i = 0; i < operations.length; i++) {
            String op = operations[i];
            Button btn = new Button(op);
            btn.setPrefSize(50, 50);
            btn.setFont(Font.font("System", FontWeight.BOLD, 20));
            btn.setStyle(
                    "-fx-background-color: #4CAF50;"
                    + "-fx-text-fill: white;"
                    + "-fx-background-radius: 5;"
            );
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"));
            btn.setOnAction(e -> {
                // Validation: prevent consecutive operators
                if (!formulaTokens.isEmpty() && "+-*/".contains(formulaTokens.get(formulaTokens.size() - 1))) {
                    ShowMessageFX.Warning("Cannot enter two operators in a row!", "Invalid formula!", "TBJ Parameter");
                    return;
                }

                // Validation: cannot start with operator
                if (formulaTokens.isEmpty()) {
                    ShowMessageFX.Warning("Formula cannot start with an operator!", "Invalid formula!", "TBJ Parameter");
                    return;
                }

                txtFormula.appendText(" " + op + " ");
                formulaTokens.add(op);
            });
            mathOps.add(btn, i, 0);
        }

        // --- Computable column buttons (3 per row) ---
        GridPane columnGrid = new GridPane();
        columnGrid.setHgap(10);
        columnGrid.setVgap(10);
        columnGrid.setPadding(new Insets(5));
        List<ColumnInfo> computableColumns = fetchComputableColumnsWithDesc(tableName);
        int colCount = 3;
        for (int i = 0; i < computableColumns.size(); i++) {
            ColumnInfo col = computableColumns.get(i);
            Button colBtn = new Button(col.description);
            colBtn.setMinWidth(150);
            colBtn.setStyle(
                    "-fx-font-size: 13px;"
                    + "-fx-background-color: #2196F3;"
                    + "-fx-text-fill: white;"
                    + "-fx-background-radius: 5;"
            );
            colBtn.setOnMouseEntered(e -> colBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white;"));
            colBtn.setOnMouseExited(e -> colBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;"));

            colBtn.setOnAction(e -> {
                // Validation: cannot place column immediately after another column
                if (!formulaTokens.isEmpty() && !"+-*/%".contains(formulaTokens.get(formulaTokens.size() - 1))) {
                    ShowMessageFX.Warning("Cannot place two columns consecutively without an operator!", "Invalid formula!", "TBJ Parameter");
                    return;
                }

                txtFormula.appendText(col.description);
                formulaTokens.add(col.name);
            });

            int row = i / colCount;
            int colIndex = i % colCount;
            columnGrid.add(colBtn, colIndex, row);
        }

        ScrollPane scrollPane = new ScrollPane(columnGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);

        // --- Done and Clear buttons ---
        HBox actionButtons = new HBox(10);
        Button btnDone = new Button("Done");
        Button btnClear = new Button("Clear");

        // common size
        double btnSizeW = 100;
        double btnSizeH = 40;

        // common style
        String buttonStyle
                = "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-color: #4CAF50;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 6;";

        // apply to both
        for (Button btn : new Button[]{btnDone, btnClear}) {
            btn.setMinSize(btnSizeW, btnSizeH);
            btn.setPrefSize(btnSizeW, btnSizeH);
            btn.setMaxSize(btnSizeW, btnSizeH);
            btn.setStyle(buttonStyle);
        }

        btnDone.setOnAction(e -> {
            if (formulaTokens.isEmpty()) {
                ShowMessageFX.Warning("Please select at least one column.", "Formula is empty!", "TBJ Parameter");
                return;
            }

            String last = formulaTokens.get(formulaTokens.size() - 1);
            if ("+-*/%".contains(last)) {
                ShowMessageFX.Warning("Formula cannot end with an operator.", "Invalid formula!", "TBJ Parameter");
                return;
            }

            // All validation passed, join tokens to save
            StringBuilder finalFormula = new StringBuilder();
            for (String t : formulaTokens) {
                finalFormula.append(t);
            }

            Detail(detailRow).setDerivedField(finalFormula.toString());
            modal.close();
        });

        btnClear.setOnAction(e -> {
            txtFormula.clear();
            formulaTokens.clear();
        });

        actionButtons.getChildren().addAll(btnDone, btnClear);

        modalRoot.getChildren().addAll(mathOps, scrollPane, actionButtons);

        Scene modalScene = new Scene(modalRoot);
        modal.setScene(modalScene);
        modal.sizeToScene();
        modal.showAndWait();
    }

    // Helper class for column info
    /**
     * Represents metadata for a computable database column.
     * <p>
     * This class holds the column's technical name and its user-friendly
     * description, which are used when building derived field formulas in the
     * UI.
     * </p>
     */
    private static class ColumnInfo {
        String name;        // sColumnNm
        String description; // sColumnDs

        public ColumnInfo(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Fetches a list of computable columns for a given table along with their
     * descriptions.
     * <p>
     * This method queries the {@code xxxSysColumn} table for columns that are:
     * <ul>
     * <li>Active (cRecdStat = '1')</li>
     * <li>Not excluded (sColumnNm NOT IN ('nEntryNox'))</li>
     * <li>Of supported data types</li>
     * </ul>
     * </p>
     * <p>
     * Supported column data types are based on {@link java.sql.Types} and
     * include:
     * </p>
     * <ul>
     * <li><b>Numeric Types</b>:
     * <ul>
     * <li>  2 - NUMERIC</li>
     * <li>  3 - DECIMAL</li>
     * <li>  4 - INTEGER</li>
     * <li>  5 - SMALLINT</li>
     * <li> -5 - BIGINT</li>
     * <li>  6 - FLOAT</li>
     * <li>  7 - REAL</li>
     * <li>  8 - DOUBLE</li>
     * </ul>
     * </li>
     * <li><b>String Types</b>:
     * <ul>
     * <li>12 - VARCHAR</li>
     * <li>-1 - LONGVARCHAR</li>
     * </ul>
     * </li>
     * <li><b>Boolean Type</b>:
     * <ul>
     * <li>16 - BOOLEAN</li>
     * </ul>
     * </li>
     * </ul>
     * <p>
     * If a table name is provided, only columns belonging to that table are
     * returned. If null or empty, columns from all tables are fetched.
     * </p>
     * @param tableName the name of the table whose computable columns are to be
     * fetched; may be null or empty to fetch columns from all tables
     * @return a list of {@link ColumnInfo} objects containing the column name
     * and description
     * @throws SQLException if a database error occurs while executing the query
     */

    private List<ColumnInfo> fetchComputableColumnsWithDesc(String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();
        String sql = "SELECT sColumnNm, sColumnDs FROM xxxSysColumn "
                + "WHERE cRecdStat = '1' AND nColumnTp IN ('2','3','4','5','-5','6','7','8','12','-1','16')"
                + " AND sColumnNm NOT IN ('nEntryNox')";

        if (tableName != null && !tableName.isEmpty()) {
            sql += " AND sTableNme = " + SQLUtil.toSQL(tableName);
        }

        ResultSet rs = poGRider.executeQuery(sql);
        while (rs.next()) {
            columns.add(new ColumnInfo(
                    rs.getString("sColumnNm"),
                    rs.getString("sColumnDs")
            ));
        }
        return columns;
    }
    
    /**
     * Converts column names in a formula to their human-readable descriptions.
     * <p>
     * This method takes a formula string (e.g., "colA+colB") and replaces each
     * column name with its corresponding description from the
     * {@code xxxSysColumn} table for the specified detail row's table.</p>
     * <p>
     * The process works as follows:</p>
     * <ol>
     * <li>Extract all unique column names from the formula by splitting on
     * operators (+, -, *, /, %).</li>
     * <li>Query {@code xxxSysColumn} to fetch the column descriptions for the
     * given table.</li>
     * <li>Replace the column names in the formula with their corresponding
     * descriptions.</li>
     * </ol>
     * @param fieldName the formula containing column names
     * @param detailRow the index of the detail row whose table will be used to
     * resolve column descriptions
     * @return a string where all column names in the formula are replaced with
     * their descriptions; returns the original formula if no columns are found
     * or if {@code fieldName} is null/empty
     * @throws SQLException if a database error occurs while querying the column
     * descriptions
     * @throws GuanzonException if the operation fails due to a transaction or
     * application-related error
     */
    public String getFieldName(String fieldName, int detailRow) throws SQLException, GuanzonException {

        if (fieldName == null || fieldName.isEmpty()) {
            return "";
        }

        // --- 1. Extract column names from formula ---
        String[] tokens = fieldName.split("[+\\-*/%]");
        List<String> columns = new ArrayList<>();

        for (String token : tokens) {
            token = token.trim();
            if (!token.isEmpty() && !columns.contains(token)) {
                columns.add(token);
            }
        }

        if (columns.isEmpty()) {
            return fieldName;
        }

        // --- 2. Build SQL ---
        StringBuilder lsSQL = new StringBuilder(
                "SELECT "
                + "sColumnNm, "
                + "sColumnDs "
                + "FROM xxxSysColumn "
                + "WHERE sTableNme = " + SQLUtil.toSQL(Detail(detailRow).getTableNm())
                + " AND  sColumnNm IN ("
        );

        for (int i = 0; i < columns.size(); i++) {
            lsSQL.append(SQLUtil.toSQL(columns.get(i)));
            if (i < columns.size() - 1) {
                lsSQL.append(", ");
            }
        }
        lsSQL.append(")");

        System.out.println("Executing SQL: " + lsSQL);

        // --- 3. Map column name -> description ---
        Map<String, String> columnMap = new HashMap<>();
        ResultSet loRS = poGRider.executeQuery(lsSQL.toString());

        while (loRS.next()) {
            columnMap.put(
                    loRS.getString("sColumnNm"),
                    loRS.getString("sColumnDs")
            );
        }

        // --- 4. Replace column names in formula with descriptions ---
        String formattedFormula = fieldName;

        for (Map.Entry<String, String> entry : columnMap.entrySet()) {
            formattedFormula = formattedFormula.replace(
                    entry.getKey(),
                    entry.getValue()
            );
        }
        return formattedFormula;
    } 
    
    /**
     * Validates duplicate entries within the detail records.
     *
     * <p>
     * <b>Validation Adjustment (April 1, 2026 | Author: Teejei De
     * Celis):</b><br>
     * This validation was updated to check duplicates based solely on
     * <code>accountNo</code> instead of a composite key consisting of
     * <code>tableNm</code>, <code>accountType</code>,
     * <code>derivedField</code>, and <code>accountNo</code>.
     * </p>
     *
     * <p>
     * <b>Previous Behavior:</b><br>
     * Duplicate detection used a combination of multiple fields to determine
     * uniqueness per row.
     * </p>
     *
     * <p>
     * <b>Current Behavior:</b><br>
     * Each detail row must have a unique <code>accountNo</code>. If the same
     * account number appears more than once, the method flags it as a
     * duplicate.
     * </p>
     *
     * <p>
     * <b>Validation Process:</b>
     * <ul>
     * <li>Iterates through all detail records.</li>
     * <li>Stores each <code>accountNo</code> in a Set.</li>
     * <li>Checks if the current <code>accountNo</code> already exists.</li>
     * <li>If duplicate is found, returns an error with the row number.</li>
     * <li>If no duplicates are found, returns success.</li>
     * </ul>
     * </p>
     *
     * <p>
     * <b>Return Value:</b><br>
     * Returns a {@link JSONObject} containing:
     * <ul>
     * <li><code>"result"</code> = "success" if no duplicates found</li>
     * <li><code>"result"</code> = "error" if duplicate exists</li>
     * <li><code>"message"</code> = descriptive error message with row
     * number</li>
     * </ul>
     * </p>
     *
     * @return {@link JSONObject} result of the duplicate validation check
     */
    public JSONObject checkDuplicateDetail() {
        poJSON = new JSONObject();

        Set<String> uniqueSet = new HashSet<>();

        for (int lnCtr = 0; lnCtr <= getDetailCount()-1; lnCtr++) {

//            String tableNm = String.valueOf(Detail(lnCtr).getTableNm());
//            String accountType = String.valueOf(Detail(lnCtr).getAccountType());
//            String derivedField = String.valueOf(Detail(lnCtr).getDerivedField());

            /*adjustment for the duplicate */
            String accountNo = String.valueOf(Detail(lnCtr).getAccountNo());

            // Combine all 4 fields as a unique key
//            String key = tableNm + "|" + accountType + "|" + derivedField + "|" + accountNo;
              String key = accountNo;
            // Check duplicate
            if (uniqueSet.contains(key)) {
                poJSON.put("result", "error");
                poJSON.put("message", "Duplicate detail found at row: " + (lnCtr + 1));
                return poJSON;
            }

            uniqueSet.add(key);
        }

        poJSON.put("result", "success");
        return poJSON;
    }
    
}
