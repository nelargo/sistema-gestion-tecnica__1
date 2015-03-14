package cl.inexcell.sistemadegestion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class planta_ext1 extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return (LinearLayout) inflater.inflate(R.layout.planta_ext1_layout, container, false);
	}
}
