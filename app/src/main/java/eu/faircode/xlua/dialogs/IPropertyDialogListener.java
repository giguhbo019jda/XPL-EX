package eu.faircode.xlua.dialogs;

import eu.faircode.xlua.api.properties.MockPropPacket;
import eu.faircode.xlua.api.properties.MockPropSetting;

public interface IPropertyDialogListener {
    void pushMockPropPacket(MockPropPacket setting);
}
