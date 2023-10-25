package com.neptunedreams.framework.ui;

import java.awt.Component;
import java.math.BigDecimal;
import java.text.NumberFormat;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/24/23
 * <p>Time: 10:03 PM
 *
 * @author Miguel Muñoz
 */
public class OverrideBug extends DefaultListCellRenderer {
    private static final NumberFormat format = NumberFormat.getCurrencyInstance();
    //
    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        if (value instanceof Double) {
            value = format.format((Double)value); 
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
