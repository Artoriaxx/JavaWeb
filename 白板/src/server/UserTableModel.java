package server;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UserTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 9199079384604183114L;
    private String[] colName = {"编号", "名称", "性别"};
    private List<String[]> rows = new ArrayList<String[]>();
    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return colName.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return (rows.get(rowIndex))[columnIndex];
    }
    public String getColumnName(int c) {
        return colName[c];
    }
    public void addElement(String[] Element) {
        rows.add(Element);
        fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }
    public void removeElement(int id) {
        for (int i = 0; i < rows.size(); i++) {
            if (String.valueOf(id).equals(getValueAt(i, 0))) {
                rows.remove(i);
                fireTableRowsDeleted(i, i);
                break;
            }
        }
    }
}
