package com.poliusp.monografia;

import com.poliusp.monografia.business.BLCalculo;
import com.poliusp.monografia.business.BLProcessarArquivos;
import com.poliusp.monografia.entity.IndiceNegociabilidade;
import weka.gui.WrapLayout;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainGUI extends JFrame {

    private JTextField txtFile;
    private JButton btnChooseFile, btnProcessarArquivo;
    private JProgressBar jProgressBar;
    private FlowLayout layout;
    private Container container;

    private JLabel lblDataInicio, lblDataFim, lblDiasPrevisao;
    private JTextField txtDateInicio, txtDateFim, txtDiasPrevisao;
    private JButton btnPrevisao;
    private JTable tblPrevisao;
    private DefaultTableModel tableModel;
    public MainGUI()
    {
        super("");

        layout = new WrapLayout();
        container = getContentPane();
        setLayout(layout);
        layout.setAlignment(FlowLayout.LEFT);
        layout.layoutContainer(container);

        //Importação
        TitledBorder borderImportacao = new TitledBorder("Importação");
        borderImportacao.setTitleJustification(TitledBorder.CENTER);

        JPanel panelImportacao = new JPanel();
        panelImportacao.setBorder(borderImportacao);

        btnChooseFile = new JButton("Selecionar");
        panelImportacao.add(btnChooseFile);

        txtFile = new JTextField();
        txtFile.setPreferredSize(new Dimension(550, 27));
        panelImportacao.add(txtFile);

        btnProcessarArquivo = new JButton("Processar");
        panelImportacao.add(btnProcessarArquivo);
        add(panelImportacao);

        //Previsão
        TitledBorder borderPrevisao = new TitledBorder("Previsão");
        borderPrevisao.setTitleJustification(TitledBorder.CENTER);

        JPanel panelPrevisao = new JPanel();
        panelPrevisao.setBorder(borderPrevisao);

        txtDateInicio = new JTextField();
        txtDateFim = new JTextField();
        txtDiasPrevisao = new JTextField();
        lblDiasPrevisao = new JLabel("Dias Previsão:");
        lblDataInicio = new JLabel("Data Início:");
        lblDataFim = new JLabel("Data Fim:");
        txtDateInicio.setText("01/09/2015");
        txtDateInicio.setPreferredSize(new Dimension(97, 25));

        txtDateFim.setText("24/08/2016");
        txtDateFim.setPreferredSize(new Dimension(97, 25));

        txtDiasPrevisao.setText("5");
        txtDiasPrevisao.setPreferredSize(new Dimension(50, 25));

        panelPrevisao.add(lblDataInicio);
        panelPrevisao.add(txtDateInicio);
        panelPrevisao.add(lblDataFim);
        panelPrevisao.add(txtDateFim);
        panelPrevisao.add(lblDiasPrevisao);
        panelPrevisao.add(txtDiasPrevisao);

        btnPrevisao = new JButton("Previsão");
        panelPrevisao.add(btnPrevisao);
        add(panelPrevisao);

        //Tabela
        montaTabela();

        btnChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser jfileChooser = new JFileChooser();
                jfileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
                jfileChooser.setFileFilter(filter);
                jfileChooser.setAcceptAllFileFilterUsed(false);
                jfileChooser.setCurrentDirectory(new File("."));
                jfileChooser.setVisible(true);


                int rVal = jfileChooser.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    txtFile.setText(jfileChooser.getSelectedFile().toString());
                }
            }
        });

        btnProcessarArquivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    BLProcessarArquivos processFile = new BLProcessarArquivos();
                    processFile.lerArquivo(txtFile.getText());
                    JOptionPane.showMessageDialog(null, "Sucesso");
                }catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        btnPrevisao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    Date dtInicio = formato.parse(txtDateInicio.getText());
                    Date dtFim = formato.parse(txtDateFim.getText());

                    BLCalculo blCalculo = new BLCalculo();
                    java.util.List<IndiceNegociabilidade> indices = blCalculo.CalcularCarteira(dtInicio, dtFim, Integer.parseInt(txtDiasPrevisao.getText()));

                    for (IndiceNegociabilidade indice: indices) {
                        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
                        defaultFormat.setMinimumFractionDigits(2);
                        tableModel.addRow(new Object[]{indice.getSimbolo(), defaultFormat.format(indice.getIndiceNegociabilidade()), (indice.getPercenteIndicice()? "Sim": "Não"), indice.getCriterioExclusão()});
                    }
                }catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(795, 600);
        setLocationRelativeTo(null);
    }

    private void montaTabela() {

        TitledBorder borderTable = new TitledBorder("Resultado");
        borderTable.setTitleJustification(TitledBorder.CENTER);

        JPanel panelTabela = new JPanel();
        panelTabela.setBorder(borderTable);

        tblPrevisao = new JTable();
        tblPrevisao.setPreferredScrollableViewportSize(new Dimension(750, 350));
        tblPrevisao.setFillsViewportHeight(true);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Símbolo", "Índice Neg.%", "Pertence Índice", "Critério Exclusão"});

        tblPrevisao.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane( tblPrevisao );
        getContentPane().add( scrollPane );

        panelTabela.add(scrollPane);

        tblPrevisao.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if ( column == 2) {
                    c.setForeground(value.toString().equals("Sim") ? Color.BLUE : Color.RED);
                }
                return c;
            }
        });

        tblPrevisao.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblPrevisao.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblPrevisao.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblPrevisao.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblPrevisao.getColumnModel().getColumn(3).setPreferredWidth(380);

        add(panelTabela);
    }
}
