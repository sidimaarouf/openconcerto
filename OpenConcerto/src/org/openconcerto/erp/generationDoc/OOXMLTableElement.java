/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2011 OpenConcerto, by ILM Informatique. All rights reserved.
 * 
 * The contents of this file are subject to the terms of the GNU General Public License Version 3
 * only ("GPL"). You may not use this file except in compliance with the License. You can obtain a
 * copy of the License at http://www.gnu.org/licenses/gpl-3.0.html See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each file.
 */
 
 package org.openconcerto.erp.generationDoc;

import org.openconcerto.sql.Configuration;
import org.openconcerto.sql.element.SQLElement;
import org.openconcerto.sql.model.SQLRow;
import org.openconcerto.sql.model.SQLRowAccessor;
import org.openconcerto.sql.model.SQLTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom.Element;

public class OOXMLTableElement {

    private List<SQLRow> row;
    private int firstLine, endPageLine, endLine, filterId;
    private List<String> listBlankLineStyle;
    private boolean typeStyleWhere;
    private SQLElement elt;
    private String foreignTableWhere, typeWhere, fieldWhere;
    private Element tableau;
    private OOXMLCache cache;

    public OOXMLTableElement(Element tableau, SQLRow row, OOXMLCache cache) {

        this.tableau = tableau;
        this.cache = cache;
        this.foreignTableWhere = tableau.getAttributeValue("tableForeignWhere");
        this.fieldWhere = tableau.getAttributeValue("fieldWhere");

        if (this.fieldWhere != null && row.getTable().contains(fieldWhere)) {
            this.filterId = row.getInt(this.fieldWhere);
        }

        String fieldAttribute = tableau.getAttributeValue("field");
        if (fieldAttribute != null && fieldAttribute.trim().length() > 0) {
            if (fieldAttribute.contains(",")) {
                List<String> l = SQLRow.toList(fieldAttribute);
                this.row = new ArrayList<SQLRow>();
                for (String string : l) {
                    this.row.add(row.getForeignRow(string));
                }
            } else {
                this.row = Arrays.asList(row.getForeignRow(fieldAttribute));
            }
        } else {
            this.row = Arrays.asList(row);
        }

        this.firstLine = Integer.valueOf(tableau.getAttributeValue("firstLine"));

        this.endPageLine = Integer.valueOf(tableau.getAttributeValue("endPageLine"));
        this.endLine = Integer.valueOf(tableau.getAttributeValue("endLine"));

        this.elt = Configuration.getInstance().getDirectory().getElement(tableau.getAttributeValue("table"));

        this.typeWhere = tableau.getAttributeValue("typeWhere");

        String blankLineBeforeStyle = tableau.getAttributeValue("blankLineBeforeStyle");
        this.listBlankLineStyle = new ArrayList<String>();

        if (blankLineBeforeStyle != null) {
            this.listBlankLineStyle = SQLRow.toList(blankLineBeforeStyle.trim());
        }

        this.typeStyleWhere = (this.typeWhere == null) ? false : this.typeWhere.equalsIgnoreCase("Style");

    }

    public List<? extends SQLRowAccessor> getRows() {
        SQLTable tableElt = Configuration.getInstance().getRoot().findTable(this.tableau.getAttributeValue("table"));

        if (tableElt != null) {

            // // #if gestionnx
            // Set<SQLField> fields =
            // tablePourcentService.getForeignKeys(tableElt);
            //
            // if (((ComptaPropsConfiguration)
            // Configuration.getInstance()).customerIsPreventec() && fields !=
            // null && fields.size() > 0) {
            // SQLSelect sel = new
            // SQLSelect(Configuration.getInstance().getBase());
            //
            // sel.addSelectStar(tableElt);
            // Set<SQLField> fieldsElt =
            // tableElt.getForeignKeys(this.row.getTable());
            // Where w = new Where(fieldsElt.iterator().next(), "=",
            // this.row.getTable().getKey());
            // w = w.and(new Where(fields.iterator().next(), "=",
            // tableElt.getKey()));
            // w = w.and(new Where(this.row.getTable().getKey(), "=",
            // this.row.getID()));
            // sel.setWhere(w);
            // sel.addFieldOrder(tablePourcentService.getField("ID_VERIFICATEUR"));
            // sel.addFieldOrder(fields.iterator().next());
            // List<SQLRow> l = (List<SQLRow>)
            // Configuration.getInstance().getBase().getDataSource().execute(sel.asString(),
            // SQLRowListRSH.createFromSelect(sel, tableElt));
            //
            // // Suppression des doublons
            // List<SQLRow> list = new ArrayList<SQLRow>();
            // for (SQLRow sqlRow : l) {
            // if (!list.contains(sqlRow)) {
            // list.add(sqlRow);
            // }
            // }
            // return list;
            // }
            // // #endif

            return cache.getReferentRows(this.row, tableElt, this.tableau.getAttributeValue("groupBy"), this.tableau.getAttributeValue("orderBy"));

        } else {
            System.err.println("OOXMLTableElement.getRows() Table " + tableElt + " is null!");
            return new ArrayList<SQLRow>();
        }
    }

    public int getFirstLine() {
        return this.firstLine;
    }

    public int getFilterId() {
        return this.filterId;
    }

    public int getEndPageLine() {
        return this.endPageLine;
    }

    public int getEndLine() {
        return this.endLine;
    }

    public List<String> getListBlankLineStyle() {
        return this.listBlankLineStyle;
    }

    public SQLElement getSQLElement() {
        return this.elt;
    }

    public String getForeignTableWhere() {
        return this.foreignTableWhere;
    }

    public List<SQLRow> getRow() {
        return this.row;
    }

    public Element getTableau() {
        return this.tableau;
    }

    public String getTypeWhere() {
        return this.typeWhere;
    }

    public boolean getTypeStyleWhere() {
        return this.typeStyleWhere;
    }

    public String getFieldWhere() {
        return this.fieldWhere;
    }
}
