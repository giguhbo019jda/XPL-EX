package eu.faircode.xlua.ui;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public interface ILoader {
    void loadData();
    void filter(String query);
    FragmentManager getManager();
    Fragment getFragment();
}
