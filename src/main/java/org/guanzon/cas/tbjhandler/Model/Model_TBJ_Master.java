/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.tbjhandler.Model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.parameter.model.Model_Category;
import org.guanzon.cas.parameter.model.Model_Company;
import org.guanzon.cas.parameter.model.Model_Industry;
import org.guanzon.cas.parameter.model.Model_xxxTransactionSource;
import org.guanzon.cas.tbjhandler.constant.TBJ_Constant;
import org.guanzon.cas.parameter.services.ParamModels;
import org.json.simple.JSONObject;
/**
 * Model_TBJ_Master --------------------------------------------------------
 * Handles the master record of the Transaction Book Journal (TBJ).
 * Responsible for storing transaction info, validating entries,
 * managing references to category and transaction source,
 * and providing next transaction codes.
 * 
 * Includes functionality to track modification dates, user ID,
 * transaction status, source, and industry.
 * 
 * @author User
 */
public class Model_TBJ_Master extends Model {

    // Reference objects
    private Model_Category poCategory;
    private Model_xxxTransactionSource poTransactionSource;
    private Model_Company poCompany;
    private Model_Industry poIndustry;
    /**
     * Initializes the TBJ Master model.
     * Loads XML metadata, inserts default row, sets initial values,
     * and initializes reference objects.
     */
    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            // Assign default values
            poEntity.updateString("cTranStat", TBJ_Constant.OPEN);
            poEntity.updateObject("sModified", poGRider.getUserID());
            poEntity.updateObject("dModified", SQLUtil.toDate(xsDateShort(poGRider.getServerDate()), SQLUtil.FORMAT_SHORT_DATE));

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = "sGLTranNo";

            // Initialize reference objects
            ParamModels model = new ParamModels(poGRider);
            poCategory = model.Category();
            poTransactionSource = model.TransactionSource();
            poCompany = model.Company();
            poIndustry = model.Industry();

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Converts a Date to a string in yyyy-MM-dd format.
     * @param fdValue Date value
     * @return formatted date string
     */
    private static String xsDateShort(Date fdValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(fdValue);
    }

    // -------------------- Setters and Getters --------------------

    /**
     * Sets the transaction number.
     * @param transactionNo Transaction number
     * @return JSONObject result
     */
    public JSONObject setTransactionNo(String transactionNo) {
        return setValue("sGLTranNo", transactionNo);
    }

    /**
     * Returns the transaction number.
     * @return Transaction number
     */
    public String getTransactionNo() {
        return (String) getValue("sGLTranNo");
    }

    /**
     * Sets the source code of the transaction.
     * @param sourceCode Source code
     * @return JSONObject result
     */
    public JSONObject setSourceCode(String sourceCode) {
        return setValue("sSourceCD", sourceCode);
    }

    /**
     * Returns the transaction's source code.
     * @return Source code
     */
    public String getSourceCode() {
        return (String) getValue("sSourceCD");
    }

    /**
     * Sets the industry ID for the transaction.
     * @param industryID Industry ID
     * @return JSONObject result
     */
    public JSONObject setIndustryID(String industryID) {
        return setValue("sIndstCde", industryID);
    }

    /**
     * Returns the industry ID of the transaction.
     * @return Industry ID
     */
    public String getIndustryID() {
        return (String) getValue("sIndstCde");
    }

    /**
     * Sets the category ID of the transaction.
     * @param categoryID Category ID
     * @return JSONObject result
     */
    public JSONObject setCategoryID(String categoryID) {
        return setValue("sCategrCd", categoryID);
    }

    /**
     * Returns the category ID of the transaction.
     * @return Category ID
     */
    public String getCategoryID() {
        return (String) getValue("sCategrCd");
    }

    /**
     * Sets remarks or description for the transaction.
     * @param remarks Remarks
     * @return JSONObject result
     */
    public JSONObject setRemarks(String remarks) {
        return setValue("sRemarksx", remarks);
    }

    /**
     * Returns remarks or description of the transaction.
     * @return Remarks
     */
    public String getRemarks() {
        return (String) getValue("sRemarksx");
    }

    /**
     * Sets the transaction status (OPEN, CONFIRMED, etc.).
     * @param transactionStatus Transaction status
     * @return JSONObject result
     */
    public JSONObject setTransactionStatus(String transactionStatus) {
        return setValue("cTranStat", transactionStatus);
    }

    /**
     * Returns the transaction status.
     * @return Transaction status
     */
    public String getTransactionStatus() {
        return (String) getValue("cTranStat");
    }

    /**
     * Sets the user ID who last modified the transaction.
     * @param modifyingId User ID
     * @return JSONObject result
     */
    public JSONObject setModifyingId(String modifyingId) {
        return setValue("sModified", modifyingId);
    }

    /**
     * Returns the user ID who last modified the transaction.
     * @return User ID
     */
    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    /**
     * Sets the modification date.
     * @param modifiedDate Date modified
     * @return JSONObject result
     */
    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    /**
     * Returns the modification date of the transaction.
     * @return Date modified
     */
    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    /**
     * Returns the next transaction number based on current branch code.
     * @return Next transaction code
     */
    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(
                this.getTable(),
                ID,
                true,
                poGRider.getGConnection().getConnection(),
                poGRider.getBranchCode()
        );
    }

    // -------------------- Reference Objects --------------------

    /**
     * Returns the linked category object.
     * Opens or initializes the record if not already loaded.
     * @return Category model
     * @throws GuanzonException
     * @throws SQLException
     */
    public Model_Category Category() throws GuanzonException, SQLException {
        if (!"".equals((String) getValue("sCategrCd"))) {
            if (poCategory.getEditMode() == EditMode.READY
                    && poCategory.getCategoryId().equals((String) getValue("sCategrCd"))) {
                return poCategory;
            } else {
                poJSON = poCategory.openRecord((String) getValue("sCategrCd"));
                if ("success".equals((String) poJSON.get("result"))) {
                    return poCategory;
                } else {
                    poCategory.initialize();
                    return poCategory;
                }
            }
        } else {
            poCategory.initialize();
            return poCategory;
        }
    }

    /**
     * Returns the linked transaction source object.
     * Opens or initializes the record if not already loaded.
     * @return Transaction source model
     * @throws GuanzonException
     * @throws SQLException
     */
    public Model_xxxTransactionSource TransactionSource() throws GuanzonException, SQLException {
        if (!"".equals((String) getValue("sSourceCD"))) {
            if (poTransactionSource.getEditMode() == EditMode.READY
                    && poTransactionSource.getSourceCode().equals((String) getValue("sSourceCD"))) {
                return poTransactionSource;
            } else {
                poJSON = poTransactionSource.openRecord((String) getValue("sSourceCD"));
                if ("success".equals((String) poJSON.get("result"))) {
                    return poTransactionSource;
                } else {
                    poTransactionSource.initialize();
                    return poTransactionSource;
                }
            }
        } else {
            poTransactionSource.initialize();
            return poTransactionSource;
        }
    }
    
    public Model_Industry Industry() throws GuanzonException, SQLException {
        if (!"".equals((String) getValue("sIndstCde"))) {
            if (poIndustry.getEditMode() == EditMode.READY
                    && poIndustry.getIndustryId().equals((String) getValue("sIndstCde"))) {
                return poIndustry;
            } else {
                poJSON = poIndustry.openRecord((String) getValue("sIndstCde"));
                if ("success".equals((String) poJSON.get("result"))) {
                    return poIndustry;
                } else {
                    poIndustry.initialize();
                    return poIndustry;
                }
            }
        } else {
            poIndustry.initialize();
            return poIndustry;
        }
    }
    
}
