/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.guanzon.cas.tbjhandler.previous;

import java.sql.SQLException;
import java.util.List;
import org.guanzon.appdriver.base.GRiderCAS;
import org.json.simple.JSONObject;
import org.guanzon.cas.tbjhandler.TBJEntry;

/**
 *
 * @author Administrator
 */
public interface TBJHandler {
    public void setGRiderCAS(GRiderCAS grider);
    public void setData(JSONObject json);
    public JSONObject processRequest() throws SQLException;
    public List<TBJEntry> getJournalEntries();
}
