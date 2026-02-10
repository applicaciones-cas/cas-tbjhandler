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
public class Model_xxxSysTable extends Model {

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



            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            ID = "sTableNme";

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            logwrapr.severe(e.getMessage());
            System.exit(1);
        }
    }

    // sTableNme

    public JSONObject setTableNamex(String tableName) {
        return setValue("sTableNme", tableName);
    }

    public String getTableNamex() {
        return (String) getValue("sTableNme");
    }

// cTableTyp
    public JSONObject setTableType(String tableType) {
        return setValue("cTableTyp", tableType);
    }

    public String getTableType() {
        return (String) getValue("cTableTyp");
    }

// sRefFld01
    public JSONObject setReferenceField1(String referenceField1) {
        return setValue("sRefFld01", referenceField1);
    }

    public String getReferenceField1() {
        return (String) getValue("sRefFld01");
    }

// sRefFld02
    public JSONObject setReferenceField2(String referenceField2) {
        return setValue("sRefFld02", referenceField2);
    }

    public String getReferenceField2() {
        return (String) getValue("sRefFld02");
    }

// sRefFld03
    public JSONObject setReferenceField3(String referenceField3) {
        return setValue("sRefFld03", referenceField3);
    }

    public String getReferenceField3() {
        return (String) getValue("sRefFld03");
    }

// sRefFld04
    public JSONObject setReferenceField4(String referenceField4) {
        return setValue("sRefFld04", referenceField4);
    }

    public String getReferenceField4() {
        return (String) getValue("sRefFld04");
    }

// sRefFld05
    public JSONObject setReferenceField5(String referenceField5) {
        return setValue("sRefFld05", referenceField5);
    }

    public String getReferenceField5() {
        return (String) getValue("sRefFld05");
    }

// sParentxx
    public JSONObject setParentTable(String parentTable) {
        return setValue("sParentxx", parentTable);
    }

    public String getParentTable() {
        return (String) getValue("sParentxx");
    }


}
