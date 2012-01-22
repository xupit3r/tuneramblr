package tjs.songmap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class SongmapmobileActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // setup our button
        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new AddSongHandler());
    }
}