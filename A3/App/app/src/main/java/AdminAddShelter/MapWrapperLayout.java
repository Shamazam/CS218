package AdminAddShelter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * A custom layout to handle touch events between a map and surrounding UI components.
 */
public class MapWrapperLayout extends FrameLayout {

    private GoogleMap map;
    private int bottomOffsetPixels;
    private Marker marker;

    public MapWrapperLayout(Context context) {
        super(context);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(GoogleMap map, int bottomOffsetPixels) {
        this.map = map;
        this.bottomOffsetPixels = bottomOffsetPixels;
    }

    public void setMarkerWithOffset(Marker marker, int bottomOffsetPixels) {
        this.marker = marker;
        this.bottomOffsetPixels = bottomOffsetPixels;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (marker != null && map != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
            // Offset the touch coordinates to account for the marker offset
            final int x = (int) ev.getX();
            final int y = (int) ev.getY();
            if (hitTest(x, y)) {
                // Simulate a touch event at the marker location with the offset
                ev.offsetLocation(0, -bottomOffsetPixels);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean hitTest(int x, int y) {
        // Determine if the touch event is within the bounds of the marker
        return marker.getPosition().latitude != 0 && marker.getPosition().longitude != 0;
    }
}
