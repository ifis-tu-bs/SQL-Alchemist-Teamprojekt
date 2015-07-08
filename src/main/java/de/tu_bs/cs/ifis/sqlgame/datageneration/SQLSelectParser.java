/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_bs.cs.ifis.sqlgame.datageneration;

import java.util.ArrayList;
import java.util.StringTokenizer;
import sun.util.locale.StringTokenIterator;

/**
 *
 * @author Tobias
 */
public class SQLSelectParser {
    
    String selectStatement;
    
    public SQLSelectParser(String selectStatement) {
        this.selectStatement = selectStatement;
    }
    
    public String getFromInformation() {
        String [] splitFrom = this.selectStatement.split("FROM");
        StringTokenizer st = new StringTokenizer(splitFrom[1].trim());
        
        return st.nextToken();
    }
    
    public ArrayList<ArrayList<String>> getWhereInformation() {
        ArrayList<ArrayList<String>> whereInformation = new ArrayList<>();
        
        if (this.selectStatement.contains("WHERE")) {
            String [] splitWhere = this.selectStatement.split("WHERE");
            String [] splitAnd = splitWhere[1].split("AND");
            for (String whereClause : splitAnd) {
                //New row of where information
                ArrayList<String> whereInformationRow = new ArrayList<>();

                //Split the where clause
                StringTokenizer st = new StringTokenizer(whereClause);

                //First token of the where clause is the column name
                StringTokenizer stt = new StringTokenizer(st.nextToken(), ".");
                String columnName = stt.nextToken();
                if (stt.hasMoreTokens()) {
                    columnName = stt.nextToken();
                }
                whereInformationRow.add(columnName);

                //Second token of the where clause is the where comparison type
                String comparisonType = st.nextToken();
                whereInformationRow.add(comparisonType);

                //Get the token comparison token of the where clause
                CharSequence comparisonString = st.nextToken();
                while (st.hasMoreTokens()) {
                    comparisonString += " " + st.nextToken();
                }
                
                String comparison = "";
                if (comparisonString.charAt(0) == '"') {
                    //varchar
                    for (int i = 1; i < comparisonString.length(); i++) {
                        if (comparisonString.charAt(i) == '"') {
                            break;
                        } else {
                            comparison += comparisonString.charAt(i);
                        }
                    }
                    
                } else if (comparisonString.charAt(0) == '\'') {
                    //varchar
                    for (int i = 1; i < comparisonString.length(); i++) {
                        if (comparisonString.charAt(i) == '\'') {
                            break;
                        } else {
                            comparison += comparisonString.charAt(i);
                        }
                    }
                } else {
                    //int or double
                    StringTokenizer sttt = new StringTokenizer(comparisonString.toString());
                    if (sttt.hasMoreTokens()) {
                        comparison = sttt.nextToken();
                    }
                }
                
                whereInformationRow.add(comparison);

                whereInformation.add(whereInformationRow);
            }
        }
        
        return whereInformation;
    }
}
