package ncku.appt3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class history_re extends AppCompatActivity{

    private Button historyBack;

    private ArrayList<object_id> rentalProperties = new ArrayList<>();
    MyDBHandler_re dbHandler;

    int i;
    int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_re);

        //create our new array adapter
        dbHandler = new MyDBHandler_re(this, null, null, 1);
        rentalProperties=  dbHandler.databaseToString_id();
        ArrayAdapter<object_id> adapter = new propertyArrayAdapter(this, 0, rentalProperties);

        //Find list view and bind it with the custom adapter
        ListView listView = (ListView) findViewById(R.id.historyList);
        listView.setAdapter(adapter);

        //add event listener so we can handle clicks
        AdapterView.OnItemClickListener adapterViewListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                object_id property=rentalProperties.get(position);
                String da=property.get_date();
                String va=property.get_value();
                String in=Integer.toString(property.get_id());

                Intent intent = new Intent(history_re.this,detail.class);
                intent.putExtra("date",da);
                intent.putExtra("int",in);
                intent.putExtra("value",va);
                startActivity(intent);
            }
        };

        //set the listener to the list view
        listView.setOnItemClickListener(adapterViewListener);

    }

    class propertyArrayAdapter extends ArrayAdapter<object_id>{
        private Context context;
        private List<object_id> rentalProperties;

        //constructor, call on creation
        public propertyArrayAdapter(Context context, int resource, ArrayList<object_id> objects) {
            super(context, resource, objects);

            this.context = context;
            this.rentalProperties = objects;
        }

        //called when rendering the list
        public View getView(int position, View convertView, ViewGroup parent) {
            //get the property we are displaying
            object_id property = rentalProperties.get(position);

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.custom_row, null);

            TextView text_date = (TextView) view.findViewById(R.id.text_date);
            TextView text_value = (TextView) view.findViewById(R.id.text_value);

            //set text_date
            text_date.setText("Date : "+property.get_value());

            //display trimmed excerpt for value

            int descriptionLength = property.get_value().length();
            if(descriptionLength >= 100){
                String Trim = property.get_value().substring(0, 100) + "...";
                text_value.setText("Value : "+Trim);
            }else{
                text_value.setText("Value : "+property.get_date());
            }

            return view;
        }

    }

    public void backtoHome(View v){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

}
