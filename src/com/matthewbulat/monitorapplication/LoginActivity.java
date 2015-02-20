package com.matthewbulat.monitorapplication;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	public void HostChoiceActivity(){
    	Intent intent = new Intent(this, HostChoiceActivity.class);
		startActivity(intent);
    }

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		setupActionBar();

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
		private Message serial = new Message();
		private Enc_Dec secret = new Enc_Dec();
		private WebSocketClient mWebSocketClient;
		private String req=null;
		private byte[] dat2;
		public void Websocket(){
		    	Message serverAddress = new Message();
		    	String server = serverAddress.getAddress();
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
							dat2 = secret.Decrypt(data);
							req = serial.getRequest(dat2);
						}
						@Override
						public void onMessage(String x) {//message Socket
								
							
							
						}
					};
					mWebSocketClient.connect();//establish connection with server
		    }
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			//attempt authentication against a network service.

			
			boolean pass=false;
			
			Database db = new Database(LoginActivity.this);
			
			
			//assign variables
			final String request="login";
			final String logpass=mEmail+":"+mPassword; 
			//passes assigned variables to object Message			
			serial.request=request;
			serial.data=logpass;
			//Sends it to server
			try{
				mWebSocketClient.getReadyState();
			}catch(NullPointerException e){
				Websocket();
			}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int ready = mWebSocketClient.getReadyState();
				int noOfTries_Ready=0;
				while(noOfTries_Ready!=9 && ready!=1){
					try {
						Websocket();
						Thread.sleep(250);
						ready = mWebSocketClient.getReadyState();
					} catch (InterruptedException e){
						e.printStackTrace();
					}
					noOfTries_Ready++;
				}
				if(noOfTries_Ready==9){
							runOnUiThread(new Toasting("Time out, please check your network connection, or loggin server address."));
							pass=false;
						}
						else{
							mWebSocketClient.send(secret.Encrypt(serial.getBytes(2)));
							int sleepTime=0;
						while(req==null){
							try {
								sleepTime+=100;
								Thread.sleep(sleepTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							}
						Log.d("Websocket", "req  "+req+" pass" +mPassword);
						
						switch(req){
							case"#*new_token*#":
									runOnUiThread(new Toasting("New Token was created for you."));
									int userCount= db.getCount();
									if(userCount==0){
										db.addUser(new DBInput(mEmail,secret.Encrypt(mPassword.getBytes()),serial.getToken(dat2)));//user details added to the database
									}else if(userCount==1){
										db.updateUser(new DBInput(mEmail,secret.Encrypt(mPassword.getBytes()),serial.getToken(dat2)));//user details updated to the database
									}			
									pass=true;
									
								break;
							case"#*try_again*#":
									String tries=serial.getData(dat2);
									runOnUiThread(new Toasting("Wrong password, number of tries: "+" "+tries+" out of 10."));
									pass=false;	
								break;
							case"#*blocked*#":
									runOnUiThread(new Toasting("Too many tries, your account is blocked, please contact your administrator."));
									pass=false;	//message to the user
								break;
							case"#*acc_blocked*#":
									runOnUiThread(new Toasting("Too many tries, your account was blocked, please contact your administrator."));
									pass=false;	//message to the user
								break;
							case"#*ok*#":
									int userCount1= db.getCount();
									if(userCount1==0){
										db.addUser(new DBInput(mEmail,secret.Encrypt(mPassword.getBytes()),serial.getToken(dat2)));//user details added to the database
									}else if(userCount1==1){
										db.updateUser(new DBInput(mEmail,secret.Encrypt(mPassword.getBytes()),serial.getToken(dat2)));//user details updated to the database
									}
									runOnUiThread(new Toasting("Welcome"));
									pass=true;
								break;
								default:
									runOnUiThread(new Toasting("Unknow error, please contact your administrator, or try again."));
									pass=false;
									break;
						}
					}
		req=null;		
		return pass;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				mWebSocketClient.close();
				HostChoiceActivity();
				//finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
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
