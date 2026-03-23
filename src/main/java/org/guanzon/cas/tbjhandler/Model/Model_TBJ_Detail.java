package org.guanzon.cas.tbjhandler.Model;

import java.sql.SQLException;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.parameter.model.Model_xxxTransactionSourceTable;
import org.guanzon.cas.parameter.services.ParamModels;
import org.guanzon.cas.tbjhandler.Services.TBJModels;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.cashflow.model.Model_Account_Chart;
import ph.com.guanzongroup.cas.cashflow.services.CashflowModels;

/**
 * Model_TBJ_Detail --------------------------------------------------------
 * Data model for Transaction Book Journal (TBJ) detail records.
 * <p>
 * Responsible for:
 * <ul>
 *     <li>Initializing TBJ detail records with default values.</li>
 *     <li>Providing getter/setter methods for all fields in TBJ_Detail table.</li>
 *     <li>Managing references to Account Chart, System Table, and System Column models.</li>
 *     <li>Interfacing with backend services to open or initialize related records.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Start Date: 2026-02-05 <br>
 * End Date: 2026-02-06
 * </p>
 * 
 * @author Teejei
 */
public class Model_TBJ_Detail extends Model {

    private Model_Account_Chart poAccountChart;
    private Model_xxxSysTable poxxxSysTable;
    private Model_xxxSysColumn poxxxSysColumn;
    private Model_xxxTransactionSourceTable poTransactionSourceTable;

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            // Assign default values
            poEntity.updateObject("nEntryNox", 0);          // Entry number starts at 0
            poEntity.updateObject("cIsReqrdx", 0);          // Required flag default = true
            poEntity.updateObject("cAcctNoSr", "0");        // Default account number series
            poEntity.updateObject("cActivexx", 1);          // Active by default

            poEntity.insertRow();
            poEntity.moveToCurrentRow();
            poEntity.absolute(1);

            ID = "sGLTranNo";  // Primary key 1
            ID2 = "nEntryNox"; // Primary key 2

            // Initialize reference objects
            CashflowModels cashFlow = new CashflowModels(poGRider);
            poAccountChart = cashFlow.Account_Chart();
            ParamModels paramModels = new ParamModels(poGRider);
            poTransactionSourceTable = paramModels.TransactionSourceTable();

            TBJModels tbj = new TBJModels(poGRider);
            poxxxSysTable = tbj.SysTable();
            poxxxSysColumn = tbj.SysColumn();

            pnEditMode = EditMode.UNKNOWN;

        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public String getNextCode() {
        return "";
    }

    // --- Transaction Number ---
    /**
     * Sets the transaction number for this TBJ detail.
     * @param transactionNo Transaction number string
     * @return JSONObject result of the set operation
     */
    public JSONObject setTransactionNo(String transactionNo) {
        return setValue("sGLTranNo", transactionNo);
    }

    /**
     * Returns the transaction number for this TBJ detail.
     * @return Transaction number string
     */
    public String getTransactionNo() {
        return (String) getValue("sGLTranNo");
    }

    // --- Entry Number ---
    /**
     * Sets the entry number of the TBJ detail.
     * @param entryNo Integer entry number
     * @return JSONObject result of the set operation
     */
    public JSONObject setEntryNo(int entryNo) {
        return setValue("nEntryNox", entryNo);
    }

    /**
     * Returns the entry number of the TBJ detail.
     * @return Entry number
     */
    public int getEntryNo() {
        return (int) getValue("nEntryNox");
    }

    // --- Table Name ---
    /**
     * Sets the database table name associated with this detail.
     * @param tblName Table name string
     * @return JSONObject result of the set operation
     */
    public JSONObject setTableNm(String tblName) {
        return setValue("sTableNme", tblName);
    }

    /**
     * Returns the database table name associated with this detail.
     * @return Table name string
     */
    public String getTableNm() {
        return (String) getValue("sTableNme");
    }

    // --- Required flag ---
    /**
     * Marks the TBJ detail as required or not.
     * @param isRequired True if required, false otherwise
     * @return JSONObject result of the set operation
     */
    public JSONObject isRequired(boolean isRequired) {
        return setValue("cIsReqrdx", isRequired ? "1" : "0");
    }

    /**
     * Checks if the TBJ detail is required.
     * @return True if required, false otherwise
     */
    public boolean isRequired() {
        Object value = getValue("cIsReqrdx");
        return "1".equals(String.valueOf(value));
    }

