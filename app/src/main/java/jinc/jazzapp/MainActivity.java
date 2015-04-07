package jinc.jazzapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScaleDecider scaleDecider = new ScaleDecider();
        int[] test = {0,3,10};
        String[] scale = scaleDecider.get_scale('C', ScaleDecider.Accidental.NATURAL,
                                                ScaleDecider.ScaleType.MINOR, test);
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<scale.length; i++) {
            builder.append(scale[i]+",");
        }
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText(builder.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
