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
 
 package org.openconcerto.erp.core.sales.quote.component;

import static org.openconcerto.utils.CollectionUtils.createSet;
import org.openconcerto.erp.config.ComptaPropsConfiguration;
import org.openconcerto.erp.core.common.element.ComptaSQLConfElement;
import org.openconcerto.erp.core.common.element.NumerotationAutoSQLElement;
import org.openconcerto.erp.core.common.ui.AbstractArticleItemTable;
import org.openconcerto.erp.core.common.ui.AbstractVenteArticleItemTable.TypeCalcul;
import org.openconcerto.erp.core.common.ui.Acompte;
import org.openconcerto.erp.core.common.ui.AcompteField;
import org.openconcerto.erp.core.common.ui.AcompteRowItemView;
import org.openconcerto.erp.core.common.ui.DeviseField;
import org.openconcerto.erp.core.common.ui.TotalPanel;
import org.openconcerto.erp.core.sales.quote.element.DevisSQLElement;
import org.openconcerto.erp.core.sales.quote.element.EtatDevisSQLElement;
import org.openconcerto.erp.core.sales.quote.report.DevisXmlSheet;
import org.openconcerto.erp.core.sales.quote.ui.DevisItemTable;
import org.openconcerto.erp.panel.PanelOOSQLComponent;
import org.openconcerto.map.ui.ITextComboVilleViewer;
import org.openconcerto.sql.Configuration;
import org.openconcerto.sql.element.BaseSQLComponent;
import org.openconcerto.sql.element.SQLElement;
import org.openconcerto.sql.model.SQLBackgroundTableCache;
import org.openconcerto.sql.model.SQLRow;
import org.openconcerto.sql.model.SQLRowAccessor;
import org.openconcerto.sql.model.SQLRowValues;
import org.openconcerto.sql.model.SQLSelect;
import org.openconcerto.sql.model.SQLTable;
import org.openconcerto.sql.model.UndefinedRowValuesCache;
import org.openconcerto.sql.model.Where;
import org.openconcerto.sql.sqlobject.ElementComboBox;
import org.openconcerto.sql.sqlobject.JUniqueTextField;
import org.openconcerto.sql.sqlobject.SQLTextCombo;
import org.openconcerto.sql.ui.RadioButtons;
import org.openconcerto.sql.users.UserManager;
import org.openconcerto.sql.view.EditFrame;
import org.openconcerto.ui.DefaultGridBagConstraints;
import org.openconcerto.ui.FormLayouter;
import org.openconcerto.ui.JDate;
import org.openconcerto.ui.TitledSeparator;
import org.openconcerto.ui.VFlowLayout;
import org.openconcerto.ui.component.ITextArea;
import org.openconcerto.utils.ExceptionHandler;
import org.openconcerto.utils.GestionDevise;
import org.openconcerto.utils.SwingWorker2;
import org.openconcerto.utils.cc.ITransformer;
import org.openconcerto.utils.checks.ValidState;
import org.openconcerto.utils.text.SimpleDocumentListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class DevisSQLComponent extends BaseSQLComponent {
    private AbstractArticleItemTable table;
    private JUniqueTextField numeroUniqueDevis;
    private final SQLTable tableNum = getTable().getBase().getTable("NUMEROTATION_AUTO");
    private final ITextArea infos = new ITextArea();
    private final RadioButtons radioEtat = new RadioButtons("NOM");
    private JTextField textPourcentRemise, textPoidsTotal;
    private DeviseField textRemiseHT;
    private DeviseField fieldHT;
    private PanelOOSQLComponent panelOO;

    // Site d'intervention
    final JTextField telSite = new JTextField(20);
    final ITextComboVilleViewer villeSite = new ITextComboVilleViewer();
    final JTextField faxSite = new JTextField(20);
    final JTextField telPSite = new JTextField(20);
    final JTextField mailSite = new JTextField(20);
    final JTextField contactSite = new JTextField(20);
    final JTextField desSite = new JTextField(20);
    final ITextArea adrSite = new ITextArea();

    // Donneur d'ordre
    final JTextField telDonneur = new JTextField(20);
    final JTextField sirenDonneur = new JTextField(20);
    final ITextComboVilleViewer villeDonneur = new ITextComboVilleViewer();
    final JTextField faxDonneur = new JTextField(20);
    final JTextField telPDonneur = new JTextField(20);
    final JTextField mailDonneur = new JTextField(20);
    final JTextField contactDonneur = new JTextField(20);
    final JTextField desDonneur = new JTextField(20);
    final ITextArea adrDonneur = new ITextArea();

    public DevisSQLComponent(final SQLElement elt) {
        super(elt);
    }

    public AbstractArticleItemTable getRowValuesTable() {
        return this.table;
    }

    @Override
    public Set<String> getPartialResetNames() {
        Set<String> s = new HashSet<String>();
        s.add("OBJET");
        s.add("NUMERO");
        return s;
    }

    @Override
    public void addViews() {
        setLayout(new GridBagLayout());
        final GridBagConstraints c = new DefaultGridBagConstraints();

        // Champ Module
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = GridBagConstraints.REMAINDER;
        final JPanel addP = ComptaSQLConfElement.createAdditionalPanel();
        this.setAdditionalFieldsPanel(new FormLayouter(addP, 2));
        this.add(addP, c);

        c.gridy++;
        c.gridwidth = 1;
        final JLabel labelNumero = new JLabel(getLabelFor("NUMERO"));
        labelNumero.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(labelNumero, c);

        // Ligne 1: Numero du devis

        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
            this.numeroUniqueDevis = new JUniqueTextField(15);

        DefaultGridBagConstraints.lockMinimumSize(this.numeroUniqueDevis);
        DefaultGridBagConstraints.lockMaximumSize(this.numeroUniqueDevis);
        this.add(this.numeroUniqueDevis, c);

        // Date

        c.gridx++;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        final JLabel labelDate = new JLabel(getLabelFor("DATE"));

        labelDate.setHorizontalAlignment(SwingConstants.RIGHT);

        this.add(labelDate, c);
        c.gridx++;

        c.weightx = 1;

        c.fill = GridBagConstraints.NONE;
        final JDate dateDevis = new JDate(true);
        this.add(dateDevis, c);

        // Etat devis
        this.radioEtat.setLayout(new VFlowLayout());
        this.radioEtat.setBorder(BorderFactory.createTitledBorder(getLabelFor("ID_ETAT_DEVIS")));
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 5;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.gridx += 2;
        this.radioEtat.initLocalization(new ITransformer<String, String>() {
            @Override
            public String transformChecked(String id) {
                return id;
                // return TranslationManager.getInstance().getTranslationForItem(id);
            }
        });
        this.add(this.radioEtat, c);
        // this.radioEtat.setVisible(false);

        // Ligne 2: Reference
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.weightx = 0;

        final JLabel labelObjet = new JLabel(getLabelFor("OBJET"));
        labelObjet.setHorizontalAlignment(SwingConstants.RIGHT);

        this.add(labelObjet, c);

        final SQLTextCombo textObjet = new SQLTextCombo();
        c.gridx++;
        c.weightx = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(textObjet, c);

        String field;
            field = "ID_COMMERCIAL";
        // Commercial
        final JLabel labelCommercial = new JLabel(getLabelFor(field));
        labelCommercial.setHorizontalAlignment(SwingConstants.RIGHT);
        c.gridx++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        this.add(labelCommercial, c);

        final ElementComboBox comboCommercial = new ElementComboBox(false, 25);

        comboCommercial.setListIconVisible(false);
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;

        this.add(comboCommercial, c);
        addRequiredSQLObject(comboCommercial, field);

        // Ligne 3: Client
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        final JLabel labelClient = new JLabel(getLabelFor("ID_CLIENT"));
        labelClient.setHorizontalAlignment(SwingConstants.RIGHT);
        c.weightx = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(labelClient, c);

        final ElementComboBox comboClient = new ElementComboBox();
        c.gridx++;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        this.add(comboClient, c);
        addRequiredSQLObject(comboClient, "ID_CLIENT");

        final ElementComboBox boxTarif = new ElementComboBox();
            if (getTable().contains("ID_CONTACT")) {
                // Contact Client
                c.gridx = 0;
                c.gridy++;
                c.gridwidth = 1;
                final JLabel labelContact = new JLabel(getLabelFor("ID_CONTACT"));
                labelContact.setHorizontalAlignment(SwingConstants.RIGHT);
                c.weightx = 0;
                c.gridwidth = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                this.add(labelContact, c);

                final ElementComboBox comboContact = new ElementComboBox();
                c.gridx++;
                c.gridwidth = 1;
                c.weightx = 0;
                c.weighty = 0;
                c.fill = GridBagConstraints.NONE;
                this.add(comboContact, c);
                final SQLElement contactElement = getElement().getForeignElement("ID_CONTACT");
                comboContact.init(contactElement, contactElement.getComboRequest(true));
                DefaultGridBagConstraints.lockMinimumSize(comboContact);
                this.addView(comboContact, "ID_CONTACT");
                comboClient.addModelListener("wantedID", new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        int wantedID = comboClient.getWantedID();
                        System.err.println("SET WHERE ID_CLIENT = " + wantedID);
                        if (wantedID != SQLRow.NONEXISTANT_ID && wantedID >= SQLRow.MIN_VALID_ID) {

                            final SQLRow rowClient = getTable().getForeignTable("ID_CLIENT").getRow(wantedID);
                            int idClient = rowClient.getID();
                            comboContact.getRequest().setWhere(new Where(contactElement.getTable().getField("ID_CLIENT"), "=", idClient));
                        } else {
                            comboContact.getRequest().setWhere(null);
                            DevisSQLComponent.this.table.setTarif(null, false);
                        }
                    }
                });

            }

        if (getTable().getFieldsName().contains("DATE_VALIDITE")) {
            c.gridx++;
            this.add(new JLabel(getLabelFor("DATE_VALIDITE")), c);
            c.gridx++;
            JDate dateValidite = new JDate();
            this.add(dateValidite, c);
            this.addView(dateValidite, "DATE_VALIDITE");
        }

        // Tarif
        if (this.getTable().getFieldsName().contains("ID_TARIF")) {
            // TARIF
            c.gridy++;
            c.gridx = 0;
            c.weightx = 0;
            c.weighty = 0;
            c.gridwidth = 1;
            JLabel comp = new JLabel("Tarif à appliquer", SwingConstants.RIGHT);
            this.add(comp, c);
            c.gridx++;
            c.gridwidth = GridBagConstraints.REMAINDER;

            c.weightx = 1;
            this.add(boxTarif, c);
            this.addView(boxTarif, "ID_TARIF");
            DefaultGridBagConstraints.lockMinimumSize(boxTarif);
            boxTarif.addModelListener("wantedID", new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {

                    SQLRow selectedRow = boxTarif.getRequest().getPrimaryTable().getRow(boxTarif.getWantedID());
                    table.setTarif(selectedRow, !isFilling());
                }
            });
        }

        // Table d'élément
            this.table = new DevisItemTable();

            final AcompteField acompteField = new AcompteField();
            acompteField.getDocument().addDocumentListener(new SimpleDocumentListener() {

                @Override
                public void update(DocumentEvent e) {
                    Acompte a = acompteField.getValue();
                    ((DevisItemTable) table).calculPourcentage(a, TypeCalcul.CALCUL_REMISE);
                }
            });

            // Remise
            c.gridy++;
            c.gridx = 0;
            c.weightx = 0;
            c.weighty = 0;
            c.gridwidth = 1;
            JLabel comp = new JLabel(getLabelFor("MONTANT_REMISE"), SwingConstants.RIGHT);
            // this.add(comp, c);
            c.gridx++;
            c.gridwidth = GridBagConstraints.REMAINDER;

            c.weightx = 1;
            // this.add(acompteField, c);
            this.addView(new AcompteRowItemView(acompteField), "MONTANT_REMISE,POURCENT_REMISE", null);


        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy += 5;
        c.weighty = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(this.table, c);
        this.addView(this.table.getRowValuesTable(), "");

        // Panel en bas
        final JPanel bottomPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints cBottom = new DefaultGridBagConstraints();
        // 1ere Colonne : Infos
        cBottom.weightx = 1;
        bottomPanel.add(new TitledSeparator(getLabelFor("INFOS")), cBottom);
        cBottom.gridy++;
        cBottom.fill = GridBagConstraints.BOTH;
        cBottom.weighty = 0;

        final JScrollPane scrollPane = new JScrollPane(this.infos);
        scrollPane.setBorder(null);
        bottomPanel.add(scrollPane, cBottom);

        // 2eme Colonne : Poids total
        final JPanel panel = new JPanel(new GridBagLayout());

        this.textPoidsTotal = new JTextField(8);
        this.textPoidsTotal.setText("0.0");
        final GridBagConstraints cPanel = new DefaultGridBagConstraints();
        panel.add(new JLabel(getLabelFor("T_POIDS")), cPanel);
        cPanel.weightx = 0;
        cPanel.gridx++;
        panel.add(this.textPoidsTotal, cPanel);
        this.textPoidsTotal.setEnabled(false);
        this.textPoidsTotal.setEditable(false);
        this.textPoidsTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        this.textPoidsTotal.setDisabledTextColor(Color.BLACK);

        // Port
        cPanel.gridy++;
        cPanel.gridx = 0;
        panel.add(new JLabel(getLabelFor("PORT_HT"), SwingConstants.RIGHT), cPanel);

        cPanel.gridx++;
        final DeviseField textPortHT = new DeviseField();
        panel.add(textPortHT, cPanel);

        // Remise HT
        final JRadioButton radioEuros = new JRadioButton("en €");
        final JRadioButton radioPourCent = new JRadioButton("en %");
        final ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(radioEuros);
        radioGroup.add(radioPourCent);
        radioEuros.setSelected(true);

        final JPanel panelRemise = new JPanel(new GridBagLayout());
        final GridBagConstraints cRemise = new DefaultGridBagConstraints();
        cRemise.insets = new Insets(0, 0, 1, 0);
        final JLabel labelRemise = new JLabel(getLabelFor("REMISE_HT"));
        panelRemise.add(labelRemise, cRemise);
        cRemise.gridx++;
        panelRemise.add(radioEuros, cRemise);

        cRemise.gridx++;
        cRemise.weightx = 0;
        this.textRemiseHT = new DeviseField();
        panelRemise.add(this.textRemiseHT, cRemise);
        this.textRemiseHT.setMinimumSize(new Dimension(150, 20));
        this.textRemiseHT.setPreferredSize(new Dimension(150, 20));

        cRemise.gridx = 1;
        cRemise.gridy++;
        cRemise.weightx = 0;
        panelRemise.add(radioPourCent, cRemise);

        this.textPourcentRemise = new JTextField(5);
        DefaultGridBagConstraints.lockMinimumSize(this.textPourcentRemise);
        cRemise.gridx++;
        panelRemise.add(this.textPourcentRemise, cRemise);

        cPanel.gridx = 0;
        cPanel.gridy++;
        cPanel.gridwidth = 2;
        panel.add(panelRemise, cPanel);

        cBottom.gridy = 0;
        cBottom.gridx++;
        cBottom.weighty = 0;
        cBottom.weightx = 1;
        cBottom.gridheight = 2;
        cBottom.fill = GridBagConstraints.HORIZONTAL;
        cBottom.anchor = GridBagConstraints.NORTHEAST;
        DefaultGridBagConstraints.lockMinimumSize(panel);
        // bottomPanel.add(panel, cBottom);

        addSQLObject(this.textRemiseHT, "REMISE_HT");
        addSQLObject(textPortHT, "PORT_HT");
        // this.checkImpression.setSelected(true);

        // Total
        this.fieldHT = new DeviseField();
        final DeviseField fieldTVA = new DeviseField();
        final DeviseField fieldTTC = new DeviseField();
        final DeviseField fieldDevise = new DeviseField();
        final DeviseField fieldService = new DeviseField();
        this.fieldHT.setEditable(false);
        fieldTVA.setEditable(false);
        fieldTTC.setEditable(false);
        fieldService.setEditable(false);

        addRequiredSQLObject(this.fieldHT, "T_HT");
        addRequiredSQLObject(fieldTVA, "T_TVA");
        addSQLObject(fieldDevise, "T_DEVISE");
        addRequiredSQLObject(fieldTTC, "T_TTC");
        addRequiredSQLObject(fieldService, "T_SERVICE");
        JTextField poids = new JTextField();
        // addSQLObject(poids, "T_POIDS");

        // FIXME Field add field T_HA dans installation avec recalcul des devis deja saisis
        final DeviseField fieldHA = new DeviseField();

        if (getTable().contains("PREBILAN")) {
            addSQLObject(fieldHA, "PREBILAN");
        } else if (getTable().contains("T_HA")) {

            addSQLObject(fieldHA, "T_HA");
        }

        final TotalPanel totalTTC = new TotalPanel(this.table, this.fieldHT, fieldTVA, fieldTTC, textPortHT, this.textRemiseHT, fieldService, fieldHA, fieldDevise, poids, null);

        cBottom.gridy = 0;
        cBottom.gridx += 2;
        cBottom.gridheight = 2;
        cBottom.gridwidth = 1;
        cBottom.fill = GridBagConstraints.NONE;
        cBottom.weightx = 0;
        cBottom.anchor = GridBagConstraints.NORTHEAST;
        DefaultGridBagConstraints.lockMinimumSize(totalTTC);
        bottomPanel.add(totalTTC, cBottom);

        c.gridy++;
        c.gridx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(bottomPanel, c);

        c.gridx = 0;
        c.gridy++;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.SOUTHEAST;

        this.panelOO = new PanelOOSQLComponent(this);
        this.add(this.panelOO, c);

        textPortHT.getDocument().addDocumentListener(new SimpleDocumentListener() {

            @Override
            public void update(final DocumentEvent e) {
                totalTTC.updateTotal();
            }
        });

        this.textRemiseHT.getDocument().addDocumentListener(new SimpleDocumentListener() {

            @Override
            public void update(final DocumentEvent e) {
                totalTTC.updateTotal();
            }
        });

        this.textPourcentRemise.getDocument().addDocumentListener(new SimpleDocumentListener() {

            @Override
            public void update(final DocumentEvent e) {
                calculPourcentage();
            }
        });

        radioEuros.addChangeListener(new ChangeListener() {

            public void stateChanged(final ChangeEvent e) {
                DevisSQLComponent.this.textRemiseHT.setEnabled(radioEuros.isSelected());
                DevisSQLComponent.this.textPourcentRemise.setEnabled(!radioEuros.isSelected());
            }
        });

        this.table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(final TableModelEvent e) {
                DevisSQLComponent.this.textPoidsTotal.setText(String.valueOf(DevisSQLComponent.this.table.getPoidsTotal()));
            }
        });

        addSQLObject(textObjet, "OBJET");
        addSQLObject(this.textPoidsTotal, "T_POIDS");
        addRequiredSQLObject(dateDevis, "DATE");
        addRequiredSQLObject(this.radioEtat, "ID_ETAT_DEVIS");
        addRequiredSQLObject(this.numeroUniqueDevis, "NUMERO");
        addSQLObject(this.infos, "INFOS");
        comboClient.addModelListener("wantedID", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
                if (!isFilling()) {
                    Integer id = comboClient.getWantedID();

                    if (id > 1) {

                        SQLRow row = comboClient.getElement().getTable().getRow(id);
                        if (comboClient.getElement().getTable().getFieldsName().contains("ID_TARIF")) {

                            SQLRowAccessor foreignRow = row.getForeignRow("ID_TARIF");
                            if (!foreignRow.isUndefined() && (boxTarif.getSelectedRow() == null || boxTarif.getSelectedId() != foreignRow.getID())
                                    && JOptionPane.showConfirmDialog(null, "Appliquer les tarifs associés au client?") == JOptionPane.YES_OPTION) {
                                boxTarif.setValue(foreignRow.getID());
                                // SaisieVenteFactureSQLComponent.this.tableFacture.setTarif(foreignRow,
                                // true);
                            } else {
                                boxTarif.setValue(foreignRow.getID());
                            }

                            // SQLRowAccessor foreignRow = row.getForeignRow("ID_TARIF");
                            // if (foreignRow.isUndefined() &&
                            // !row.getForeignRow("ID_DEVISE").isUndefined()) {
                            // SQLRowValues rowValsD = new SQLRowValues(foreignRow.getTable());
                            // rowValsD.put("ID_DEVISE", row.getObject("ID_DEVISE"));
                            // foreignRow = rowValsD;
                            //
                            // }
                            // table.setTarif(foreignRow, true);
                        }
                    }
                }

            }
        });
        DefaultGridBagConstraints.lockMinimumSize(comboCommercial);
        DefaultGridBagConstraints.lockMinimumSize(comboClient);
    }

    private enum Type_Diff {
        SITE("SITE"), DONNEUR_ORDRE("DONNEUR");
        private final String name;

        private Type_Diff(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private ValidState validStateContact = ValidState.getTrueInstance();

    @Override
    public synchronized ValidState getValidState() {
        assert SwingUtilities.isEventDispatchThread();
        return super.getValidState().and(this.validStateContact);
    }

    private JPanel createPanelDiff(final Type_Diff type) {

        GridBagConstraints cTabSite = new DefaultGridBagConstraints();
        JPanel tabSite = new JPanel(new GridBagLayout());

        cTabSite.weightx = 1;
        cTabSite.fill = GridBagConstraints.HORIZONTAL;
        cTabSite.gridwidth = 2;

        final String name = type.getName();
        final JCheckBox boxSiteDiff = new JCheckBox(getLabelFor(name + "_DIFF"));

        tabSite.add(boxSiteDiff, cTabSite);
        this.addView(boxSiteDiff, name + "_DIFF");

        final String fieldSiren = "SIREN_" + name;
        if (getTable().contains(fieldSiren)) {
            final JLabel labelSrenSite = new JLabel(getLabelFor(fieldSiren));
            labelSrenSite.setHorizontalAlignment(SwingConstants.RIGHT);
            cTabSite.gridwidth = 1;
            cTabSite.gridx = 2;
            cTabSite.weightx = 0;
            tabSite.add(labelSrenSite, cTabSite);

            cTabSite.gridx++;
            cTabSite.weightx = 1;
            if (type == Type_Diff.SITE) {
                throw new IllegalArgumentException("Le siren n'est pas à renseigné pour le site");
            }
            final JTextField siren = this.sirenDonneur;
            tabSite.add(siren, cTabSite);
            this.addView(siren, fieldSiren);
            DefaultGridBagConstraints.lockMinimumSize(siren);
        }
        cTabSite.gridy++;
        cTabSite.gridx = 0;
        cTabSite.weightx = 0;
        cTabSite.fill = GridBagConstraints.HORIZONTAL;
        cTabSite.gridwidth = 1;
        final JLabel labelSiteDes = new JLabel(getLabelFor("DESIGNATION_" + name));

        labelSiteDes.setHorizontalAlignment(SwingConstants.RIGHT);

        tabSite.add(labelSiteDes, cTabSite);
        cTabSite.gridx++;
        cTabSite.weightx = 1;
        final JTextField designation = type == Type_Diff.SITE ? this.desSite : this.desDonneur;
        tabSite.add(designation, cTabSite);
        this.addView(designation, "DESIGNATION_" + name);
        DefaultGridBagConstraints.lockMinimumSize(designation);

        final JLabel labelTelSite = new JLabel(getLabelFor("TEL_" + name));
        labelTelSite.setHorizontalAlignment(SwingConstants.RIGHT);
        cTabSite.gridx++;
        cTabSite.weightx = 0;
        tabSite.add(labelTelSite, cTabSite);

        cTabSite.gridx++;
        cTabSite.weightx = 1;
        final JTextField tel = type == Type_Diff.SITE ? this.telSite : this.telDonneur;
        tabSite.add(tel, cTabSite);
        this.addView(tel, "TEL_" + name);
        DefaultGridBagConstraints.lockMinimumSize(tel);

        final JLabel labelSiteAdr = new JLabel(getLabelFor("ADRESSE_" + name));
        labelSiteAdr.setHorizontalAlignment(SwingConstants.RIGHT);
        cTabSite.gridy++;
        cTabSite.gridx = 0;
        cTabSite.weightx = 0;
        tabSite.add(labelSiteAdr, cTabSite);

        cTabSite.gridx++;
        cTabSite.weightx = 1;
        final ITextArea adresse = type == Type_Diff.SITE ? this.adrSite : this.adrDonneur;
        tabSite.add(adresse, cTabSite);
        this.addView(adresse, "ADRESSE_" + name);
        DefaultGridBagConstraints.lockMinimumSize(adresse);

        final JLabel labelTelPSite = new JLabel(getLabelFor("TEL_P_" + name));
        labelTelPSite.setHorizontalAlignment(SwingConstants.RIGHT);
        cTabSite.gridx++;
        cTabSite.weightx = 0;
        tabSite.add(labelTelPSite, cTabSite);

        cTabSite.gridx++;
        cTabSite.weightx = 1;
        final JTextField telP = type == Type_Diff.SITE ? this.telPSite : this.telPDonneur;
        tabSite.add(telP, cTabSite);
        this.addView(telP, "TEL_P_" + name);

        cTabSite.gridy++;
        cTabSite.gridx = 0;
        cTabSite.weightx = 0;
        final JLabel labelVilleAdr = new JLabel(getLabelFor("VILLE_" + name));
        labelVilleAdr.setHorizontalAlignment(SwingConstants.RIGHT);
        tabSite.add(labelVilleAdr, cTabSite);

        cTabSite.gridx++;
        cTabSite.weightx = 1;
        final ITextComboVilleViewer ville = type == Type_Diff.SITE ? this.villeSite : this.villeDonneur;
        tabSite.add(ville, cTabSite);
        this.addView(ville, "VILLE_" + name);
        DefaultGridBagConstraints.lockMinimumSize(ville);

        cTabSite.gridx++;
        cTabSite.weightx = 0;

        final JLabel labelFaxSite = new JLabel(getLabelFor("FAX_" + name));
        labelFaxSite.setHorizontalAlignment(SwingConstants.RIGHT);
        tabSite.add(labelFaxSite, cTabSite);

        cTabSite.gridx++;
        cTabSite.weightx = 1;
        final JTextField fax = type == Type_Diff.SITE ? this.faxSite : this.faxDonneur;
        tabSite.add(fax, cTabSite);
        this.addView(fax, "FAX_" + name);
        DefaultGridBagConstraints.lockMinimumSize(fax);

        cTabSite.gridy++;
        cTabSite.gridx = 0;
        cTabSite.weightx = 0;

        final JLabel labelContactSite = new JLabel(getLabelFor("CONTACT_" + name));
        labelContactSite.setHorizontalAlignment(SwingConstants.RIGHT);
        tabSite.add(labelContactSite, cTabSite);

        cTabSite.gridx++;
        cTabSite.weightx = 1;
        final JTextField contact = type == Type_Diff.SITE ? this.contactSite : this.contactDonneur;
        tabSite.add(contact, cTabSite);
        this.addView(contact, "CONTACT_" + name);

        cTabSite.gridx++;
        cTabSite.weightx = 0;

        final JLabel labelMailSite = new JLabel(getLabelFor("MAIL_" + name));
        labelMailSite.setHorizontalAlignment(SwingConstants.RIGHT);
        tabSite.add(labelMailSite, cTabSite);

        cTabSite.gridx++;
        cTabSite.weightx = 1;
        final JTextField mail = type == Type_Diff.SITE ? this.mailSite : this.mailDonneur;
        tabSite.add(mail, cTabSite);
        this.addView(mail, "MAIL_" + name);

        boxSiteDiff.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                final boolean selected = boxSiteDiff.isSelected();
                setSiteEnabled(selected, type);
                if (!selected) {
                    clearFieldDiff(type);
                }
            }
        });
        return tabSite;
    }

    private void clearFieldDiff(Type_Diff type) {
        if (type == Type_Diff.SITE) {
            this.desSite.setText("");
            this.adrSite.setText("");
            this.villeSite.setValue(null);
            this.telPSite.setText("");
            this.telSite.setText("");
            this.mailSite.setText("");
            this.contactSite.setText("");
            this.faxSite.setText("");
        } else {
            this.sirenDonneur.setText("");
            this.desDonneur.setText("");
            this.adrDonneur.setText("");
            this.villeDonneur.setValue(null);
            this.telPDonneur.setText("");
            this.telDonneur.setText("");
            this.mailDonneur.setText("");
            this.contactDonneur.setText("");
            this.faxDonneur.setText("");
        }
    }

    private void setSiteEnabled(boolean b, Type_Diff type) {
        if (type == Type_Diff.SITE) {
            this.desSite.setEditable(b);
            this.adrSite.setEditable(b);
            this.villeSite.setEnabled(b);
            this.telPSite.setEditable(b);
            this.telSite.setEditable(b);
            this.mailSite.setEditable(b);
            this.contactSite.setEditable(b);
            this.faxSite.setEditable(b);
        } else {
            b = false;

            this.sirenDonneur.setEditable(b);
            this.desDonneur.setEditable(b);
            this.adrDonneur.setEditable(b);
            this.villeDonneur.setEnabled(b);
            this.telPDonneur.setEditable(b);
            this.telDonneur.setEditable(b);
            this.mailDonneur.setEditable(b);
            this.contactDonneur.setEditable(b);
            this.faxDonneur.setEditable(b);
        }
    }

    @Override
    protected SQLRowValues createDefaults() {
        System.err.println("Create defaults");

        setSiteEnabled(false, Type_Diff.DONNEUR_ORDRE);
        setSiteEnabled(false, Type_Diff.SITE);

        // Numero incremental auto
        final SQLRowValues rowVals = new SQLRowValues(getTable());
        rowVals.put("NUMERO", NumerotationAutoSQLElement.getNextNumero(getElement().getClass()));

        // User
        // final SQLSelect sel = new SQLSelect(Configuration.getInstance().getBase());
        final SQLElement eltComm = Configuration.getInstance().getDirectory().getElement("COMMERCIAL");
        final int idUser = UserManager.getInstance().getCurrentUser().getId();
        //
        // sel.addSelect(eltComm.getTable().getKey());
        // sel.setWhere(new Where(eltComm.getTable().getField("ID_USER_COMMON"), "=", idUser));
        // final List<SQLRow> rowsComm = (List<SQLRow>)
        // Configuration.getInstance().getBase().getDataSource().execute(sel.asString(), new
        // SQLRowListRSH(eltComm.getTable()));

        SQLRow rowsComm = SQLBackgroundTableCache.getInstance().getCacheForTable(eltComm.getTable()).getFirstRowContains(idUser, eltComm.getTable().getField("ID_USER_COMMON"));

        if (rowsComm != null) {
            rowVals.put("ID_COMMERCIAL", rowsComm.getID());
        }

        if (getTable().getUndefinedID() == SQLRow.NONEXISTANT_ID) {
            rowVals.put("ID_ETAT_DEVIS", EtatDevisSQLElement.EN_ATTENTE);
        } else {
            SQLRowValues foreign = UndefinedRowValuesCache.getInstance().getDefaultRowValues(getTable());
            if (foreign != null && !foreign.isUndefined()) {
                rowVals.put("ID_ETAT_DEVIS", foreign.getObject("ID_ETAT_DEVIS"));
            } else {
                rowVals.put("ID_ETAT_DEVIS", EtatDevisSQLElement.EN_ATTENTE);
            }
        }
        rowVals.put("T_HT", Long.valueOf(0));
        rowVals.put("T_TVA", Long.valueOf(0));
        rowVals.put("T_SERVICE", Long.valueOf(0));
        rowVals.put("T_TTC", Long.valueOf(0));

        if (getTable().getFieldsName().contains("DATE_VALIDITE")) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 1);
            rowVals.put("DATE_VALIDITE", cal.getTime());
        }
        return rowVals;
    }

    private void calculPourcentage() {
        final String remiseP = this.textPourcentRemise.getText().replace(',', '.');
        Long totalHT = this.fieldHT.getValue();
        Long remiseHT = this.textRemiseHT.getValue();

        totalHT = totalHT == null ? Long.valueOf(0) : totalHT;
        remiseHT = remiseHT == null ? Long.valueOf(0) : remiseHT;

        try {
            final int valueRemise = Integer.valueOf(remiseP);

            final long remise = valueRemise * (totalHT.longValue() + remiseHT.longValue()) / 100;
            if (remiseHT != remise) {
                this.textRemiseHT.setValue(remise);
            }

        } catch (final NumberFormatException e) {
            ExceptionHandler.handle("Erreur durant le calcul de la remise", e);
        }

    }

    @Override
    public int insert(final SQLRow order) {

        final int idDevis;
        // on verifie qu'un devis du meme numero n'a pas été inséré entre temps
        if (this.numeroUniqueDevis.checkValidation()) {

            idDevis = super.insert(order);
            this.table.updateField("ID_DEVIS", idDevis);
            // Création des articles
            this.table.createArticle(idDevis, getElement());

            // generation du document
            try {
                final DevisXmlSheet sheet = new DevisXmlSheet(getTable().getRow(idDevis));
                sheet.createDocumentAsynchronous();
                sheet.showPrintAndExportAsynchronous(DevisSQLComponent.this.panelOO.isVisualisationSelected(), DevisSQLComponent.this.panelOO.isImpressionSelected(), true);
            } catch (Exception e) {
                ExceptionHandler.handle("Impossible de créer le devis", e);
            }

            // incrémentation du numéro auto
            if (NumerotationAutoSQLElement.getNextNumero(getElement().getClass()).equalsIgnoreCase(this.numeroUniqueDevis.getText().trim())) {
                final SQLRowValues rowVals = new SQLRowValues(this.tableNum);
                int val = this.tableNum.getRow(2).getInt(NumerotationAutoSQLElement.getLabelNumberFor(getElement().getClass()));
                val++;
                rowVals.put(NumerotationAutoSQLElement.getLabelNumberFor(getElement().getClass()), new Integer(val));
                try {
                    rowVals.update(2);
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            idDevis = getSelectedID();
            ExceptionHandler.handle("Impossible d'ajouter, numéro de devis existant.");
            final Object root = SwingUtilities.getRoot(this);
            if (root instanceof EditFrame) {
                final EditFrame frame = (EditFrame) root;
                frame.getPanel().setAlwaysVisible(true);
            }
        }

        return idDevis;
    }

    @Override
    public void select(final SQLRowAccessor r) {
        if (r == null || r.getIDNumber() == null)
            super.select(r);
        else {
            System.err.println(r);
            final SQLRowValues rVals = r.asRowValues();
            final SQLRowValues vals = new SQLRowValues(r.getTable());
            vals.load(rVals, createSet("ID_CLIENT"));
            vals.setID(rVals.getID());
            System.err.println("Select CLIENT");

            super.select(vals);
            rVals.remove("ID_CLIENT");
            super.select(rVals);
        }

        // super.select(r);
        if (r != null) {
            this.table.insertFrom("ID_DEVIS", r.getID());
            // this.radioEtat.setVisible(r.getID() > getTable().getUndefinedID());
            if (getTable().contains("SITE_DIFF"))
                setSiteEnabled(r.getBoolean("SITE_DIFF"), Type_Diff.SITE);

            if (getTable().contains("DONNEUR_DIFF"))
                setSiteEnabled(r.getBoolean("DONNEUR_DIFF"), Type_Diff.DONNEUR_ORDRE);
        }

    }

    @Override
    public void update() {

        if (!this.numeroUniqueDevis.checkValidation()) {
            ExceptionHandler.handle("Impossible de modifier, numéro de devis existant.");
            final Object root = SwingUtilities.getRoot(this);
            if (root instanceof EditFrame) {
                final EditFrame frame = (EditFrame) root;
                frame.getPanel().setAlwaysVisible(true);
            }
            return;
        }
        super.update();
        this.table.updateField("ID_DEVIS", getSelectedID());
        this.table.createArticle(getSelectedID(), getElement());

        // generation du document

        try {
            final DevisXmlSheet sheet = new DevisXmlSheet(getTable().getRow(getSelectedID()));
            sheet.createDocumentAsynchronous();
            sheet.showPrintAndExportAsynchronous(DevisSQLComponent.this.panelOO.isVisualisationSelected(), DevisSQLComponent.this.panelOO.isImpressionSelected(), true);
        } catch (Exception e) {
            ExceptionHandler.handle("Impossible de créer le devis", e);
        }

    }

    /**
     * Création d'un devis à partir d'un devis existant
     * 
     * @param idDevis
     * 
     */
    public void loadDevisExistant(final int idDevis) {

        final SQLElement devis = Configuration.getInstance().getDirectory().getElement("DEVIS");
        final SQLElement devisElt = Configuration.getInstance().getDirectory().getElement("DEVIS_ELEMENT");

        // On duplique le devis
        if (idDevis > 1) {
            final SQLRow row = devis.getTable().getRow(idDevis);
            final SQLRowValues rowVals = new SQLRowValues(devis.getTable());
            rowVals.put("ID_CLIENT", row.getInt("ID_CLIENT"));
            rowVals.put("NUMERO", NumerotationAutoSQLElement.getNextNumero(getElement().getClass()));


            this.select(rowVals);
        }

        // On duplique les elements de devis
        final List<SQLRow> myListItem = devis.getTable().getRow(idDevis).getReferentRows(devisElt.getTable());

        if (myListItem.size() != 0) {
            this.table.getModel().clearRows();

            for (final SQLRow rowElt : myListItem) {

                final SQLRowValues rowVals = rowElt.createUpdateRow();
                rowVals.clearPrimaryKeys();
                this.table.getModel().addRow(rowVals);
                final int rowIndex = this.table.getModel().getRowCount() - 1;
                this.table.getModel().fireTableModelModified(rowIndex);
            }
        } else {
            this.table.getModel().clearRows();
        }
        this.table.getModel().fireTableDataChanged();
        this.table.repaint();
    }
}
