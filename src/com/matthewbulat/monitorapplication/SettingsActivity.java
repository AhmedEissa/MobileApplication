package com.matthewbulat.monitorapplication;





import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends ActionBarActivity {
	private Database db = new Database(SettingsActivity.this);
	private ServerDatabase dbs = new ServerDatabase(SettingsActivity.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
			
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
					
		}
		
		
		
	}
	protected void onStart(){
		super.onStart();
		int size = dbs.getCount();
		if(size==1){
			setFields();
		}
		
	}
	public void setFields(){
		String server = dbs.TopRow();
		EditText serverEditText = (EditText) findViewById(R.id.editText_server);
		serverEditText.setText(String.valueOf(server));
		
	}

	  public void Server_address(View view){
		  View focusView = null;
		  int count = dbs.getCount();  
		  EditText serverEditText = (EditText) findViewById(R.id.editText_server);
		  String str = serverEditText.getText().toString();
		  if(count==0){
			  if (TextUtils.isEmpty(str)) {
				  serverEditText.setError(getString(R.string.error_field_required));
					focusView = serverEditText;
					focusView.requestFocus();
				}else{
					dbs.addServer(str);
					  runOnUiThread(new Toasting("Server address updated"));
				}
		  }else if(count==1){
			  if (TextUtils.isEmpty(str)) {
				  serverEditText.setError(getString(R.string.error_field_required));
					focusView = serverEditText;
					focusView.requestFocus();
				}else{
					 String currentAddress = dbs.TopRow();
					 dbs.updateServer(str,currentAddress);
					 runOnUiThread(new Toasting("Server address updated"));
				}
			 
			  }
	}
	  public void Sign_out(View view){
		  int count = db.getCount();
		  if(count==1){
			 DBInput user=db.TopRow();
			 db.deleteUser(user); 
			 runOnUiThread(new Toasting("Logged out."));
		  }else{
			  runOnUiThread(new Toasting("You are already logged out."));
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
			View rootView = inflater.inflate(R.layout.fragment_settings,
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
