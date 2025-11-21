/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

import VulTool.TableData;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MyTableModel extends AbstractTableModel
{
    private final List<TableData> dataList;
    private final ArrayList<Integer>xssIdList=new ArrayList<Integer>();

    public MyTableModel()
    {
        this.dataList = new ArrayList<>();
    }
    public void cleanXssList(){
        dataList.clear();
        xssIdList.clear();
        fireTableDataChanged();; // 通知表格数据已删除
    }

    @Override
    public synchronized int getRowCount()
    {
        return dataList.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // 这有助于排序器更好地处理不同类型的数据
        switch (columnIndex) {
            case 0: return Integer.class; // ID列
            default: return String.class; // 其他列
        }
    }
    @Override
    public int getColumnCount()
    {
        return 5;
    }

    @Override
    public String getColumnName(int column)
    {
        return switch (column)
                {
                    case 0 -> "request id";
                    case 1 -> "URL";
                    case 2 -> "对应的request id";
                    case 3 -> "xss-param";
                    case 4 -> "status_code";
                    default -> "";
                };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex)
    {
        TableData tableData = dataList.get(rowIndex);

        return switch (columnIndex)
                {
                    case 0 -> tableData.getId();
                    case 1 -> tableData.getUrl();
                    case 2 -> tableData.getXssId();
                    case 3 -> tableData.getXssParam();
                    case 4 -> tableData.getStatus();

                    default -> "";
                };
    }

    public synchronized void add(TableData tableData)
    {
        int index = dataList.size();
        dataList.add(tableData);
        if (tableData.getXssId() != null) { // 如果是有XSS的记录，添加到高亮列表
            xssIdList.add(tableData.getId());
        }
        fireTableRowsInserted(index, index);
    }


    public synchronized TableData get(int rowIndex)
    {
        return dataList.get(rowIndex);
    }
    // 检查某行是否需要高亮
    public boolean isXssRow(int row) {
        if (row >= 0 && row < dataList.size()) {
            TableData data = dataList.get(row);
            return xssIdList.contains(data.getId());
        }
        return false;
    }


}
