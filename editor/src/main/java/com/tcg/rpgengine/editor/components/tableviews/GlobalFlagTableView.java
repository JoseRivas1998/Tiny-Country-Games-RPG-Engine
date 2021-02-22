package com.tcg.rpgengine.editor.components.tableviews;

import com.tcg.rpgengine.common.data.system.GlobalFlag;

public class GlobalFlagTableView extends KeyValueObjectTableView<GlobalFlag> {
    @Override
    protected String getKeyLabel() {
        return "Name";
    }

    @Override
    protected String getValueLabel() {
        return "Initial Value";
    }

    @Override
    protected String getKey(GlobalFlag globalFlag) {
        return globalFlag.getName();
    }

    @Override
    protected String getValue(GlobalFlag globalFlag) {
        return Boolean.toString(globalFlag.initialValue);
    }
}
