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
 
 package org.openconcerto.erp.core.reports.history.ui;

import org.openconcerto.erp.config.ComptaPropsConfiguration;
import org.openconcerto.erp.preferences.DefaultNXProps;
import org.openconcerto.sql.model.SQLBase;
import org.openconcerto.sql.model.SQLSelect;
import org.openconcerto.sql.model.SQLTable;
import org.openconcerto.sql.model.Where;
import org.openconcerto.utils.GestionDevise;

import java.awt.GridLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.dbutils.handlers.ArrayListHandler;

public class HistoriqueClientBilanPanel extends JPanel {
    private final JLabel labelVentesComptoir = new JLabel();
    private final JLabel labelVentesFacture = new JLabel();
    private final JLabel labelPropositions = new JLabel();
    private final JLabel labelCheques = new JLabel();
    private final JLabel labelEcheances = new JLabel();
    private final JLabel labelRelances = new JLabel();
    private final JLabel labelTotalVente = new JLabel();
    private final JLabel labelTotalVentesArticle = new JLabel();
    private long nbVentesCompoir;
    private long totalVentesCompoir;
    private long nbVentesFacture;
    private long totalVentesFacture;
    private long totalVentesArticle;
    private long nbTotalCheques;
    private long totalCheques;
    private long nbChequesNonEncaisses;
    private long nbRelances;
    private int delaiPaiementMoyen;
    private long nbPropositions;
    private long totalPropositions;
    private long nbFacturesImpayees;
    private long totalFacturesImpayees;
    private int poucentageVentes;

    public HistoriqueClientBilanPanel() {
        super();

        setLayout(new GridLayout(4, 2));

        // Saisie Vente Comptoir --> HT, TTC

        final Boolean bModeVenteComptoir = DefaultNXProps.getInstance().getBooleanValue("ArticleVenteComptoir", true);
        if (bModeVenteComptoir) {
            add(this.labelVentesComptoir);
        }
        // Saisie VF --> HT, TTC
        add(this.labelVentesFacture);
        // Proposition
        add(this.labelPropositions);
        // Cheque
        add(this.labelCheques);
        // Echeances
        add(this.labelEcheances);
        // Relances
        add(this.labelRelances);
        // Total vente
        add(this.labelTotalVente);
        // Toal article vente
        add(this.labelTotalVentesArticle);
    }

    public synchronized void updateRelance(final List<Integer> listId) {
        final int nb = listId.size();
        if (this.nbRelances != nb) {
            setRelances(nb);
            updateLabels();
        }
    }

    public synchronized void updateEcheance(final List<Integer> listId) {

        ComptaPropsConfiguration.getInstanceCompta().getNonInteractiveSQLExecutor().execute(new Runnable() {

            @Override
            public void run() {

                long valueTotal = 0;
                if (listId != null && listId.size() > 0) {
                    final SQLBase base = ((ComptaPropsConfiguration) ComptaPropsConfiguration.getInstance()).getSQLBaseSociete();
                    final SQLSelect select = new SQLSelect();
                    final SQLTable table = base.getTable("ECHEANCE_CLIENT");
                    select.addSelect(table.getField("MONTANT"), "SUM");
                    select.setWhere(new Where(table.getKey(), listId));
                    final Number n = (Number) base.getDBSystemRoot().getDataSource().executeScalar(select.asString());
                    if (n != null) {
                        valueTotal = n.longValue();
                    }
                }
                setNbFacturesImpayees(listId == null ? 0 : listId.size());
                setTotalFacturesImpayees(valueTotal);
                updateLabels();

            }
        });

    }

