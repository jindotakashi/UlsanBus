package finedev.com.ulsanbus.bus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

import finedev.com.ulsanbus.MainActivity;
import finedev.com.ulsanbus.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusRootFragment extends Fragment {

    public BusRootFragment() { }

    public static BusRootFragment newInstance(Stack<Fragment> stack) {
        BusRootFragment fragment = new BusRootFragment();
        stack.push(new FindBusFragment());
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        bus_root_frame
        View view = inflater.inflate(R.layout.fragment_bus_root, container, false);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.bus_root_frame, ((MainActivity)getActivity()).getTabStack(getString(R.string.find_bus)).lastElement());
        transaction.commit();

        return view;
    }


}
