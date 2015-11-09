package pos.projectcomunicacaosemfio;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

    private String TAG = "projectcomunicacaosemfio";
    private BluetoothAdapter bfAdapter;
    private List<BluetoothDevice> lista;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        bfAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bfAdapter != null){
            lista = new ArrayList<BluetoothDevice>(bfAdapter.getBondedDevices());
            updateLista();
            //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
        }

        if (bfAdapter.isEnabled()){
            Toast.makeText(getApplicationContext(), "Bluetooth está ligado", Toast.LENGTH_LONG).show();
        } else {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 0);
        }
    }

    private void updateLista() {
        List<String> nomes = new ArrayList<String>();
        for (BluetoothDevice device : lista){
            boolean pareado = device.getBondState() == BluetoothDevice.BOND_BONDED;
            nomes.add(device.getName() + " - " + device.getAddress() + " " + (pareado ? " *pareado": ""));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, nomes);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = lista.get(position);
        String msg = device.getName() + " - " + device.getAddress();

        Intent it = new Intent(this, BluetoohClientActivity.class);
        it.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        startActivity(it);
        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK){
            Toast.makeText(getApplicationContext(), "Bluetooth não foi ativado", Toast.LENGTH_LONG).show();
        }
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
