/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.tbjhandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.SQLUtil;

/**
 *
 * @author Administrator
 */
public class TBJUtil{
    public static String getAccount(GRiderCAS grider, String industry, String glcode) throws SQLException{
        String lsSQL = "SELECT sAcctCode" + 
                      " FROM Account_Chart" + 
                      " WHERE sGLCodexx = " + SQLUtil.toSQL(glcode) + 
                        " AND sIndstCde = " + SQLUtil.toSQL(industry);
        ResultSet loRS = grider.executeQuery(lsSQL);
        if(loRS.next()){
            return loRS.getString("sAcctCode");
        }
        return "";
    }
    
    public static ArrayList<String> extractFormula(String formula){
        // Regular expression to match words (variables) only, not numbers or operators
        Pattern pattern = Pattern.compile("\\b[a-zA-Z_]\\w*\\b");
        Matcher matcher = pattern.matcher(formula);

        // Use a Set to store unique variable names
        ArrayList<String> variables = new ArrayList<>();

        while (matcher.find()) {
            if (!variables.contains(matcher.group())) { // Avoid duplicates
                variables.add(matcher.group());
            }
        }
        
        return variables;
    }
    
    public static Map<String, Object> Row2HashMap(ResultSet rs) throws SQLException{
        Map<String, Object> row = new HashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            row.put(meta.getColumnName(i), rs.getObject(i));
        }    
        
        return row;
    }
}

