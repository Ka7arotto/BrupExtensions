/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

import VulTool.TableData;
import burp.api.montoya.http.handler.HttpResponseReceived;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MyTableModel extends AbstractTableModel
{
    private final List<TableData> log;

    public MyTableModel()
    {
        this.log = new ArrayList<>();
    }

    @Override
    public synchronized int getRowCount()
    {
        return log.size();
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
                    case 0 -> "id";
                    case 1 -> "URL";
                    case 2 -> "status_code";
                    case 3 -> "Length";
                    case 4 -> "Vulnerability";
                    default -> "";
                };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex)
    {
        TableData tableData = log.get(rowIndex);

        return switch (columnIndex)
                {
                    case 0 -> tableData.getId();
                    case 1 -> tableData.getUrl();
                    case 2 -> tableData.getStatus();
                    case 3 -> tableData.getLength();
                    case 4 -> tableData.getVulnerability();
                    default -> "";
                };
    }

    public synchronized void add(TableData tableData)
    {
        int index = log.size();
        log.add(tableData);
        fireTableRowsInserted(index, index);
    }

    public synchronized TableData get(int rowIndex)
    {
        return log.get(rowIndex);
    }
}