    public synchronized void updateVFData(final List<Integer> listId, final int idClient) {
        ComptaPropsConfiguration.getInstanceCompta().getNonInteractiveSQLExecutor().execute(new Runnable() {

            @Override
            public void run() {
                final SQLBase base = ((ComptaPropsConfiguration) ComptaPropsConfiguration.getInstance()).getSQLBaseSociete();

                long valueTotal = 0;
                if (listId != null && listId.size() > 0) {
                    final SQLSelect select = new SQLSelect();
                    final SQLTable table = base.getTable("SAISIE_VENTE_FACTURE");
                    select.addSelect(table.getField("T_HT"), "SUM");
                    select.setWhere(new Where(table.getKey(), listId));
                    final Number n = (Number) base.getDBSystemRoot().getDataSource().executeScalar(select.asString());
                    if (n != null) {
                        valueTotal = n.longValue();
                    }
                }

                final Map<Object, Date> mapDateFact = new HashMap<Object, Date>();
                // On recupere les dates de facturations VF
                final SQLSelect selDateFacture = new SQLSelect();
                final SQLTable tableFacture = base.getTable("SAISIE_VENTE_FACTURE");
                final SQLTable tableEncaisse = base.getTable("ENCAISSER_MONTANT");
                final SQLTable tableEcheance = base.getTable("ECHEANCE_CLIENT");
                final SQLTable tableMvt = base.getTable("MOUVEMENT");
                selDateFacture.addSelect(tableFacture.getField("DATE"));
                selDateFacture.addSelect(tableMvt.getField("ID_PIECE"));
                Where w = new Where(tableFacture.getField("ID_MOUVEMENT"), "=", tableMvt.getKey());
                if (idClient > 1) {
                    w = w.and(new Where(tableFacture.getField("ID_CLIENT"), "=", idClient));
                }
                selDateFacture.setWhere(w);

                addDatesToMap(base, selDateFacture, mapDateFact);

                // On recupere les dates de facturations
                final SQLSelect selDateFactureC = new SQLSelect();
                final SQLTable tableComptoir = base.getTable("SAISIE_VENTE_COMPTOIR");
                selDateFactureC.addSelect(tableComptoir.getField("DATE"));
                selDateFactureC.addSelect(tableMvt.getField("ID_PIECE"));
                Where wC = new Where(tableComptoir.getField("ID_MOUVEMENT"), "=", tableMvt.getKey());
                if (idClient > 1) {
                    wC = wC.and(new Where(tableComptoir.getField("ID_CLIENT"), "=", idClient));
                }
                selDateFactureC.setWhere(wC);
                addDatesToMap(base, selDateFactureC, mapDateFact);

                // On recupere les dates d'encaissement
                final SQLSelect selDateEncaisse = new SQLSelect();
                selDateEncaisse.addSelect(tableEncaisse.getField("DATE"));
                selDateEncaisse.addSelect(tableMvt.getField("ID_PIECE"));
                selDateEncaisse.addSelect(tableEcheance.getField("ID"));
                Where wEncaisse = new Where(tableEcheance.getField("ID"), "=", tableEncaisse.getField("ID_ECHEANCE_CLIENT"));
                wEncaisse = wEncaisse.and(new Where(tableEcheance.getField("ID_MOUVEMENT"), "=", tableMvt.getField("ID")));
                wEncaisse = wEncaisse.and(new Where(tableEcheance.getArchiveField(), "=", 1));

                if (idClient > 1) {
                    wEncaisse = wEncaisse.and(new Where(tableEcheance.getField("ID_CLIENT"), "=", idClient));
                }

                selDateEncaisse.setWhere(wEncaisse);
                selDateEncaisse.setArchivedPolicy(SQLSelect.BOTH);

                final List<Object[]> lDateEncaisse = (List<Object[]>) base.getDataSource().execute(selDateEncaisse.asString(), new ArrayListHandler());
                final Map<Object, Date> mapDateEncaisse = new HashMap<Object, Date>();
                for (int i = 0; i < lDateEncaisse.size(); i++) {
                    final Object[] tmp = lDateEncaisse.get(i);
                    final Date d2 = (Date) tmp[0];
                    final Object d = mapDateEncaisse.get(tmp[1]);
                    if (d != null) {
                        final Date d1 = (Date) d;
                        if (d1.before(d2)) {
                            mapDateEncaisse.put(tmp[1], d2);
                        }
                    } else {
                        mapDateEncaisse.put(tmp[1], d2);
                    }
                }

                // Calcul moyenne
                int cpt = 0;
                int day = 0;
                final Calendar cal1 = Calendar.getInstance();
                final Calendar cal2 = Calendar.getInstance();
                for (final Iterator i = mapDateFact.keySet().iterator(); i.hasNext();) {
                    final Object key = i.next();
                    final Date dFact = mapDateFact.get(key);
                    final Date dEncaisse = mapDateEncaisse.get(key);

                    if (dFact != null && dEncaisse != null) {
                        cpt++;
                        cal1.setTime(dFact);
                        cal2.setTime(dEncaisse);
                        cal1.set(Calendar.HOUR, 0);
                        cal1.set(Calendar.MINUTE, 0);
                        cal1.set(Calendar.SECOND, 0);
                        cal1.set(Calendar.MILLISECOND, 0);
                        cal2.set(Calendar.HOUR, 0);
                        cal2.set(Calendar.MINUTE, 0);
                        cal2.set(Calendar.SECOND, 0);
                        cal2.set(Calendar.MILLISECOND, 0);
                        day += (cal2.getTime().getTime() - cal1.getTime().getTime()) / 86400000;
                    }
                }

                setPoucentageVentes(cpt == 0 ? 0 : day / cpt);
                setTotalVentesFacture(valueTotal);
                setNbVentesFacture(listId == null ? 0 : listId.size());
                updateLabels();
            }
        });
    }

