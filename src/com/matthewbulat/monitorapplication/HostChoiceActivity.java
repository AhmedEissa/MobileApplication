package com.matthewbulat.monitorapplication;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class HostChoiceActivity extends ActionBarActivity {
	
	private ProgressBar spinner;
	protected static String EXTRA_MESSAGE = null;
	private String localhostName;
	private boolean clicked=false;
	private Message serial = new Message();
	private Enc_Dec secret = new Enc_Dec();
	private WebSocketClient mWebSocketClient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_choice);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	
	public void Websocket(){
	    	Message serverAddress = new Message();
	    	String server = serverAddress.getAddress();
			String fullHostName = "ws://"+server+":8080/MiddleServer/start";//Converts host address to variable used by web sockets
			URI uri = null;
			try {
				
	            uri = new URI(fullHostName);//assigns FullHostName as URI
	            
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
							GraphActivity();
						}else if(req.equals("HostNotResponding")){
							runOnUiThread(new Runnable() {
					    	     @Override
					    	     public void run() {
					    	    	 spinner = (ProgressBar)findViewById(R.id.progressBar1);
					    	    	 spinner.setVisibility(View.GONE);	

					    	    }
					    	});
							runOnUiThread(new Toasting("Unable to connect to host."));
							clicked=false;
						}
					}
					@Override
					public void onMessage(String x) {//message Socket
					}
				};
				mWebSocketClient.connect();//establish connection with server
	    }
	
	public void connectToHost(View view){
			if(!clicked){
					clicked=true;
					EditText serverEditText = (EditText) findViewById(R.id.editText_manual_address);
				 	this.localhostName = serverEditText.getText().toString();
				 if(this.localhostName.length()==0){
					 runOnUiThread(new Toasting("Please enter host address before connecting."));
				 }else{
					 runOnUiThread(new Runnable() {
			    	     @Override
			    	     public void run() {
			    	    	 spinner = (ProgressBar)findViewById(R.id.progressBar1);
			    	    	 spinner.setVisibility(View.VISIBLE);	

			    	    }
			    	});
					 testHost(); 
				 }
			}
		}
	
	public void connectToNFCHost(View view){
		Intent intent = new Intent(this, NFCActivity.class);
		startActivity(intent);
	}
	public void connectToQRHost(View view){
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		scanIntegrator.initiateScan();
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			this.localhostName = scanningResult.getContents();
			testHost(); 
		}
	 public void testHost(){
		 Database db = new Database(HostChoiceActivity.this);
		 DBInput user = db.TopRow();
		 serial.setRequest("HostAvailability");
		 serial.setEmail(user.getEmail());
		 serial.setToken(user.getToken());
		 serial.setData(this.localhostName);
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
	 public void GraphActivity(){
	    	Intent intent = new Intent(this, GraphActivity.class);
	    	intent.putExtra("hostname", this.localhostName);
	    	mWebSocketClient.close();
	    	clicked=false;
	    	runOnUiThread(new Runnable() {
	    	     @Override
	    	     public void run() {
	    	    	 spinner = (ProgressBar)findViewById(R.id.progressBar1);
	    	    	 spinner.setVisibility(View.INVISIBLE);	

	    	    }
	    	});
	    	startActivity(intent);
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
			View rootView = inflater.inflate(R.layout.fragment_host_choice,
					container, false);
			return rootView;
		}
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
