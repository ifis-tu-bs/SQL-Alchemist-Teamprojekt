/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_bs.cs.ifis.sqlgame.datageneration;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Tobias
 */
public class SQLSelectParser {
    
    String selectStatement;
    
    public SQLSelectParser(String selectStatement) {
        this.selectStatement = selectStatement.toLowerCase();
    }
    
    public ArrayList<ArrayList<String>> getWhereInformation() {
        ArrayList<ArrayList<String>> whereInformation = new ArrayList<>();
        
        StringTokenizer st = new StringTokenizer(this.selectStatement, "where");
        st.nextToken();
        String whereStatement = st.nextToken();
        
        st = new StringTokenizer(whereStatement, "and");
        while (st.hasMoreTokens()) {
            //New row of where information
            ArrayList<String> whereInformationRow = new ArrayList<>();
            
            //Split the where clause
            StringTokenizer stt = new StringTokenizer(st.nextToken());
            
            //First token of the where clause is the column name
            StringTokenizer sttt = new StringTokenizer(stt.nextToken(), ".");
            String columnName = sttt.nextToken();
            if (sttt.hasMoreTokens()) {
                columnName = sttt.nextToken();
            }
            whereInformationRow.add(columnName);
            
            //Second token of the where clause is the where comparison type
            String comparisonType = stt.nextToken();
            whereInformationRow.add(comparisonType);
            
            //Third token of the where clause is the comparsion String or number
            String comparison = stt.nextToken();
            whereInformationRow.add(comparison);
            
            whereInformation.add(whereInformationRow);
        }
        
        return whereInformation;
    }
}
