package com.matthewbulat.monitorapplication;



//imports and everything...

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MainMenu extends ActionBarActivity {
	private WebSocketClient mWebSocketClient;
	private int autoLog=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
    }
    public void Websocket(){
    	Message serverAddress = new Message();
    	String server = serverAddress.getAddress();//"matthewbulat.com";// TODO later get hostname from settings
		String fullHostName = "ws://"+server+":8080/MiddleServer/start";//Converts hostaddress to variable used by web sockets
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
					byte[] data=x.array();
					byte[] dat2 = secret.Decrypt(data);//decrypts the data
					String req = serial.getRequest(dat2);
					//checks what was sent by server, if ok then pass is try, if failed then pass is false and user will have to perform manual login 
					if(req.equals("#*ok*#")){
						autoLog=1;
					}else if(req.equals("#*failed*#")){
						autoLog=2;
					}
						
				}
				@Override
				public void onMessage(String x) {//message Socket
				}
			};
			mWebSocketClient.connect();//establish connection with server
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
            View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
            return rootView;
        }
        
    }
    Message serial = new Message();//Message object
	Enc_Dec secret = new Enc_Dec();//encode/decode object
	Database db = new Database(MainMenu.this);//database object
	AutoLog autolog = null;//empty auto logon object
	
	public void setting(View view){//Method which will call SettingActivity when Setting button was pressed
		Intent intent = new Intent(this, SettingsActivity.class);//creating intent to call SettingActivity class
   	 startActivity(intent);//calling activity
		
	}
	//closes this activity/ but do not terminate the application, it can be done from task manager
	public void exit(View view){
		MainMenu.this.finish();
	}
	//Method which is activated after connect button was pressed, it will either autolog in user, or asks user to manually login when users token is incorrect or missing
    public void connect(View view){
    	
    	ServerDatabase server = new ServerDatabase(MainMenu.this);
        Message data = new Message();
        int serverCount=server.getCount();
        if(serverCount==0){
        	runOnUiThread(new Toasting("Please enter Server address, in settings.",1));
        	Intent intent = new Intent(this, SettingsActivity.class);
    		startActivity(intent);
        }else{
        	data.setAddress(server.TopRow());
        	int userCount= db.getCount();
        	Intent intent = new Intent(this, LoginActivity.class);
        	if(userCount==0){
        		startActivity(intent);
        	}else if(userCount==1){
        		if(db.TopRow().getToken()!=null){
        			Websocket();
        			autolog = new AutoLog();
            		autolog.execute((Void) null);
        		}else{
        			startActivity(intent);
        		}
        	}
        }
    	//android DB should only hold one record, if there are no records that means user will have to login manually, if there are records user will be auto login
    }
    //class which purpose is to auto login user
    class AutoLog extends AsyncTask<Void, Void, Boolean>{
		@Override
		//Background process to reduce amount of calculation on the main thread
		protected Boolean doInBackground(Void... params) {
			//this line will pick up server address that is being stored in database, which will be used later on by websocket class to send data to middleserver
	        
			Boolean pass=false;//variable used to define if auto login was successful or not
			int ready=0;
			DBInput user = db.TopRow();//importing user credentials, that is username and password
    		int noOfTries_Ready=0;
    		//tries 10 times to start the connection, if not user will be informed by using a toast
    		int sleepTime=0;
    		while(noOfTries_Ready!=9 && ready!=1){
				try {
					ready = mWebSocketClient.getReadyState();
					if(ready==3){
						Websocket();
						ready = mWebSocketClient.getReadyState();
					}
					sleepTime+=250;
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				noOfTries_Ready++;
			}
    		//Toast which lets user know that there is an issue with his connection
    		if(noOfTries_Ready==9){
				runOnUiThread(new Toasting("Time Out, please check your network connection, or Loggin server address/name, and try again",1));
			}
    		//if everything is fine and user is connected to the server, it will try to auto login
			else{
				serial.setRequest("autolog");//object which will hold the data
				serial.setEmail(user.getEmail());//object which will hold the data
				serial.setToken(user.getToken());//object which will hold the data
				mWebSocketClient.send(secret.Encrypt(serial.getBytes(1)));
				while(autoLog==0){
				try {
					Thread.sleep(100);//gives 0.1 seconds for server to reply
				} catch (InterruptedException e) {
					e.printStackTrace();
					}
				}
				if(autoLog==1){
					pass=true;
				}else if(autoLog==2){
					pass=false;
				}
				//cleans up the data from server
			}
    		return pass;
		}
		
		@Override
		//post execute class which will either allow user to access the system, or make him perform manual login
		protected void onPostExecute(final Boolean success) {
			autolog = null;

			if (success) {
				mWebSocketClient.close();
				HostChoiceActivity();
			} else {
				LoginActivity();
			}
		}
		
		}
  //method which calls host choice activity
    public void HostChoiceActivity(){
    	Intent intent = new Intent(this, HostChoiceActivity.class);
		startActivity(intent);
    }
    //method which calls login activity
    public void LoginActivity(){
    	Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
    }
    public String serverName(){
    	ServerDatabase dbs = new ServerDatabase(MainMenu.this);
    	String server = dbs.TopRow();
		return server;
    	
    }
    
    //toast activity which will perform toasts on the screen
class Toasting implements Runnable{
		
		private String data;
		private int toastLength;
		
		public Toasting(String x, int y){
			this.data =x;
			this.toastLength=y;
		}
		
		public void run() {
			runOnUiThread(new Runnable() {
			
				@Override
				public void run() {
					Context context = getApplicationContext();
					CharSequence text = data;
					int duration=Toast.LENGTH_LONG;
					switch(toastLength){
					case 1:
						duration = Toast.LENGTH_LONG;
						break;
					case 2:
						duration = Toast.LENGTH_SHORT;
						break;
					default:
						duration = Toast.LENGTH_SHORT;
						break;
					}
					
					
					Toast toast = Toast.makeText(context, text, duration);
					toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
					toast.show();
					
				}
				
				});
		}
	}
}
