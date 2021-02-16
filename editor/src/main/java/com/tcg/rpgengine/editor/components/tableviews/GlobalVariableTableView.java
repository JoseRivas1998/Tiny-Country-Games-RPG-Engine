package com.tcg.rpgengine.editor.components.tableviews;

import com.tcg.rpgengine.common.data.system.GlobalVariable;
import com.tcg.rpgengine.common.utils.NumberUtils;

public class GlobalVariableTableView extends KeyValueObjectTableView<GlobalVariable>{
    @Override
    protected String getKeyLabel() {
        return "Name";
    }

    @Override
    protected String getValueLabel() {
        return "Initial Value";
    }

    @Override
    protected String getKey(GlobalVariable rowValue) {
        return rowValue.getName();
    }

    @Override
    protected String getValue(GlobalVariable rowValue) {
        return NumberUtils.toString(rowValue.initialValue);
    }
}