    // --- Account Number Series ---
    /**
     * Sets the account number series.
     * @param accountNoSr Series string
     * @return JSONObject result of the set operation
     */
    public JSONObject setAccountNoSr(String accountNoSr) {
        return setValue("cAcctNoSr", accountNoSr);
    }

    /**
     * Returns the account number series.
     * @return Series string
     */
    public String getAccountNoSr() {
        return (String) getValue("cAcctNoSr");
    }

    // --- Account Number ---
    /**
     * Sets the account number for this TBJ detail.
     * @param accountNo Account number string
     * @return JSONObject result of the set operation
     */
    public JSONObject setAccountNo(String accountNo) {
        return setValue("sAcctNoxx", accountNo);
    }

    /**
     * Returns the account number for this TBJ detail.
     * @return Account number string
     */
    public String getAccountNo() {
        return (String) getValue("sAcctNoxx");
    }

    // --- Field Source ---
    /**
     * Sets the field source for this TBJ detail.
     * @param fieldSource Source string
     * @return JSONObject result of the set operation
     */
    public JSONObject setFieldSource(String fieldSource) {
        return setValue("sFieldSrc", fieldSource);
    }

    /**
     * Returns the field source for this TBJ detail.
     * @return Source string
     */
    public String getFieldSource() {
        return (String) getValue("sFieldSrc");
    }

    // --- Precondition ---
    /**
     * Sets a precondition formula or rule for this TBJ detail.
     * @param precondition Precondition string
     * @return JSONObject result of the set operation
     */
    public JSONObject setPrecondition(String precondition) {
        return setValue("sPreCondx", precondition);
    }

    /**
     * Returns the precondition formula or rule for this TBJ detail.
     * @return Precondition string
     */
    public String getPrecondition() {
        return (String) getValue("sPreCondx");
    }

    // --- Account Type ---
    /**
     * Sets the account type (e.g., debit, credit) for this TBJ detail.
     * @param accountType Account type string
     * @return JSONObject result of the set operation
     */
    public JSONObject setAccountType(String accountType) {
        return setValue("cAcctType", accountType);
    }

    /**
     * Returns the account type (e.g., debit, credit) for this TBJ detail.
     * @return Account type string
     */
    public String getAccountType() {
        return (String) getValue("cAcctType");
    }

    // --- Derived Field (Computed Formula) ---
    /**
     * Sets the derived field/formula for this TBJ detail.
     * @param derivedField Computed field formula
     * @return JSONObject result of the set operation
     */
    public JSONObject setDerivedField(String derivedField) {
        return setValue("sDervdFld", derivedField);
    }

    /**
     * Returns the derived field/formula for this TBJ detail.
     * @return Computed field formula
     */
    public String getDerivedField() {
        return (String) getValue("sDervdFld");
    }

    // --- Active Flag ---
    /**
     * Sets whether the TBJ detail is active.
     * @param isActive True if active, false otherwise
     * @return JSONObject result of the set operation
     */
    public JSONObject isActive(boolean isActive) {
        return setValue("cActivexx", isActive ? "1" : "0");
    }

    /**
     * Returns whether the TBJ detail is active.
     * @return True if active, false otherwise
     */
    public boolean isActive() {
        Object value = getValue("cActivexx");
        return "1".equals(String.valueOf(value));
    }

    // --- Account Chart Reference ---
    /**
     * Returns the linked account chart object.
     * Opens or initializes the record if not already loaded.
     * @return Account chart model
     * @throws GuanzonException
     * @throws SQLException
     */
    public Model_Account_Chart AccountChart() throws GuanzonException, SQLException {
        if (!"".equals((String) getValue("sAcctNoxx"))) {
            if (poAccountChart.getEditMode() == EditMode.READY
                    && poAccountChart.getAccountCode().equals((String) getValue("sAcctNoxx"))) {
                return poAccountChart;
            } else {
                poJSON = poAccountChart.openRecord((String) getValue("sAcctNoxx"));
                if ("success".equals((String) poJSON.get("result"))) {
                    return poAccountChart;
                } else {
                    poAccountChart.initialize();
                    return poAccountChart;
                }
            }
        } else {
            poAccountChart.initialize();
            return poAccountChart;
        }
    }

