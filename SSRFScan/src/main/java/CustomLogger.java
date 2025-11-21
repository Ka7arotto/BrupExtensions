import VulTool.PayloadConfig;
import VulTool.SSRFDetector;
import VulTool.TableData;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

public class CustomLogger implements BurpExtension {
    private MontoyaApi api;
    private PayloadConfig currentConfig;
    private SSRFDetector ssrfDetector;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        this.api = montoyaApi;
        this.currentConfig = new PayloadConfig(api);
        this.ssrfDetector=new SSRFDetector(api);

        api.extension().setName("SSRF-Scan");
        api.logging().logToOutput("SSRF-Scan start!");

        MyTableModel tableModel = new MyTableModel();
        api.userInterface().registerSuiteTab("SSRF-Scan", constructLoggerTab(tableModel));

        api.http().registerHttpHandler(new MyHttpHandler(tableModel, api, currentConfig,ssrfDetector));
    }

    private Component constructLoggerTab(MyTableModel tableModel) {
        // 创建主标签页
        JTabbedPane mainTabbedPane = new JTabbedPane();

        // 添加检测结果标签页
        mainTabbedPane.addTab("SSRF扫描", createDetectionResultsPanel(tableModel));

        //添加SSRF参数检测字典标签页
        mainTabbedPane.addTab("Param配置",createSSRFParameterConfigPanel());

        // 添加Payload配置标签页
        mainTabbedPane.addTab("Payload配置", createPayloadConfigPanel());



        return mainTabbedPane;
    }

    // 创建SSRF参数检测配置面板
    private Component createSSRFParameterConfigPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建顶部输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 单个参数输入
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("单个参数名:"), gbc);

        gbc.gridx = 1;
        JTextField singleParamField = new JTextField(20);
        inputPanel.add(singleParamField, gbc);

        gbc.gridx = 2;
        JButton addSingleButton = new JButton("Add");
        inputPanel.add(addSingleButton, gbc);

        // 批量粘贴区域
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 3;
        inputPanel.add(new JLabel("批量粘贴参数列表:"), gbc);

        gbc.gridy = 2;
        JTextArea batchTextArea = new JTextArea(5, 30);
        batchTextArea.setLineWrap(true);
        batchTextArea.setWrapStyleWord(true);
        JScrollPane batchScrollPane = new JScrollPane(batchTextArea);
        inputPanel.add(batchScrollPane, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JButton pasteAddButton = new JButton("Add all");
        inputPanel.add(pasteAddButton, gbc);


        // 参数列表表格
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        String[] columnNames = {"参数名称"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable paramTable = new JTable(tableModel);

        // 创建右键菜单
        JPopupMenu popupMenu = createParamTablePopupMenu(paramTable, tableModel);
        paramTable.setComponentPopupMenu(popupMenu);

        JScrollPane tableScrollPane = new JScrollPane(paramTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 300));
        inputPanel.add(tableScrollPane, gbc);

        // 底部按钮面板
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton deleteSelectedButton = new JButton("Remove");
        JButton clearAllButton = new JButton("Clear");

        buttonPanel.add(deleteSelectedButton);
        buttonPanel.add(clearAllButton);

        inputPanel.add(buttonPanel, gbc);

        // 刷新参数列表
        refreshParamTable(tableModel);

        // 按钮事件处理
        addSingleButton.addActionListener(e -> {
            String param = singleParamField.getText().trim();
            if (!param.isEmpty()) {
                ssrfDetector.addKeyword(param);
                singleParamField.setText("");
                refreshParamTable(tableModel);
                api.logging().logToOutput("添加参数: " + param);
            }
        });

        pasteAddButton.addActionListener(e -> {
            String text = batchTextArea.getText().trim();
            if (!text.isEmpty()) {
                String[] params = text.split("[\\r\\n]+");
                ssrfDetector.addKeywords(List.of(params));
                }
                refreshParamTable(tableModel);
                JOptionPane.showMessageDialog(mainPanel, "Success!");
            }
        );


        deleteSelectedButton.addActionListener(e -> {
            deleteSelectedParam(paramTable, tableModel, mainPanel);
        });

        clearAllButton.addActionListener(e -> {
            clearAllParams(mainPanel, tableModel);
        });


        mainPanel.add(inputPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    // 创建参数表格右键菜单
    private JPopupMenu createParamTablePopupMenu(JTable paramTable, DefaultTableModel tableModel) {
        JPopupMenu popupMenu = new JPopupMenu();

        // 删除选中菜单项
        JMenuItem deleteMenuItem = new JMenuItem("删除选中");
        deleteMenuItem.addActionListener(e -> {
            deleteSelectedParam(paramTable, tableModel, null);
        });
        popupMenu.add(deleteMenuItem);

        // 清空所有菜单项
        JMenuItem clearAllMenuItem = new JMenuItem("清空所有");
        clearAllMenuItem.addActionListener(e -> {
            clearAllParams(paramTable, tableModel);
        });
        popupMenu.add(clearAllMenuItem);

        return popupMenu;
    }

    // 删除选中参数
    private void deleteSelectedParam(JTable paramTable, DefaultTableModel tableModel, Component parentComponent) {
        int selectedRow = paramTable.getSelectedRow();
        if (selectedRow != -1) {
            String param = (String) tableModel.getValueAt(selectedRow, 0);

                ssrfDetector.removeKeyword(param);
                refreshParamTable(tableModel);
                api.logging().logToOutput("删除参数: " + param);
        } else {
            JOptionPane.showMessageDialog(
                    parentComponent != null ? parentComponent : paramTable,
                    "请先选择一个参数进行删除！",
                    "提示",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // 清空所有参数
    private void clearAllParams(Component parentComponent, DefaultTableModel tableModel) {
            ssrfDetector.clear();
            refreshParamTable(tableModel);
            api.logging().logToOutput("已清空所有参数");
    }

    // 刷新参数表格
    private void refreshParamTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (String param : ssrfDetector.getParams()) {
            tableModel.addRow(new Object[]{param});
        }
    }

    private Component createPayloadConfigPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 配置面板
        JPanel configPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Payload类型选择
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Payload类型:"), gbc);

        gbc.gridx = 1;
        String[] payloadTypes = {"URL", "DNS"};
        JComboBox<String> payloadTypeCombo = new JComboBox<>(payloadTypes);
        configPanel.add(payloadTypeCombo, gbc);

        // 自定义Payload输入
        gbc.gridx = 0; gbc.gridy = 1;
        configPanel.add(new JLabel("自定义Payload:"), gbc);

        gbc.gridx = 1;
        JTextField customPayloadField = new JTextField(20);
        configPanel.add(customPayloadField, gbc);

        // 添加按钮
        gbc.gridx = 2;
        JButton addButton = new JButton("添加");
        configPanel.add(addButton, gbc);

        // 启用扫描选项
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3;
        JCheckBox enableScanCheckbox = new JCheckBox("启用主动扫描");
        enableScanCheckbox.setSelected(currentConfig.isEnableActiveScan());
        configPanel.add(enableScanCheckbox, gbc);

        // 保存按钮
        gbc.gridy = 3;
        JButton saveButton = new JButton("保存配置");
        configPanel.add(saveButton, gbc);

        // Payload列表显示
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        String[] columnNames = {"类型", "Payload"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable payloadTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(payloadTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 200));
        configPanel.add(tableScrollPane, gbc);

        // 删除按钮面板
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton deleteButton = new JButton("删除选中");
        JButton clearAllButton = new JButton("清空所有");
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearAllButton);
        configPanel.add(buttonPanel, gbc);
        // 刷新Payload列表
        refreshPayloadTable(tableModel);

        // 删除选中按钮事件
        deleteButton.addActionListener(e -> {
            int selectedRow = payloadTable.getSelectedRow();
            if (selectedRow != -1) {
                String type = (String) tableModel.getValueAt(selectedRow, 0);
                String payload = (String) tableModel.getValueAt(selectedRow, 1);

                if ("URL".equals(type)) {
                    currentConfig.removeUrlPayload(payload);
                } else {
                    currentConfig.removeDnsPayload(payload);
                }

                refreshPayloadTable(tableModel);
                api.logging().logToOutput("删除Payload: " + type + " - " + payload);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "请先选择一个Payload进行删除！", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        //清除所有事件处理
        clearAllButton.addActionListener(e->{
            currentConfig.clearAllPayloads();
            refreshPayloadTable(tableModel);
        });
        // 添加事件处理
        addButton.addActionListener(e -> {
            String payload = customPayloadField.getText().trim();
            String type = (String) payloadTypeCombo.getSelectedItem();

            if (!payload.isEmpty()) {
                if ("URL".equals(type)) {
                    currentConfig.addUrlPayload(payload);
                } else {
                    currentConfig.addDnsPayload(payload);
                }
                customPayloadField.setText("");
                refreshPayloadTable(tableModel);
                api.logging().logToOutput("添加Payload: " + type + " - " + payload);
            }
        });

        //保存配置
        saveButton.addActionListener(e -> {
            currentConfig.setEnableActiveScan(enableScanCheckbox.isSelected());
            JOptionPane.showMessageDialog(mainPanel, "配置保存成功！");
            api.logging().logToOutput("配置已保存，启用主动扫描: " + currentConfig.isEnableActiveScan());
        });

        mainPanel.add(configPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private void refreshPayloadTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);

        for (String payload : currentConfig.getUrlPayloads()) {
            tableModel.addRow(new Object[]{"URL", payload});
        }

        for (String payload : currentConfig.getDnsPayloads()) {
            tableModel.addRow(new Object[]{"DNS", payload});
        }
    }

    private Component createDetectionResultsPanel(MyTableModel tableModel) {
        // 原有的检测结果面板代码
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JTabbedPane tabs = new JTabbedPane();
        UserInterface userInterface = api.userInterface();

        HttpRequestEditor requestViewer = userInterface.createHttpRequestEditor(READ_ONLY);
        HttpResponseEditor responseViewer = userInterface.createHttpResponseEditor(READ_ONLY);

        tabs.addTab("Request", requestViewer.uiComponent());
        tabs.addTab("Response", responseViewer.uiComponent());

        splitPane.setRightComponent(tabs);

        JTable table = new JTable(tableModel) {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                TableData tableData = tableModel.get(rowIndex);
                requestViewer.setRequest(tableData.getHttpRequestResponse().request());
                responseViewer.setResponse(tableData.getHttpRequestResponse().response());
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);
        splitPane.setLeftComponent(scrollPane);
        splitPane.setDividerLocation(0.5);

        return splitPane;
    }
}