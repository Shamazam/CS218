package AdminEmergencyContacts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;

public class MapTouchWrapper extends FrameLayout {

    private GoogleMap mMap;
    private OnTouchListener mListener;

    public MapTouchWrapper(Context context) {
        super(context);
    }

    public MapTouchWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapTouchWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setGoogleMap(GoogleMap map) {
        this.mMap = map;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
