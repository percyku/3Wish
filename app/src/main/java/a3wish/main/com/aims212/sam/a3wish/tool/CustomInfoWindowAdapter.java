package a3wish.main.com.aims212.sam.a3wish.tool;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import a3wish.main.com.aims212.sam.a3wish.R;

/**
 * Created by percyku on 2017/8/25.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public CustomInfoWindowAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.customwindow, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(marker.getTitle());
        return view;
    }
}
