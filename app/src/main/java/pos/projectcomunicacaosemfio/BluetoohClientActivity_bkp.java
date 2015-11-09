package pos.projectcomunicacaosemfio;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * Created by helio on 08/11/15.
 */
public class BluetoohClientActivity_bkp extends Activity {
    private static final String TAG = "PosComunicacaoSemFio";
    private static final UUID uuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private BluetoothDevice device;
    private TextView tMsg;
    private TextView tv_velocidade;
    private TextView tv_temperatura;
    private TextView tv_rotacao;
    private OutputStream out;
    private OutputStream out5;
    private BluetoothSocket socket;
    private BluetoothSocketWrapper bluetoothSocket;
    private int candidate;
    private List<UUID> uuidCandidates;
    private boolean secure;
    private BluetoothAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_msg);

        tMsg = (TextView) findViewById(R.id.tMsg);

        device = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        TextView tNome = (TextView) findViewById(R.id.tNomeDevice);
        tNome.setText(device.getName() + " - " + device.getAddress());

        findViewById(R.id.btConectar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    socket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                    connect();

                    out = bluetoothSocket.getOutputStream();
                    out.write("AT E0\r".getBytes());
                    out.write("AT L0\r".getBytes());
                    out.write("AT ST 00\r".getBytes());
                    out.write("AT SP 0\r".getBytes());
                    out.write("AT Z\r".getBytes());

                    // Se chegou aqui È porque conectou
                    if(out != null) {
                        // Habilita o bot„o para enviar mensagens
                        findViewById(R.id.btConectar).setEnabled(false);
                        findViewById(R.id.btEnviar).setEnabled(true);

                        checkAvailable();
                    }

                } catch (IOException e) {

                    Log.e(TAG, "Erro ao conectar: " + e.getMessage(), e);
                }
            }
        });
    }

    private void checkAvailable() {

        /*final Handler mHandler = new Handler();
        new Runnable(){
            @Override
            public void run() {
                try {

                    out = bluetoothSocket.getOutputStream();
                    //InputStream stream = bluetoothSocket.getInputStream();
                    if (out != null) {
                        out.write("01 05\r".getBytes()); // Temperatura
                    }

                    //Thread.sleep(3000);

                    InputStream mmInStream = bluetoothSocket.getInputStream();
                    byte[] readBuffer = new byte[8];
                    mmInStream.read(readBuffer);


                    String deucerto = fromStream(mmInStream);

                    if (deucerto.substring(0,5).equals("41 05")) { // Temperatura do motor
                        tv_temperatura = (TextView) findViewById(R.id.tv_temperatura);
                        tv_temperatura.setText("Temperatura: "+deucerto);
                    }

                } catch (Exception e1) {
                    Log.i("TESTEEEEEEE", e1.getMessage());
                }
                mHandler.postDelayed(this, 2000);
            }
        }.run();

        final Handler mHandler3 = new Handler();
        new Runnable(){
            @Override
            public void run() {
                try {

                    out = bluetoothSocket.getOutputStream();
                    //InputStream stream = bluetoothSocket.getInputStream();
                    if (out != null) {
                        out.write("01 0D\r".getBytes()); // Velocidade
                    }

                    //Thread.sleep(3000);

                    InputStream mmInStream = bluetoothSocket.getInputStream();
                    byte[] readBuffer = new byte[8];
                    mmInStream.read(readBuffer);


                    String deucerto = fromStream(mmInStream);

                    if (deucerto.substring(0,5).equals("41 0D")) { // Temperatura do motor
                        tv_velocidade = (TextView) findViewById(R.id.tv_velocidade);
                        tv_velocidade.setText("Velocidade: "+deucerto);
                    }

                } catch (Exception e1) {
                    Log.i("TESTEEEEEEE", e1.getMessage());
                }
                mHandler3.postDelayed(this, 2000);
            }
        }.run();*/

        final Handler mHandler2 = new Handler();
        new Runnable(){
            @Override
            public void run() {
                try {

                    out = bluetoothSocket.getOutputStream();
                    out5 = bluetoothSocket.getOutputStream();
                    //InputStream stream = bluetoothSocket.getInputStream();
                    if (out != null) {
                        out.write("01 0C\r".getBytes()); // Rotacao
                        Thread.sleep(200);
                        out.write("01 0D\r".getBytes()); // velocidade
                        Thread.sleep(200);
                        out.write("01 05\r".getBytes()); // temperatura
                        Thread.sleep(200);
                    }

                    //Thread.sleep(3000);

                    InputStream mmInStream = bluetoothSocket.getInputStream();
                    byte[] readBuffer = new byte[8];
                    mmInStream.read(readBuffer);


                    String deucerto = fromStream(mmInStream);

                    if (deucerto.substring(0,5).equals("41 0C")) { // Rotação do motor
                        tv_rotacao = (TextView) findViewById(R.id.tv_rotacao);
                        String numero = deucerto.substring(6,8);
                        String numero2 = deucerto.substring(9,11);
                        tv_rotacao.setText("Rotação: "+(convertHexa(numero)*256 + convertHexa(numero2))/4);
                    }
                    if (deucerto.substring(0,5).equals("41 0D")) { // Velocidade
                        tv_velocidade = (TextView) findViewById(R.id.tv_velocidade);
                        tv_velocidade.setText("Velocidade: "+deucerto);
                    }

                } catch (Exception e1) {
                    Log.i("TESTEEEEEEE", e1.getMessage());
                }
                mHandler2.postDelayed(this, 2000);
            }
        }.run();

    }

    public static long convertHexa(String text) {
        if (text.length() == 16) {
            return (convertHexa(text.substring(0, 1)) << 60)
                    | convertHexa(text.substring(1));
        }
        return Long.parseLong(text, 16);
    }

    public static String fromStream(InputStream in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out2 = new StringBuilder();
        //String newLine = System.getProperty("line.separator");
        String line = "";
        /*while ((line = reader.readLine()) != null) {
            out2.append(line);
            //out2.append(newLine);
        }*/
        if ((line = reader.readLine()) != null){
            out2.append(line);
        }
        return out2.toString();
    }

    private boolean selectSocket() throws IOException {
        /*if (candidate >= uuidCandidates.size()) {
            return false;
        }*/

        BluetoothSocket tmp;
        //UUID uuid = uuidCandidates.get(candidate++);
        UUID uuid = device.getUuids()[0].getUuid();

        Log.i("BT", "Attempting to connect to Protocol: "+ uuid);
        if (secure) {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } else {
            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
        }
        bluetoothSocket = new NativeBluetoothSocket(tmp);

        return true;
    }

    public BluetoothSocketWrapper connect() throws IOException {
        boolean success = false;
        while (selectSocket()) {
            //adapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
                success = true;
                break;
            } catch (IOException e) {
                //try the fallback
                try {
                    bluetoothSocket = new FallbackBluetoothSocket(bluetoothSocket.getUnderlyingSocket());
                    Thread.sleep(500);
                    bluetoothSocket.connect();
                    success = true;
                    break;
                } catch (FallbackException e1) {
                    Log.w("BT", "Could not initialize FallbackBluetoothSocket classes.", e);
                } catch (InterruptedException e1) {
                    Log.w("BT", e1.getMessage(), e1);
                } catch (IOException e1) {
                    Log.w("BT", "Fallback failed. Cancelling.", e1);
                }
            }
        }

        if (!success) {
            throw new IOException("Could not connect to device: "+ device.getAddress());
        }

        return bluetoothSocket;
    }

    public static interface BluetoothSocketWrapper {

        InputStream getInputStream() throws IOException;

        OutputStream getOutputStream() throws IOException;

        String getRemoteDeviceName();

        void connect() throws IOException;

        String getRemoteDeviceAddress();

        void close() throws IOException;

        BluetoothSocket getUnderlyingSocket();

    }

    public static class NativeBluetoothSocket implements BluetoothSocketWrapper {

        private BluetoothSocket socket;

        public NativeBluetoothSocket(BluetoothSocket tmp) {
            this.socket = tmp;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        @Override
        public String getRemoteDeviceName() {
            return socket.getRemoteDevice().getName();
        }

        @Override
        public void connect() throws IOException {
            socket.connect();
        }

        @Override
        public String getRemoteDeviceAddress() {
            return socket.getRemoteDevice().getAddress();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }

        @Override
        public BluetoothSocket getUnderlyingSocket() {
            return socket;
        }

    }

    public static class FallbackException extends Exception {

        private static final long serialVersionUID = 1L;

        public FallbackException(Exception e) {
            super(e);
        }

    }

    public class FallbackBluetoothSocket extends NativeBluetoothSocket {

        private BluetoothSocket fallbackSocket;

        public FallbackBluetoothSocket(BluetoothSocket tmp) throws FallbackException {
            super(tmp);
            try
            {
                Class<?> clazz = tmp.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[] {Integer.valueOf(1)};
                fallbackSocket = (BluetoothSocket) m.invoke(tmp.getRemoteDevice(), params);
            }
            catch (Exception e)
            {
                throw new FallbackException(e);
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fallbackSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return fallbackSocket.getOutputStream();
        }


        @Override
        public void connect() throws IOException {
            fallbackSocket.connect();
        }


        @Override
        public void close() throws IOException {
            fallbackSocket.close();
        }

    }
}