    public synchronized void updateVFArticleData(final List<Integer> listId, final int idClient) {
        ComptaPropsConfiguration.getInstanceCompta().getNonInteractiveSQLExecutor().execute(new Runnable() {

            @Override
            public void run() {
                final SQLBase base = ((ComptaPropsConfiguration) ComptaPropsConfiguration.getInstance()).getSQLBaseSociete();

                double valueTotal = 0;
                if (listId != null && listId.size() > 0) {
                    final SQLSelect select = new SQLSelect();
                    final SQLTable tableElt = base.getTable("SAISIE_VENTE_FACTURE_ELEMENT");
                    select.addSelect(tableElt.getField("T_PV_HT"), "SUM");
                    select.setWhere(new Where(tableElt.getKey(), listId));
                    final Number n = (Number) base.getDBSystemRoot().getDataSource().executeScalar(select.asString());
                    if (n != null) {
                        valueTotal = n.doubleValue();
                    }
                }

                setTotalVentesArticle(Math.round(valueTotal * 100.0D));
                updateLabels();
            }
        });
    }

    private void addDatesToMap(final SQLBase base, final SQLSelect selDateFacture, final Map mapDateFact) {
        final List<Object[]> lDateFact = (List<Object[]>) base.getDataSource().execute(selDateFacture.asString(), new ArrayListHandler());

        final int size = lDateFact.size();
        for (int i = 0; i < size; i++) {
            final Object[] tmp = lDateFact.get(i);
            mapDateFact.put(tmp[1], tmp[0]);
        }
    }

    public synchronized void updateVCData(final List<Integer> listId) {
        ComptaPropsConfiguration.getInstanceCompta().getNonInteractiveSQLExecutor().execute(new Runnable() {

            @Override
            public void run() {
                final SQLBase base = ((ComptaPropsConfiguration) ComptaPropsConfiguration.getInstance()).getSQLBaseSociete();

                long valueTotal = 0;

                if (listId != null && listId.size() > 0) {
                    final SQLSelect select = new SQLSelect();
                    final SQLTable table = base.getTable("SAISIE_VENTE_COMPTOIR");
                    select.addSelect(table.getField("MONTANT_HT"), "SUM");
                    select.setWhere(new Where(table.getKey(), listId));
                    final Number n = (Number) base.getDBSystemRoot().getDataSource().executeScalar(select.asString());
                    if (n != null) {
                        valueTotal = n.longValue();
                    }
                }

                setNbVentesComptoir(listId == null ? 0 : listId.size());
                setTotalVentesComptoir(valueTotal);
                updateLabels();
            }
        });
    }


    public synchronized void updateChequeData(final List<Integer> listId) {
        ComptaPropsConfiguration.getInstanceCompta().getNonInteractiveSQLExecutor().execute(new Runnable() {

            @Override
            public void run() {
                final SQLBase base = ((ComptaPropsConfiguration) ComptaPropsConfiguration.getInstance()).getSQLBaseSociete();
                final SQLTable table = base.getTable("CHEQUE_A_ENCAISSER");
                long valueTotalTmp = 0;
                long valueNonEncaisseTmp = 0;

                if (listId != null && listId.size() > 0) {
                    // Total
                    final SQLSelect select = new SQLSelect();
                    select.addSelect(table.getField("MONTANT"), "SUM");
                    select.setWhere(new Where(table.getKey(), listId));
                    Number n = (Number) base.getDBSystemRoot().getDataSource().executeScalar(select.asString());
                    if (n != null) {
                        valueTotalTmp = n.longValue();
                    }
                    // Total non encaissé
                    select.setWhere(new Where(table.getKey(), listId).and(new Where(table.getField("ENCAISSE"), "=", Boolean.FALSE)));
                    n = (Number) base.getDBSystemRoot().getDataSource().executeScalar(select.asString());
                    if (n != null) {
                        valueNonEncaisseTmp = n.longValue();
                    }
                }

                setNbTotalCheques(listId == null ? 0 : listId.size());
                setNbChequesNonEncaisses(valueNonEncaisseTmp);
                setTotalCheques(valueTotalTmp);
                updateLabels();
            }
        });
    }

