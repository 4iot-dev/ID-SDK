package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IdentifierRecord {
    private String handle;
    private List<IdentifierValue> values;

    public IdentifierRecord() {
    }

    public IdentifierRecord(String handle, IdentifierValue[] valuesArray) {
        this.handle = handle;
        if (valuesArray != null) {
            values = new ArrayList<>();
            values.addAll(Arrays.asList(valuesArray));
        } else {
            values = null;
        }
    }

    public IdentifierRecord(String handle, List<IdentifierValue> values) {
        this.handle = handle;
        this.values = values;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public byte[] getHandleBytes() {
        return Util.encodeString(handle);
    }

    public List<IdentifierValue> getValues() {
        return values;
    }

    public void setValues(List<IdentifierValue> values) {
        this.values = values;
    }

    public void setValues(IdentifierValue[] valuesArray) {
        if (valuesArray != null) {
            values = new ArrayList<>();
            values.addAll(Arrays.asList(valuesArray));
        } else {
            values = null;
        }
    }

    public IdentifierValue[] getValuesAsArray() {
        if (values == null) return null;
        IdentifierValue[] result = values.toArray(new IdentifierValue[values.size()]);
        return result;
    }

    public IdentifierValue getValueAtIndex(int index) {
        if (values == null) return null;
        for (IdentifierValue value : values) {
            if (value.getIndex() == index) return value;
        }
        return null;
    }

    public List<IdentifierValue> getValuesOfType(String type) {
        List<IdentifierValue> result = new ArrayList<>();
        if (values == null) return result;
        for (IdentifierValue value : values) {
            byte[] typeBytes = value.getType();
            String valueType = Util.decodeString(typeBytes);
            if (type.equals(valueType)) {
                result.add(value);
            }
        }
        return result;
    }

}