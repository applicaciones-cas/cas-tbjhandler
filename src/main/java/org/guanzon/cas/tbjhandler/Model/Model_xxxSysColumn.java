/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.tbjhandler.Model;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;

/**
 * Model class for xxxSysColumn table
 */
public class Model_xxxSysColumn extends Model {

    @Override
    public void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(
                    System.getProperty("sys.default.path.metadata") + XML, 
                    getTable()
            );

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);

            // assign default values
            poEntity.updateString("cRecdStat", "1");
            poEntity.updateObject("sModified", poGRider.getUserID());
            poEntity.updateObject("dModified", SQLUtil.toDate(xsDateShort(poGRider.getServerDate()), SQLUtil.FORMAT_SHORT_DATE));

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = "sColumnID";

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    private static String xsDateShort(Date fdValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(fdValue);
    }

    public JSONObject setColumnID(String columnID) {
        return setValue("sColumnID", columnID); 
    }
    public String getColumnID() { 
        return (String) getValue("sColumnID"); 
    }

    public JSONObject setTableNme(String tableName) {
        return setValue("sTableNme", tableName); 
    }
    public String getTableNme() { 
        return (String) getValue("sTableNme"); 
    }

    public JSONObject setColumnName(String columnName) {
        return setValue("sColumnNm", columnName); 
    }
    public String getColumnName() { 
        return (String) getValue("sColumnNm"); 
    }

    public JSONObject setColumnLabel(String columnLabel) {
        return setValue("sColLabel", columnLabel); 
    }
    public String getColumnLabel() { 
        return (String) getValue("sColLabel"); 
    }

    public JSONObject setColumnDesc(String columnDescription) {
        return setValue("sColumnDs", columnDescription); 
    }
    public String getColumnDesc() { 
        return (String) getValue("sColumnDs"); 
    }

    public JSONObject setRemarks(String remarks) {
        return setValue("sRemarksx", remarks); 
    }
    public String getRemarks() { 
        return (String) getValue("sRemarksx"); 
    }

    public JSONObject setPosition(int position) {
        return setValue("nPosition", position); 
    }
    public int getPosition() { 
        return (Integer) getValue("nPosition"); 
    }

    public JSONObject setColumnType(int columnType) {
        return setValue("nColumnTp", columnType); 
    }
    public int getColumnType() { 
        return (Integer) getValue("nColumnTp"); 
    }

    public JSONObject IsNullable(boolean value) {
        // store "1" for true, "0" for false
        return setValue("cIsNullxx", value ? "1" : "0");
    }

    public boolean IsNullable() {
        // return true if the stored value is "1"
        String val = (String) getValue("cIsNullxx");
        return "1".equals(val);
    }

    public JSONObject setLength(int length) {
        return setValue("nLengthxx", length); 
    }
    public int getLength() { 
        return (Integer) getValue("nLengthxx"); 
    }

    public JSONObject setFixedLength(String fixLength) {
        return setValue("cFixedLen", fixLength); 
    }
    public String getFixedLength() { 
        return (String) getValue("cFixedLen"); 
    }

    public JSONObject setPrecision(int precision) {
        return setValue("nPrecisnx", precision); 
    }
    public int getPrecision() { 
        return (Integer) getValue("nPrecisnx"); 
    }

    public JSONObject setScale(int scale) {
        return setValue("nScalexxx", scale); 
    }
    public int getScale() { 
        return (Integer) getValue("nScalexxx"); 
    }

    public JSONObject setFormat(String format) {
        return setValue("sFormatxx", format); 
    }
    public String getFormat() { 
        return (String) getValue("sFormatxx"); 
    }

    public JSONObject setRegType(String regType) {
        return setValue("sRegTypex", regType); 
    }
    public String getRegType() { 
        return (String) getValue("sRegTypex"); 
    }

    public JSONObject setValueFrom(String valueFrom) {
        return setValue("sValueFrm", valueFrom); 
    }
    public String getValueFrom() { 
        return (String) getValue("sValueFrm"); 
    }

    public JSONObject setValueThrough(String valueThru) {
        return setValue("sValueThr", valueThru); 
    }
    public String getValueThrough() { 
        return (String) getValue("sValueThr"); 
    }

    public JSONObject setValueList(String valueList) {
        return setValue("sValueLst", valueList); 
    }
    public String getValueList() { 
        return (String) getValue("sValueLst"); 
    }

    public JSONObject setRecordStatus(String status) {
        return setValue("cRecdStat", status); 
    }
    public String getRecordStatus() { 
        return (String) getValue("cRecdStat"); 
    }

    public JSONObject setModifyingId(String modifyingId) {
        return setValue("sModified", modifyingId);
    }

    public String getModifyingId() {
        return (String) getValue("sModified");
    }

    public JSONObject setModifiedDate(Date modifiedDate) {
        return setValue("dModified", modifiedDate);
    }

    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }

    @Override
    public String getNextCode() {
        return MiscUtil.getNextCode(
                this.getTable(),
                ID,
                false,
                poGRider.getGConnection().getConnection(),
                poGRider.getBranchCode()
        );
    }
}