    // --- System Table Reference ---
    /**
     * Returns the linked system table object.
     * Opens or initializes the record if not already loaded.
     * @return System table model
     * @throws SQLException
     * @throws GuanzonException
     */
    public Model_xxxSysTable SysTable() throws SQLException, GuanzonException {
        if (!"".equals((String) getValue("sTableNme"))) {
            if (poxxxSysTable.getEditMode() == EditMode.READY
                    && poxxxSysTable.getTableNamex().equals((String) getValue("sTableNme"))) {
                return poxxxSysTable;
            } else {
                poJSON = poxxxSysTable.openRecord((String) getValue("sTableNme"));
                if ("success".equals((String) poJSON.get("result"))) {
                    return poxxxSysTable;
                } else {
                    poxxxSysTable.initialize();
                    return poxxxSysTable;
                }
            }
        } else {
            poxxxSysTable.initialize();
            return poxxxSysTable;
        }
    }

    // --- System Column Reference ---
    /**
     * Returns the linked system column object.
     * Opens or initializes the record if not already loaded.
     * @return System column model
     * @throws GuanzonException
     * @throws SQLException
     */
    public Model_xxxSysColumn SysColumn() throws GuanzonException, SQLException {
        if (!"".equals((String) getValue("sTableNme"))) {
            if (poxxxSysColumn.getEditMode() == EditMode.READY
                    && poxxxSysColumn.getTableNme().equals((String) getValue("sTableNme"))) {
                return poxxxSysColumn;
            } else {
                poJSON = poxxxSysColumn.openRecord((String) getValue("sTableNme"));
                if ("success".equals((String) poJSON.get("result"))) {
                    return poxxxSysColumn;
                } else {
                    poxxxSysColumn.initialize();
                    return poxxxSysColumn;
                }
            }
        } else {
            poxxxSysColumn.initialize();
            return poxxxSysColumn;
        }
    }
    
