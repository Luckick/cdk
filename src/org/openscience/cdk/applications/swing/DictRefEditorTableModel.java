/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.swing;

import org.openscience.cdk.*;
import org.openscience.cdk.dict.*;
import org.openscience.cdk.tools.LoggingTool;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * @cdkPackage applications
 */
public class DictRefEditorTableModel extends AbstractTableModel {
    
    private LoggingTool logger;
    
    private String[] columnNames;
    private Vector fields = new Vector();
    private Vector dicts = new Vector();
    private Vector entries = new Vector();
    
    public DictRefEditorTableModel() {
        logger = new LoggingTool(this.getClass().getName());
        
        columnNames = new String[3];
        columnNames[0] = "Field";
        columnNames[1] = "Dictionary";
        columnNames[2] = "Entry";
        
        insertBlankRow(0);
    }
    
    
    public void setValueAt(Object value, int row, int column) {
        if (((String)value).length() == 0 ) {
            fields.removeElementAt(row);
            dicts.removeElementAt(row);
            entries.removeElementAt(row);
            fireTableRowsDeleted(row, row);
            if (fields.size() < 1) {
                insertBlankRow(row);
            }
        } else if (column == 0) {
            fields.setElementAt(value, row);
        } else if (column == 1) {
            dicts.setElementAt(value, row);
        } else if (column == 2) {
            entries.setElementAt(value, row);
        }

        if (row == fields.size()-1) {
            insertBlankRow(row);
        }
        
        fireTableCellUpdated(row, column);
    }
    
    
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public int getRowCount() {
        return fields.size();
    }
    
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    public Class getColumnClass(int col) {
        return getColumnName(col).getClass();
    }
    
    public Object getValueAt(int row, int col) {
        if ( row >= fields.size() || col >= columnNames.length ) {
            return null;
        }
        
        if (col == 0) {
            return fields.elementAt(row);
        } else if (col == 1) {
            return dicts.elementAt(row);
        } else {
            return entries.elementAt(row);
        }
    }
    
    public boolean isCellEditable(int row, int column) {
        return true;
    }
    
    /**
     * Parses the properties of this ChemObject and looks for (read: start with)
     * 'org.openscience.cdk.dict" entries. Based on such entries, it fills
     * the table with dictionary references.
     *
     */
    public void setChemObject(ChemObject object) {
        cleanTable();
        logger.debug("Filling dict ref table for " + object.toString());
        Map properties = object.getProperties();
        Iterator iter = properties.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            logger.debug("Found property: " + key);
            if (key instanceof String) {
                String keyName = (String)key;
                if (keyName.startsWith(DictionaryDatabase.DICTREFPROPERTYNAME)) {
                    logger.debug("About to add this ref: " + keyName);
                    if (keyName.length() > DictionaryDatabase.DICTREFPROPERTYNAME.length()) {
                        String fieldName = keyName.substring(DictionaryDatabase.DICTREFPROPERTYNAME.length()+1);
                        fields.addElement(fieldName);
                    } else {
                        fields.addElement("unspecified");
                    }
                    String dictRef = (String)properties.get(keyName);
                    int index = dictRef.indexOf(':');
                    if (index != -1) {
                        dicts.addElement(dictRef.substring(0,index));
                        entries.addElement(dictRef.substring(index+1));
                    } else {
                        // The dictRef has no namespace
                        dicts.addElement(dictRef);
                        entries.addElement("");
                    }
                    fireTableDataChanged();
                }
            }
        }
    }
    
    private void cleanTable() {
        fields.clear();
        dicts.clear();
        entries.clear();
        fireTableDataChanged();
        insertBlankRow(0);
    }
    
    private void insertBlankRow(int row) {
        fields.addElement("");
        dicts.addElement("");
        entries.addElement("");
        fireTableRowsInserted(row+1, row+1);
    }
}

