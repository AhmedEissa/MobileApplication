package com.matthewbulat.monitorapplication;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GraphActivity extends ActionBarActivity {
	private String LocalHostAddress;
	private GraphViewSeries CPU;
	private GraphViewSeries RAM;
	private GraphViewSeries NETin;
	private GraphViewSeries NETout;
	private final Handler mHandler = new Handler();
	private Runnable mTimer1;
	private ConcurrentLinkedQueue<String[]> queue = new ConcurrentLinkedQueue<String[]>();
	private Message serial = new Message();
	private Enc_Dec secret = new Enc_Dec();
	private WebSocketClient mWebSocketClient;
	private int[] value=new int[16];
	private int[] value1=new int[16];
	private int[] value2=new int[16];
	private int[] value3=new int[16];
	private int yAxis=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		this.LocalHostAddress = intent.getStringExtra("hostname");
		setContentView(R.layout.activity_graph);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			
		}
		
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
			
			View rootView = inflater.inflate(R.layout.fragment_graph,
					container, false);
			
			return rootView;
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
					Log.d("Websocket", "Error " + e.getMessage());
				}
				@Override
				public void onMessage(ByteBuffer x) {//message Socket
					byte[]dataFromServer = secret.Decrypt(x.array());
					String req = serial.getRequest(dataFromServer);
					if(req.equals("dataFromLocalServer")){
						String[] data1 = serial.getLocalServerData(dataFromServer);
						queue.add(data1);
						//setServerData(data1);
						
					}
				}
				@Override
				public void onMessage(String x) {//message Socket
				}
			};
			mWebSocketClient.connect();//establish connection with server
    }
	public void connectToHost(){
		 Database db = new Database(GraphActivity.this);
		 DBInput user = db.TopRow();
		 serial.setRequest("hostData");
		 serial.setEmail(user.getEmail());
		 serial.setToken(user.getToken());
		 serial.setData(this.LocalHostAddress);
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
	
	public void onStart(){
		super.onStart();
		setTitle("Utilization graph for: "+LocalHostAddress);
		connectToHost();
		CPU = new GraphViewSeries(new GraphViewData[] {
			     new GraphViewData(1, 0)
			    ,new GraphViewData(2, 0)
			    ,new GraphViewData(3, 0)
			    ,new GraphViewData(4, 0)
			    ,new GraphViewData(5, 0)
				,new GraphViewData(6, 0)
				,new GraphViewData(7, 0)
				,new GraphViewData(8, 0)
				,new GraphViewData(9, 0)
				,new GraphViewData(10, 0)
				,new GraphViewData(11, 0)
				,new GraphViewData(12, 0)
				,new GraphViewData(13, 0)
				,new GraphViewData(14, 0)
				,new GraphViewData(15, 0)
			     
			}
		);
		GraphView graphView = new LineGraphView(
			    this // context
			    , "" // heading
			);
		graphView.setCustomLabelFormatter(new CustomLabelFormatter(){

			@Override
			public String formatLabel(double value, boolean isValueY) {
				if(isValueY){
					
					return null;
				}
				int intValue=(int)value;
			return intValue+"%";
			}
			
		});
		RAM = new GraphViewSeries(new GraphViewData[] {
			     new GraphViewData(1, 0)
			    ,new GraphViewData(2, 0)
			    ,new GraphViewData(3, 0)
			    ,new GraphViewData(4, 0)
			    ,new GraphViewData(5, 0)
				,new GraphViewData(6, 0)
				,new GraphViewData(7, 0)
				,new GraphViewData(8, 0)
				,new GraphViewData(9, 0)
				,new GraphViewData(10, 0)
				,new GraphViewData(11, 0)
				,new GraphViewData(12, 0)
				,new GraphViewData(13, 0)
				,new GraphViewData(14, 0)
				,new GraphViewData(15, 0)
			});
		GraphView graphView1 = new LineGraphView(
			    this // context
			    , "" // heading
			);
		graphView1.setCustomLabelFormatter(new CustomLabelFormatter(){

			@Override
			public String formatLabel(double value, boolean isValueY) {
				if(isValueY){
					
					return null;
				}
				int intValue=(int)value;
				return intValue+"MB";
			}
		});
		NETin = new GraphViewSeries(new GraphViewData[] {
			     new GraphViewData(1, 0)
			    ,new GraphViewData(2, 0)
			    ,new GraphViewData(3, 0)
			    ,new GraphViewData(4, 0)
			    ,new GraphViewData(5, 0)
				,new GraphViewData(6, 0)
				,new GraphViewData(7, 0)
				,new GraphViewData(8, 0)
				,new GraphViewData(9, 0)
				,new GraphViewData(10, 0)
				,new GraphViewData(11, 0)
				,new GraphViewData(12, 0)
				,new GraphViewData(13, 0)
				,new GraphViewData(14, 0)
				,new GraphViewData(15, 0)
			});
		GraphView graphView2 = new LineGraphView(
			    this // context
			    , "" // heading
			);
		graphView2.setCustomLabelFormatter(new CustomLabelFormatter(){

			@Override
			public String formatLabel(double value, boolean isValueY) {
				if(isValueY){
					
					return null;
				}
				int intValue=(int)value;
				return intValue+"Kbps";
			}
		});
		NETout = new GraphViewSeries(new GraphViewData[] {
			     new GraphViewData(1, 0)
			    ,new GraphViewData(2, 0)
			    ,new GraphViewData(3, 0)
			    ,new GraphViewData(4, 0)
			    ,new GraphViewData(5, 0)
				,new GraphViewData(6, 0)
				,new GraphViewData(7, 0)
				,new GraphViewData(8, 0)
				,new GraphViewData(9, 0)
				,new GraphViewData(10, 0)
				,new GraphViewData(11, 0)
				,new GraphViewData(12, 0)
				,new GraphViewData(13, 0)
				,new GraphViewData(14, 0)
				,new GraphViewData(15, 0)
			});
		GraphView graphView3 = new LineGraphView(
			    this // context
			    , "" // heading
			);
		graphView3.setCustomLabelFormatter(new CustomLabelFormatter(){

			@Override
			public String formatLabel(double value, boolean isValueY) {
				if(isValueY){
					
					return null;
				}
				int intValue=(int)value;
				return intValue+"Kbps";
			}
		});
		graphView.addSeries(CPU);
		graphView1.addSeries(RAM);
		graphView2.addSeries(NETin);
		graphView3.addSeries(NETout);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.graph1);
		layout.addView(graphView);
		RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.graph2);
		layout1.addView(graphView1);
		RelativeLayout layout2 = (RelativeLayout) findViewById(R.id.graph3);
		layout2.addView(graphView2);
		RelativeLayout layout3 = (RelativeLayout) findViewById(R.id.graph4);
		layout3.addView(graphView3);
	}
	@Override
	protected void onPause() {
	mHandler.removeCallbacks(mTimer1);
	super.onPause();
	if (!this.isFinishing()){
		 Database db = new Database(GraphActivity.this);
		 DBInput user = db.TopRow();
		 serial.setRequest("stopConnection");
		 serial.setEmail(user.getEmail());
		 serial.setToken(user.getToken());
		 serial.setData(this.LocalHostAddress);
		mWebSocketClient.send(secret.Encrypt(serial.getBytes(3)));
		this.finish();
	}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK))
	    {
	    	Database db = new Database(GraphActivity.this);
			DBInput user = db.TopRow();
			serial.setRequest("stopConnection");
			serial.setEmail(user.getEmail());
			serial.setToken(user.getToken());
			serial.setData(this.LocalHostAddress);
			mWebSocketClient.send(secret.Encrypt(serial.getBytes(3)));
	        this.finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	@Override
	protected void onResume() {
	super.onResume();
	
	mTimer1 = new Runnable() {
		int cpuInt=0;
		int ramInt=0;
		int[]netIn=new int[3];
		int[]netOut=new int[3];
		int count=0;
		String[] data={"0","0","0","0","0"};
		public void run() {
			
			if(queue.isEmpty()){
				
			}else{
			data=queue.poll();
			}
			for(int i=0;i<=15;i++){
				if(i!=15){
					value[i]=value[i+1];
					value1[i]=value1[i+1];
					value2[i]=value2[i+1];
					value3[i]=value3[i+1];
				}else{
					cpuInt = Integer.parseInt(data[1]);
					value[i]=cpuInt;
					ramInt = Integer.parseInt(data[2]);
					value1[i]=ramInt;
					if(count==0||count==1||count==2){
						count++;
						netIn[1] = Integer.parseInt(data[3]);
						netOut[1] = Integer.parseInt(data[4]);
					}else{
						netIn[2]= Integer.parseInt(data[3]);
						value2[i]= (netIn[2]-netIn[1]);
						netIn[1]=netIn[2];
						netOut[2]= Integer.parseInt(data[4]);
						value3[i]= (netOut[2]-netOut[1]);
						netOut[1]=netOut[2];
					}
				}
			}
			yAxis=yAxis+1;
			CPU.resetData(
					new GraphViewData[] {
					 new GraphViewData(yAxis, value[0])
				    ,new GraphViewData(yAxis+1, value[1])
				    ,new GraphViewData(yAxis+2, value[2])
				    ,new GraphViewData(yAxis+3, value[3])
					,new GraphViewData(yAxis+4, value[4])
					,new GraphViewData(yAxis+5, value[5])
					,new GraphViewData(yAxis+6, value[6])
					,new GraphViewData(yAxis+7, value[7])
					,new GraphViewData(yAxis+8, value[8])
					,new GraphViewData(yAxis+9, value[9])
					,new GraphViewData(yAxis+10, value[10])
					,new GraphViewData(yAxis+11, value[11])
					,new GraphViewData(yAxis+12, value[12])
					,new GraphViewData(yAxis+13, value[13])
					,new GraphViewData(yAxis+14, value[14])
					,new GraphViewData(yAxis+15, value[15])
			});
			RAM.resetData(
					new GraphViewData[] {
					 new GraphViewData(yAxis, value1[0])
				    ,new GraphViewData(yAxis+1, value1[1])
				    ,new GraphViewData(yAxis+2, value1[2])
				    ,new GraphViewData(yAxis+3, value1[3])
					,new GraphViewData(yAxis+4, value1[4])
					,new GraphViewData(yAxis+5, value1[5])
					,new GraphViewData(yAxis+6, value1[6])
					,new GraphViewData(yAxis+7, value1[7])
					,new GraphViewData(yAxis+8, value1[8])
					,new GraphViewData(yAxis+9, value1[9])
					,new GraphViewData(yAxis+10, value1[10])
					,new GraphViewData(yAxis+11, value1[11])
					,new GraphViewData(yAxis+12, value1[12])
					,new GraphViewData(yAxis+13, value1[13])
					,new GraphViewData(yAxis+14, value1[14])
					,new GraphViewData(yAxis+15, value1[15])
			});
			NETin.resetData(
					new GraphViewData[] {
					 new GraphViewData(yAxis, value2[0])
				    ,new GraphViewData(yAxis+1, value2[1])
				    ,new GraphViewData(yAxis+2, value2[2])
				    ,new GraphViewData(yAxis+3, value2[3])
					,new GraphViewData(yAxis+4, value2[4])
					,new GraphViewData(yAxis+5, value2[5])
					,new GraphViewData(yAxis+6, value2[6])
					,new GraphViewData(yAxis+7, value2[7])
					,new GraphViewData(yAxis+8, value2[8])
					,new GraphViewData(yAxis+9, value2[9])
					,new GraphViewData(yAxis+10, value2[10])
					,new GraphViewData(yAxis+11, value2[11])
					,new GraphViewData(yAxis+12, value2[12])
					,new GraphViewData(yAxis+13, value2[13])
					,new GraphViewData(yAxis+14, value2[14])
					,new GraphViewData(yAxis+15, value2[15])
			});
			NETout.resetData(
					new GraphViewData[] {
					 new GraphViewData(yAxis, value3[0])
				    ,new GraphViewData(yAxis+1, value3[1])
				    ,new GraphViewData(yAxis+2, value3[2])
				    ,new GraphViewData(yAxis+3, value3[3])
					,new GraphViewData(yAxis+4, value3[4])
					,new GraphViewData(yAxis+5, value3[5])
					,new GraphViewData(yAxis+6, value3[6])
					,new GraphViewData(yAxis+7, value3[7])
					,new GraphViewData(yAxis+8, value3[8])
					,new GraphViewData(yAxis+9, value3[9])
					,new GraphViewData(yAxis+10, value3[10])
					,new GraphViewData(yAxis+11, value3[11])
					,new GraphViewData(yAxis+12, value3[12])
					,new GraphViewData(yAxis+13, value3[13])
					,new GraphViewData(yAxis+14, value3[14])
					,new GraphViewData(yAxis+15, value3[15])
			});
			mHandler.postDelayed(this, 1000);
		}
	};
	mHandler.postDelayed(mTimer1, 0);
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
					int duration = Toast.LENGTH_SHORT;
					
					Toast toast = Toast.makeText(context, text, duration);
					toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
					toast.show();
					
				}
				
				});
		}
	}
	
}

