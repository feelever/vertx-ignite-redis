package distribute.cache.ignite;

import java.io.Serializable;

public class DataClass implements Serializable {
    private String name;
    private long value;
    private String strValue;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getValue() {
        return value;
    }
    public void setValue(long value) {
        this.value = value;
    }
    public String getStrValue() {
        return strValue;
    }
    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }
}