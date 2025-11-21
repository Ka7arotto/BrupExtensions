import VulTool.TableData;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;


import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

public class CustomLogger implements BurpExtension {
    private MontoyaApi api;
    private MyTableModel tableModel;
    private MyHttpHandler myHttpHandler;
    @Override
    public void initialize(MontoyaApi montoyaApi) {
        this.api = montoyaApi;

        api.extension().setName("XSS-Scan");
        api.logging().logToOutput("XSS-Scan start!");

        this.tableModel = new MyTableModel();

        api.userInterface().registerSuiteTab("XSS-Scan", constructLoggerTab(tableModel));
        myHttpHandler=new MyHttpHandler(tableModel, api);
        api.http().registerHttpHandler(myHttpHandler);
    }

    private Component constructLoggerTab(MyTableModel tableModel) {
        // 创建主标签页
        JTabbedPane mainTabbedPane = new JTabbedPane();
        // 添加检测结果标签页
        mainTabbedPane.addTab("XSS扫描", createDetectionResultsPanel(tableModel));
        // 添加控制面板标签页
        mainTabbedPane.addTab("控制面板", createControlPanel());

        return mainTabbedPane;
    }
    private Component createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建开关按钮
        JToggleButton toggleButton = new JToggleButton("开启XSS检测");
        toggleButton.setSelected(MyHttpHandler.isOpen);
        updateButtonText(toggleButton);

        toggleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleButton.setMaximumSize(new Dimension(200, 40));
        toggleButton.setPreferredSize(new Dimension(200, 40));

        // 状态标签
        JLabel statusLabel = new JLabel("当前状态: " + (MyHttpHandler.isOpen ? "已开启" : "已关闭"));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(MyHttpHandler.isOpen ? new Color(0, 128, 0) : Color.RED);

        // 使用JEditorPane
        JEditorPane infoEditor = new JEditorPane("text/html",
                "<html><div style='width: 300px;'>"
                        + "开启XSS检测后，插件会自动检测经过Burp的请求中的XSS漏洞。<br><br>"
                        + "检测到的XSS漏洞会在扫描结果中以黄色高亮显示。"
                        + "</div></html>");
        infoEditor.setEditable(false);
        infoEditor.setBackground(controlPanel.getBackground());
        infoEditor.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // 添加按钮监听器
        toggleButton.addActionListener(e -> {
            JToggleButton button = (JToggleButton) e.getSource();
            MyHttpHandler.isOpen = button.isSelected();
            updateButtonText(button);
            // 更新状态标签
            statusLabel.setText("当前状态: " + (MyHttpHandler.isOpen ? "已开启" : "已关闭"));
            statusLabel.setForeground(MyHttpHandler.isOpen ? new Color(0, 128, 0) : Color.RED);

            api.logging().logToOutput("XSS检测" + (MyHttpHandler.isOpen ? "已开启" : "已关闭"));
        });

        // 添加组件到面板
        controlPanel.add(toggleButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        controlPanel.add(statusLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        controlPanel.add(infoEditor);

        return controlPanel;
    }

    private void updateButtonText(JToggleButton button) {
        if (button.isSelected()) {
            button.setText("关闭XSS检测");
        } else {
            button.setText("开启XSS检测");
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

        // 创建带高亮功能的表格
        JTable table = new JTable(tableModel) {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                // 将视图行索引转换为模型行索引
                int modelRowIndex = convertRowIndexToModel(rowIndex);
                TableData tableData = tableModel.get(modelRowIndex);
                requestViewer.setRequest(tableData.getHttpRequestResponse().request());
                responseViewer.setResponse(tableData.getHttpRequestResponse().response());
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        // 启用表格排序功能
        table.setAutoCreateRowSorter(true);

        // 设置自定义单元格渲染器用于高亮
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color XSS_HIGHLIGHT_COLOR = new Color(255, 255, 200); // 淡黄色背景
            private final Color NORMAL_COLOR = Color.WHITE;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 获取模型中的实际行索引（考虑排序后的映射）
                int modelRow = table.convertRowIndexToModel(row);

                if (tableModel.isXssRow(modelRow)) {
                    c.setBackground(XSS_HIGHLIGHT_COLOR);
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(NORMAL_COLOR);
                    c.setForeground(Color.BLACK);
                }

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }

                return c;
            }
        });

        // 添加右键菜单
        JPopupMenu popupMenu = createTablePopupMenu(table, tableModel);
        table.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(table);
        splitPane.setLeftComponent(scrollPane);
        splitPane.setDividerLocation(0.5);

        return splitPane;
    }
    // 创建表格右键菜单
    private JPopupMenu createTablePopupMenu(JTable table, MyTableModel tableModel) {
        JPopupMenu popupMenu = new JPopupMenu();

        // 删除所有记录菜单项
        JMenuItem deleteAllItem = new JMenuItem("删除所有记录");
        deleteAllItem.addActionListener(e -> {
            if (tableModel.getRowCount() > 0) {
                    tableModel.cleanXssList();
                    MyHttpHandler.id.set(0);
                    api.logging().logToOutput("删除了所有记录");
            } else {
                JOptionPane.showMessageDialog(table, "没有可删除的记录", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        popupMenu.add(deleteAllItem);
        popupMenu.addSeparator();

        return popupMenu;
    }
}