    // Ventes comptoir
    public void setNbVentesComptoir(final long nb) {
        this.nbVentesCompoir = nb;
    }

    public void setTotalVentesComptoir(final long totalInCents) {
        this.totalVentesCompoir = totalInCents;
    }

    // Ventes avec facture
    public void setNbVentesFacture(final long nb) {
        this.nbVentesFacture = nb;
    }

    public void setTotalVentesFacture(final long totalInCents) {
        this.totalVentesFacture = totalInCents;
    }

    public void setTotalVentesArticle(long totalVentesArticle) {
        this.totalVentesArticle = totalVentesArticle;
    }

    // Cheques
    public void setNbTotalCheques(final long nb) {
        this.nbTotalCheques = nb;
    }

    public void setTotalCheques(final long totalInCents) {
        this.totalCheques = totalInCents;
    }

    public void setNbChequesNonEncaisses(final long nb) {
        this.nbChequesNonEncaisses = nb;
    }

    // Relances
    public void setRelances(final long nb) {
        this.nbRelances = nb;
    }

    public void setDelaiPaiementMoyen(final int nb) {
        this.delaiPaiementMoyen = nb;
    }

    // Propositions
    public void setNbPropositions(final long nb) {
        this.nbPropositions = nb;
    }

    public void setTotalPropositions(final long totalInCents) {
        this.totalPropositions = totalInCents;
    }

    // Facture impayées
    public void setNbFacturesImpayees(final long nb) {
        this.nbFacturesImpayees = nb;
    }

    public void setTotalFacturesImpayees(final long totalInCents) {
        this.totalFacturesImpayees = totalInCents;
    }

    // Pourcentage des vente
    public void setPoucentageVentes(final int pourCent) {
        this.poucentageVentes = pourCent;
    }