    // --- Account Chart Reference ---
    /**
     * Returns the linked account chart object.
     * Opens or initializes the record if not already loaded.
     * @return Account chart model
     * @throws GuanzonException
     * @throws SQLException
     */
    public Model_xxxTransactionSourceTable TransactionSourceTable() throws GuanzonException, SQLException {
        if (!"".equals((String) getValue("sTableNme"))) {
            if (poTransactionSourceTable.getEditMode() == EditMode.READY
                    && poTransactionSourceTable.getSourceCode().equals((String) getValue("sTableNme"))) {
                return poTransactionSourceTable;
            } else {
                poJSON = poTransactionSourceTable.openRecord((String) getValue("sTableNme"));
                if ("success".equals((String) poJSON.get("result"))) {
                    return poTransactionSourceTable;
                } else {
                    poTransactionSourceTable.initialize();
                    return poTransactionSourceTable;
                }
            }
        } else {
            poTransactionSourceTable.initialize();
            return poTransactionSourceTable;
        }
    }
//    
//    @Override
//    public void setEditMode(int editMode) {
//        this.pnEditMode = editMode;
//    }
//    @Override
//    public JSONObject saveRecord() throws SQLException, GuanzonException {
//    this.poJSON = new JSONObject();
//    if (this.pnEditMode == 0 || this.pnEditMode == 2) {
//      if (this.pnEditMode == 0) {
//        if (!getNextCode().isEmpty())
//          setValue(this.ID, getNextCode()); 
//        String lsSQL = MiscUtil.makeSQL(this);
//        if (!lsSQL.isEmpty()) {
//          if (this.poGRider.executeQuery(lsSQL, getTable(), this.poGRider.getBranchCode(), "", "") > 0L) {
//            this.poJSON = new JSONObject();
//            this.poJSON.put("result", "success");
//            this.poJSON.put("message", "Record saved successfully.");
//          } else {
//            this.poJSON = new JSONObject();
//            this.poJSON.put("result", "error");
//            this.poJSON.put("message", this.poGRider.getMessage());
//          } 
//        } else {
//          this.poJSON = new JSONObject();
//          this.poJSON.put("result", "error");
//          this.poJSON.put("message", "No record to save.");
//        } 
//      } else {
//        Model_TBJ_Detail loOldEntity = new Model_TBJ_Detail();
//        loOldEntity.setApplicationDriver(this.poGRider);
//        loOldEntity.setXML(this.XML);
//        loOldEntity.setTableName(this.TABLE);
//        loOldEntity.initialize();
//        if (!this.ID5.isEmpty()) {
//          loOldEntity.ID5 = this.ID5;
//          loOldEntity.ID4 = this.ID4;
//          loOldEntity.ID3 = this.ID3;
//          loOldEntity.ID2 = this.ID2;
//          loOldEntity.ID = this.ID;
//          this.poJSON = loOldEntity.openRecord((String)getValue(this.ID), getValue(this.ID2), getValue(this.ID3), getValue(this.ID4), getValue(this.ID5));
//        } else if (!this.ID4.isEmpty()) {
//          loOldEntity.ID4 = this.ID4;
//          loOldEntity.ID3 = this.ID3;
//          loOldEntity.ID2 = this.ID2;
//          loOldEntity.ID = this.ID;
//          this.poJSON = loOldEntity.openRecord((String)getValue(this.ID), getValue(this.ID2), getValue(this.ID3), getValue(this.ID4));
//        } else if (!this.ID3.isEmpty()) {
//          loOldEntity.ID3 = this.ID3;
//          loOldEntity.ID2 = this.ID2;
//          loOldEntity.ID = this.ID;
//          this.poJSON = loOldEntity.openRecord((String)getValue(this.ID), getValue(this.ID2), getValue(this.ID3));
//        } else if (!this.ID2.isEmpty()) {
//          loOldEntity.ID2 = this.ID2;
//          loOldEntity.ID = this.ID;
//          this.poJSON = loOldEntity.openRecord((String)getValue(this.ID), getValue(this.ID2));
//        } else {
//          loOldEntity.ID = this.ID;
//          this.poJSON = loOldEntity.openRecord((String)getValue(this.ID));
//        } 
//        if ("success".equals(this.poJSON.get("result"))) {
//          String lsSQL;
//          if (this.ID2.isEmpty()) {
//            lsSQL = MiscUtil.makeSQL(this, loOldEntity, this.ID + " = " + SQLUtil.toSQL(getValue(this.ID)));
//          } else if (this.ID3.isEmpty()) {
//            lsSQL = MiscUtil.makeSQL(this, loOldEntity, this.ID + " = " + SQLUtil.toSQL(getValue(this.ID)) + " AND " + this.ID2 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID2)));
//          } else if (this.ID4.isEmpty()) {
//            lsSQL = MiscUtil.makeSQL(this, loOldEntity, this.ID + " = " + SQLUtil.toSQL(getValue(this.ID)) + " AND " + this.ID2 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID2)) + " AND " + this.ID3 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID3)));
//          } else if (this.ID5.isEmpty()) {
//            lsSQL = MiscUtil.makeSQL(this, loOldEntity, this.ID + " = " + SQLUtil.toSQL(getValue(this.ID)) + " AND " + this.ID2 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID2)) + " AND " + this.ID3 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID3)) + " AND " + this.ID4 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID4)));
//          } else {
//            lsSQL = MiscUtil.makeSQL(this, loOldEntity, this.ID + " = " + SQLUtil.toSQL(getValue(this.ID)) + " AND " + this.ID2 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID2)) + " AND " + this.ID3 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID3)) + " AND " + this.ID4 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID4)) + " AND " + this.ID5 + " = " + 
//                SQLUtil.toSQL(getValue(this.ID5)));
//          } 
//          if (!lsSQL.isEmpty()) {
//            if (this.poGRider.executeQuery(lsSQL, getTable(), this.poGRider.getBranchCode(), "", "") > 0L) {
//              this.poJSON = new JSONObject();
//              this.poJSON.put("result", "success");
//              this.poJSON.put("message", "Record saved successfully.");
//            } else {
//              this.poJSON = new JSONObject();
//              this.poJSON.put("result", "error");
//              this.poJSON.put("message", this.poGRider.getMessage());
//            } 
//          } else {
//            this.poJSON = new JSONObject();
//            this.poJSON.put("result", "success");
//            this.poJSON.put("message", "No updates has been made.");
//          } 
//        } else {
//          this.poJSON = new JSONObject();
//          this.poJSON.put("result", "error");
//          this.poJSON.put("message", "Record discrepancy. Unable to save record.");
//        } 
//      } 
//    } else {
//      this.poJSON = new JSONObject();
//      this.poJSON.put("result", "error");
//      this.poJSON.put("message", "Invalid update mode. Unable to save record.");
//      return this.poJSON;
//    } 
//    return this.poJSON;
//  }
}
