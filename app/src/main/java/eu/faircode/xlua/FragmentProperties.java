package eu.faircode.xlua;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Objects;

public class FragmentProperties extends Fragment {
    private final static String TAG = "XLua.FragmentProperties";

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View main = inflater.inflate(R.layout.proprecyclerview, container, false);

        progressBar = main.findViewById(R.id.pbProperties);
        initRefresh(main);
        initRecyclerView(main);
    }

    private void initRefresh(final View view) {
        int colorAccent = XUtil.resolveColor(Objects.requireNonNull(getContext()), R.attr.colorAccent);
        swipeRefresh = view.findViewById(R.id.swipeRefreshProperties);
        swipeRefresh.setColorSchemeColors(colorAccent, colorAccent, colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    private void initRecyclerView(final View view) {
        RecyclerView rvProps = view.findViewById(R.id.rvProperties);
        rvProps.setVisibility(View.VISIBLE);
        rvProps.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
                return true;
            }
        };
    }
}
