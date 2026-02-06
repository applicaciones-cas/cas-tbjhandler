package org.guanzon.cas.tbjhandler.Services;

import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.cas.tbjhandler.Model.Model_TBJ_Detail;
import org.guanzon.cas.tbjhandler.Model.Model_TBJ_Master;
import org.guanzon.cas.tbjhandler.Model.Model_xxxSysColumn;
import org.guanzon.cas.tbjhandler.Model.Model_xxxSysTable;

/**
 * TBJModels ---------------------------------------------------------------
 * Service class for managing TBJ-related models. This class provides
 * access to the models for TBJ Master, TBJ Detail, System Table, and System Column.
 *
 * <p>
 * TBJDetail and TBJMaster handle data models for the Transaction Book Journal
 * including initialization, XML mapping, and database table binding.
 * </p>
 *
 * <p>
 * SysColumn and SysTable handle system-level table and column models, providing
 * access to computable columns and TBJ-related metadata.
 * </p>
 *
 * @author Teejei De Celis
 * @since 2026-02-05
 * @startDate 2026-02-05
 * @endDate   2026-02-06
 */
public class TBJModels {

    private GRiderCAS poGRider;
    private Model_TBJ_Master poTBJMaster;
    private Model_TBJ_Detail poTBJDetail;
    private Model_xxxSysColumn poSysColumn;
    private Model_xxxSysTable poSysTable;

    /**
     * Constructor for TBJModels.
     *
     * @param applicationDriver the GRiderCAS application driver
     */
    public TBJModels(GRiderCAS applicationDriver){
        poGRider = applicationDriver;
    }

    /**
     * Returns the TBJDetail model. Initializes it if not already created.
     *
     * @return Model_TBJ_Detail instance
     */
    public Model_TBJ_Detail TBJDetail(){
        if (poGRider == null){
            System.err.println("TBJModels.TBJDetail : Application driver is not set.");
            return null;
        }

        if (poTBJDetail == null){
            poTBJDetail = new Model_TBJ_Detail();
            poTBJDetail.setApplicationDriver(poGRider);
            poTBJDetail.setXML("Model_TBJ_Detail");
            poTBJDetail.setTableName("TBJ_Detail");
            poTBJDetail.initialize();
        }

        return poTBJDetail;
    }

    /**
     * Returns the TBJMaster model. Initializes it if not already created.
     *
     * @return Model_TBJ_Master instance
     */
    public Model_TBJ_Master TBJMaster(){
        if (poGRider == null){
            System.err.println("TBJModels.TBJMaster :  Application driver is not set.");
            return null;
        }

        if (poTBJMaster == null){
            poTBJMaster = new Model_TBJ_Master();
            poTBJMaster.setApplicationDriver(poGRider);
            poTBJMaster.setXML("Model_TBJ_Master");
            poTBJMaster.setTableName("TBJ_Master");
            poTBJMaster.initialize();
        }

        return poTBJMaster;
    }

    /**
     * Returns the SysColumn model. Initializes it if not already created.
     *
     * @return Model_xxxSysColumn instance
     */
    public Model_xxxSysColumn SysColumn() {
        if (poGRider == null) {
            System.err.println("TBJModels.SysColumn: Application driver is not set.");
            return null;
        }

        if (poSysColumn == null) {
            poSysColumn = new Model_xxxSysColumn();
            poSysColumn.setApplicationDriver(poGRider);
            poSysColumn.setXML("Model_xxxSysColumn");
            poSysColumn.setTableName("xxxSysColumn");
            poSysColumn.initialize();
        }

        return poSysColumn;
    }

    /**
     * Returns the SysTable model. Initializes it if not already created.
     *
     * @return Model_xxxSysTable instance
     */
    public Model_xxxSysTable SysTable() {
        if (poGRider == null) {
            System.err.println("TBJModels.SysTable: Application driver is not set.");
            return null;
        }

        if (poSysTable == null) {
            poSysTable = new Model_xxxSysTable();
            poSysTable.setApplicationDriver(poGRider);
            poSysTable.setXML("Model_xxxSysTable");
            poSysTable.setTableName("xxxSysTable");
            poSysTable.initialize();
        }

        return poSysTable;
    }

    /**
     * Releases all models and resources for garbage collection.
     *
     * @throws Throwable if finalization fails
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            poTBJMaster = null;
            poTBJDetail = null;
            poSysColumn = null;
            poSysTable = null;
            poGRider = null;
        } finally {
            super.finalize();
        }
    }
}
