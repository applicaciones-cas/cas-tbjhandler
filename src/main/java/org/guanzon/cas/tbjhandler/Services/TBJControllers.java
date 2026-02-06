package org.guanzon.cas.tbjhandler.Services;

import java.sql.SQLException;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.cas.tbjhandler.SysColumn;
import org.guanzon.cas.tbjhandler.SysTable;
import org.guanzon.cas.tbjhandler.TBJParameter;

/**
 * TBJControllers -----------------------------------------------------------
 * Service class for managing TBJ-related controllers. This class provides
 * access to the TBJParameter, SysTable, and SysColumn services, ensuring
 * proper initialization and configuration of each controller.
 *
 * <p>
 * TBJParameter can handle:
 * - Transaction initialization
 * - Detail record management
 * - Validation of entries
 * - Saving transactions
 * - Searching and lookup operations
 * - Confirming transactions
 * </p>
 *
 * <p>
 * SysTable and SysColumn handle system table and column operations for TBJ
 * detail entries, including search and selection of computable columns.
 * </p>
 *
 * @author Teejei De Celis
 * @since 2026-02-05
 * @startDate 2026-02-05
 * @endDate   2026-02-06
 */
public class TBJControllers {

    private GRiderCAS poGRider;
    private LogWrapper poLogWrapper;
    private TBJParameter poTBJParameter;
    private SysTable poSysTable;
    private SysColumn poSysColumn;

    /**
     * Constructor for TBJControllers.
     *
     * @param applicationDriver the GRiderCAS application driver
     * @param logWrapper the LogWrapper instance for logging
     */
    public TBJControllers(GRiderCAS applicationDriver, LogWrapper logWrapper) {
        poGRider = applicationDriver;
        poLogWrapper = logWrapper;
    }

    /**
     * Returns the TBJParameter controller. Initializes it if not already created.
     *
     * @return TBJParameter instance
     * @throws SQLException if database error occurs
     * @throws GuanzonException if initialization fails
     */
    public TBJParameter TBJParameter() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("TBJControllers.TBJParameter: Application driver is not set.");
            return null;
        }

        if (poTBJParameter != null) {
            return poTBJParameter;
        }

        poTBJParameter = new TBJParameter();
        poTBJParameter.setApplicationDriver(poGRider);
        poTBJParameter.setBranchCode(poGRider.getBranchCode());
        poTBJParameter.setVerifyEntryNo(true);
        poTBJParameter.setWithParent(false);
        poTBJParameter.setLogWrapper(poLogWrapper);
        return poTBJParameter;
    }

    /**
     * Returns the SysTable controller. Initializes it if not already created.
     *
     * @return SysTable instance
     * @throws SQLException if database error occurs
     * @throws GuanzonException if initialization fails
     */
    public SysTable SysTable() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("TBJControllers.SysTable: Application driver is not set.");
            return null;
        }

        if (poSysTable != null) {
            return poSysTable;
        }

        poSysTable = new SysTable();
        poSysTable.setApplicationDriver(poGRider);
        poSysTable.setWithParentClass(false);
        poSysTable.setLogWrapper(poLogWrapper);
        poSysTable.initialize();
        // poSysTable.newRecord() intentionally removed to prevent clearing loaded data
        return poSysTable;
    }

    /**
     * Returns the SysColumn controller. Initializes it if not already created.
     *
     * @return SysColumn instance
     * @throws SQLException if database error occurs
     * @throws GuanzonException if initialization fails
     */
    public SysColumn SysColumn() throws SQLException, GuanzonException {
        if (poGRider == null) {
            poLogWrapper.severe("TBJControllers.SysColumn: Application driver is not set.");
            return null;
        }

        if (poSysColumn != null) {
            return poSysColumn;
        }

        poSysColumn = new SysColumn();
        poSysColumn.setApplicationDriver(poGRider);
        poSysColumn.setWithParentClass(false);
        poSysColumn.setLogWrapper(poLogWrapper);
        poSysColumn.initialize();
        poSysColumn.newRecord();
        return poSysColumn;
    }

    /**
     * Releases all controllers and resources for garbage collection.
     *
     * @throws Throwable if finalization fails
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            poTBJParameter = null;
            poSysTable = null;
            poLogWrapper = null;
            poSysColumn = null;
            poGRider = null;
        } finally {
            super.finalize();
        }
    }
}
