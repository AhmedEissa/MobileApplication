package com.matthewbulat.monitorapplication;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class NFCActivity extends ActionBarActivity {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;
    private Message serial = new Message();
	private Enc_Dec secret = new Enc_Dec();
	private WebSocketClient mWebSocketClient;
	private String hostname;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter != null) {
			runOnUiThread(new Toasting("Read an NFC tag"));
        } else {
        	runOnUiThread(new Toasting("This phone is not NFC enabled."));
        }
		mPendingIntent = PendingIntent.getActivity(this, 0,
	            new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            mIntentFilters = new IntentFilter[] { ndefIntent };
        } catch (Exception e) {
            //Log.e("TagDispatch", e.toString());
        }
 
        mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };
	}

	@Override
    public void onNewIntent(Intent intent) {
 
        String s = null;
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (data != null) {
            try {
            	for (int i = 0; i < data.length; i++) {
                    NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
                    for (int j = 0; j < recs.length; j++) {
                        if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                            Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
                            byte[] payload = recs[j].getPayload();
                            s = new String(payload);
                            s= s.substring(3, s.length());
                            
            }
        }
      }
	}catch(Exception e){
    	
    }}
        hostname=s;
        connectToHost(s);
	}
	
	@Override
    public void onResume() {
        super.onResume();
 
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
    }
	@Override
    public void onPause() {
        super.onPause();
 
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_nfc, container,
					false);
			return rootView;
		}
	}

	public void connectToHost(String localhostname){
		 Database db = new Database(NFCActivity.this);
		 DBInput user = db.TopRow();
		 serial.setRequest("HostAvaibility");
		 serial.setEmail(user.getEmail());
		 serial.setToken(user.getToken());
		 serial.setData(localhostname);
		 Websocket();
		 int readyState = mWebSocketClient.getReadyState();
		 while(readyState!=1){
			try {
				Thread.sleep(100);
				readyState = mWebSocketClient.getReadyState();
				} catch (InterruptedException f) {
					f.printStackTrace();
				} 
		 }
		 mWebSocketClient.send(secret.Encrypt(serial.getBytes(3)));
	}
	
	public void Websocket(){
    	Message serverAddress = new Message();
    	String server = serverAddress.getAddress();
		String fullHostName = "ws://"+server+":8080/MiddleServer/start";
		URI uri = null;
		try {
			
            uri = new URI(fullHostName);
            
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    	mWebSocketClient = new WebSocketClient(uri){
			
			
			@Override
			public void onOpen(ServerHandshake serverHandshake) {//Open Socket
				
			}
			
				@Override
				public void onClose(int i, String s, boolean b) {//close Socket
		             Log.i("Websocket", "Closed " + s);
				}

				@Override
				public void onError(Exception e) {//Error Socket
					Log.d("Websocket", "Error" + e.getMessage());
				}
				
				
				@Override
				public void onMessage(ByteBuffer x) {//message Socket
					byte[]dataFromServer = secret.Decrypt(x.array());
					String req = serial.getRequest(dataFromServer);
					Log.d("Websocket", "num1"+req);
					if(req.equals("HostResponding")){
						mWebSocketClient.close();
						GraphActivity();
					}else if(req.equals("HostNotResponding")){
						runOnUiThread(new Toasting("Unable to connect to host."));
						mWebSocketClient.close();
					}
				}
				@Override
				public void onMessage(String x) {//message Socket
				}
			};
			mWebSocketClient.connect();//establish connection with server
    }
	
	public void GraphActivity(){
    	Intent intent = new Intent(this, GraphActivity.class);
    	intent.putExtra("hostname", this.hostname);
    	startActivity(intent);
    }
class Toasting implements Runnable{
		
		private String data;
		
		public Toasting(String x){
			this.data =x;
		}
		
		public void run() {
			runOnUiThread(new Runnable() {
			
				@Override
				public void run() {
					Context context = getApplicationContext();
					CharSequence text = data;
					int duration = Toast.LENGTH_LONG;
					
					Toast toast = Toast.makeText(context, text, duration);
					toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
					toast.show();
					
				}
				
				});
		}
	}
}





