package eu.faircode.xlua.api.hook.assignment;

import android.os.Parcel;

import eu.faircode.xlua.api.hook.XLuaHook;

public class XLuaAssignmentWriter extends XLuaAssignment {
    public XLuaAssignmentWriter() { }
    public XLuaAssignmentWriter(Parcel p) { super(p); }
    public XLuaAssignmentWriter(XLuaHook hook) { super(hook); }

    public XLuaAssignmentWriter setHook(XLuaHook hook) {
        if(hook != null) this.hook = hook;
        return this;
    }

    public XLuaAssignmentWriter setInstalled(Long installed) {
        if(installed != null) this.installed = installed;
        return this;
    }

    public XLuaAssignmentWriter setUsed(Long used) {
        if(used != null) this.used = used;
        return this;
    }

    public XLuaAssignmentWriter setRestricted(Boolean restricted) {
        if(restricted != null) this.restricted = restricted;
        return this;
    }

    public XLuaAssignmentWriter setException(String exception) {
        if(exception != null) this.exception = exception;
        return this;
    }
}
