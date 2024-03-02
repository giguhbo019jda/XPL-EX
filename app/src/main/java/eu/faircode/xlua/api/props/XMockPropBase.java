package eu.faircode.xlua.api.props;

import androidx.annotation.NonNull;

public class XMockPropBase {
    protected String propertyName;
    protected String packageName;
    protected Integer userId;
    protected String value;

    protected XMockPropBase() { }
    protected XMockPropBase(String propertyName, Integer userId, String packageName, String value) {
        setPropertyName(propertyName);
        setPackageName(packageName);
        setUserId(userId);

        setValue(value);
    }

    public String getValue() { return this.value; }
    public XMockPropBase setValue(String value) {
        if(value != null) this.value = value;
        return this;
    }

    public String getPropertyName() { return this.propertyName; }
    public XMockPropBase setPropertyName(String propertyName) {
        if(propertyName != null) this.propertyName = propertyName;
        return this;
    }

    public Integer getUserId() { return userId; }
    public XMockPropBase setUserId(Integer userId) {
        if(userId != null) this.userId = userId;
        return this;
    }

    public String getPackageName() { return this.packageName; }
    public XMockPropBase setPackageName(String packageName) {
        if(packageName != null) this.packageName = packageName;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(propertyName != null) {
            sb.append(" propertyName=");
            sb.append(propertyName);
        }

        if(packageName != null) {
            sb.append(" packageName=");
            sb.append(packageName);
        }

        if(value != null) {
            sb.append(" value=");
            sb.append(value);
        }

        if(userId != null) {
            sb.append(" user=");
            sb.append(userId);
        }

        return sb.toString();
    }
}
