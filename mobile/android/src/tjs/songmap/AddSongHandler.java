package tjs.songmap;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class AddSongHandler implements OnClickListener {

	@Override
	public void onClick(View v) {
		Context context = v.getContext();
		Toast addingSongText = Toast.makeText(context, R.string.addingSong, Toast.LENGTH_SHORT);
		addingSongText.show();
		
		// get the last known position of the user
		LocationManager locManager = (LocationManager) v.getContext().getSystemService(Context.LOCATION_SERVICE);
		Location lastKnown = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		String latLngStr = "Latitude: "+lastKnown.getLatitude()+" Longitude: "+lastKnown.getLongitude();
		Toast latLngText = Toast.makeText(context, latLngStr, Toast.LENGTH_SHORT);
		latLngText.show();
		
	}

}
