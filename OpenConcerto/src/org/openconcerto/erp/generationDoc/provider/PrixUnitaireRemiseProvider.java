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
 
 package org.openconcerto.erp.generationDoc.provider;

import org.openconcerto.erp.generationDoc.SpreadSheetCellValueContext;
import org.openconcerto.erp.generationDoc.SpreadSheetCellValueProviderManager;
import org.openconcerto.sql.model.SQLRowAccessor;
import org.openconcerto.utils.DecimalUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PrixUnitaireRemiseProvider extends UserInitialsValueProvider {

    @Override
    public Object getValue(SpreadSheetCellValueContext context) {
        SQLRowAccessor row = context.getRow();
        final BigDecimal pv = row.getBigDecimal("PV_HT");
        BigDecimal remise = (BigDecimal) row.getObject("POURCENT_REMISE");
        if (remise == null) {
            remise = BigDecimal.ZERO;
        }
        BigDecimal acompte = BigDecimal.ONE;
        if (row.getTable().contains("POURCENT_ACOMPTE") && row.getObject("POURCENT_ACOMPTE") != null) {
            acompte = ((BigDecimal) row.getObject("POURCENT_ACOMPTE")).movePointLeft(2);
        }
        BigDecimal result = BigDecimal.ONE.subtract(remise.movePointLeft(2)).multiply(pv, DecimalUtils.HIGH_PRECISION).multiply(acompte, DecimalUtils.HIGH_PRECISION);

        return result.setScale(2, RoundingMode.HALF_UP);
    }

    public static void register() {
        SpreadSheetCellValueProviderManager.put("PrixUnitaireRemise", new PrixUnitaireRemiseProvider());
    }
}
