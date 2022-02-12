package dev.morganv.ezbarber.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dev.morganv.ezbarber.R;

public class MainActivity extends AppCompatActivity {

    TextInputEditText logEmail;
    TextInputEditText logPassword;
    TextView tvRegister;
    Button btnLogin;
    SignInButton btnSignIn;
    Boolean signout = false;


    FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();

        logEmail = findViewById(R.id.login_ET_email);
        logPassword = findViewById(R.id.login_ET_password);
        tvRegister = findViewById(R.id.login_TV_register);
        btnLogin = findViewById(R.id.login_BTN_login);
        btnSignIn = findViewById(R.id.login_BTN_google);
        mAuth = FirebaseAuth.getInstance();

        signout = getIntent().getBooleanExtra("signout", false);
        if (signout)
            signOut();

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));

        requestGoogleSignIn();

        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setOnClickListener(v -> signIn());

    }

    private void requestGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void loginUser() {
        String email = logEmail.getText().toString();
        String password = logPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            logEmail.setError("Email cannot be empty");
            logEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            logPassword.setError("Password cannot be empty");
            logPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                    signInWithEmail();
                } else {
                    Toast.makeText(MainActivity.this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(MainActivity.this, "Welcome " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            signInWithEmail();
        }
    }

    private void signInWithEmail() {
        Intent intent = new Intent(MainActivity.this, MonthActivity.class);
        intent.putExtra("signInWithGoogle", false);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
//              String personName = acct.getDisplayName();
//              String personGivenName = acct.getGivenName();
//              String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
//              String personId = acct.getId();
//              Uri personPhoto = acct.getPhotoUrl();

                Toast.makeText(this, "User " + personEmail + " logged in", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(MainActivity.this, MonthActivity.class);
            intent.putExtra("signInWithGoogle", true);
            startActivity(intent);
        } catch (ApiException e) {
            Log.d("ErrorMessage", e.toString());
        }
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        GoogleSignIn.getClient(
                MainActivity.this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        signout = false;
    }

    public void hideSystemUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}