    private void updateLabels() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                String total;
                long nb;
                // Ventes comptoir
                total = GestionDevise.currencyToString(HistoriqueClientBilanPanel.this.totalVentesCompoir, true);
                nb = HistoriqueClientBilanPanel.this.nbVentesCompoir;
                if (nb == 0) {
                    HistoriqueClientBilanPanel.this.labelVentesComptoir.setText(" pas de vente comptoir");
                } else if (nb == 1) {
                    HistoriqueClientBilanPanel.this.labelVentesComptoir.setText(" une vente comptoir d'un montant de " + total + " € HT");
                } else {
                    HistoriqueClientBilanPanel.this.labelVentesComptoir.setText(" " + nb + " ventes comptoir d'un montant total de " + total + " € HT");
                }
                // Ventes facture
                total = GestionDevise.currencyToString(HistoriqueClientBilanPanel.this.totalVentesFacture, true);
                nb = HistoriqueClientBilanPanel.this.nbVentesFacture;
                if (nb == 0) {
                    HistoriqueClientBilanPanel.this.labelVentesFacture.setText(" pas de vente avec facture");
                } else if (nb == 1) {
                    HistoriqueClientBilanPanel.this.labelVentesFacture.setText(" une vente avec facture d'un montant de " + total + " € HT");
                } else {
                    HistoriqueClientBilanPanel.this.labelVentesFacture.setText(" " + nb + " ventes avec facture d'un montant total de " + total + " € HT");
                }
                // Propositions
                total = GestionDevise.currencyToString(HistoriqueClientBilanPanel.this.totalPropositions, true);
                nb = HistoriqueClientBilanPanel.this.nbPropositions;
                if (nb == 0) {
                    HistoriqueClientBilanPanel.this.labelPropositions.setText(" pas de proposition commerciale");
                } else if (nb == 1) {
                    HistoriqueClientBilanPanel.this.labelPropositions.setText(" une proposition commerciale d'un montant de " + total + " € HT");
                } else {
                    HistoriqueClientBilanPanel.this.labelPropositions.setText(" " + nb + " propositions commerciales d'un montant total de " + total + " € HT");
                }
                // Chèques
                nb = HistoriqueClientBilanPanel.this.nbTotalCheques;
                total = GestionDevise.currencyToString(HistoriqueClientBilanPanel.this.totalCheques, true);
                if (nb == 0) {
                    HistoriqueClientBilanPanel.this.labelCheques.setText(" pas de chèque");
                } else if (nb == 1) {
                    if (HistoriqueClientBilanPanel.this.nbChequesNonEncaisses == 0) {
                        HistoriqueClientBilanPanel.this.labelCheques.setText(" un chèque d'un montant de " + total + " € HT");
                    } else {
                        HistoriqueClientBilanPanel.this.labelCheques.setText(" un chèque non encaissé d'un montant de " + total + " € HT");
                    }
                } else {
                    if (HistoriqueClientBilanPanel.this.nbChequesNonEncaisses == 0) {
                        HistoriqueClientBilanPanel.this.labelCheques.setText(" " + nb + " chèques d'un montant total de " + total + " € HT");
                    } else if (HistoriqueClientBilanPanel.this.nbChequesNonEncaisses == nb) {
                        HistoriqueClientBilanPanel.this.labelCheques.setText(" " + nb + " chèques non encaissés d'un montant total de " + total + " € HT");
                    } else {
                        HistoriqueClientBilanPanel.this.labelCheques.setText(" " + nb + " chèques non d'un montant total de " + total + " € HT dont "
                                + HistoriqueClientBilanPanel.this.nbChequesNonEncaisses + " non encaissés");
                    }
                }
                // Factures impayées
                nb = HistoriqueClientBilanPanel.this.nbFacturesImpayees;
                total = GestionDevise.currencyToString(HistoriqueClientBilanPanel.this.totalFacturesImpayees, true);
                if (nb == 0) {
                    HistoriqueClientBilanPanel.this.labelEcheances.setText(" pas de facture impayée");
                } else if (nb == 1) {
                    HistoriqueClientBilanPanel.this.labelEcheances.setText(" une facture impayée d'un montant de " + total + " € HT");
                } else {
                    HistoriqueClientBilanPanel.this.labelEcheances.setText(" " + nb + " factures impayées d'un montant total de " + total + " € HT");
                }
                // Relances
                nb = HistoriqueClientBilanPanel.this.nbRelances;
                String txt;
                if (nb == 0) {
                    txt = " pas de relance effectuée";
                } else if (nb == 1) {
                    txt = " une relance effectuée";
                } else {
                    txt = " " + nb + " relances effectuées";
                }
                if (nb > 0) {
                    if (HistoriqueClientBilanPanel.this.delaiPaiementMoyen == 1) {
                        txt += ", délai moyen de paiment d'une journée";
                    } else if (HistoriqueClientBilanPanel.this.delaiPaiementMoyen > 1) {
                        txt += ", délai moyen de paiment de " + HistoriqueClientBilanPanel.this.delaiPaiementMoyen + " jours";
                    }
                }
                HistoriqueClientBilanPanel.this.labelRelances.setText(txt);
                // % des ventes
                final long cents = HistoriqueClientBilanPanel.this.totalVentesCompoir + HistoriqueClientBilanPanel.this.totalVentesFacture;
                total = GestionDevise.currencyToString(cents, true);
                if (cents == 0) {
                    HistoriqueClientBilanPanel.this.labelTotalVente.setText(" pas de vente");
                } else if (HistoriqueClientBilanPanel.this.poucentageVentes <= 0) {
                    HistoriqueClientBilanPanel.this.labelTotalVente.setText(" ventes de " + total + " € HT");
                } else {
                    HistoriqueClientBilanPanel.this.labelTotalVente.setText(" ventes de " + total + " € HT, soit " + HistoriqueClientBilanPanel.this.poucentageVentes + "% des ventes totales");
                }

                // % des ventes

                total = GestionDevise.currencyToString(HistoriqueClientBilanPanel.this.totalVentesArticle, true);
                if (HistoriqueClientBilanPanel.this.totalVentesArticle == 0) {
                    HistoriqueClientBilanPanel.this.labelTotalVentesArticle.setText(" Aucun article vendu");
                } else {
                    HistoriqueClientBilanPanel.this.labelTotalVentesArticle.setText(total + "€ HT d'articles facturés");
                }
            }
        });

    }
}
