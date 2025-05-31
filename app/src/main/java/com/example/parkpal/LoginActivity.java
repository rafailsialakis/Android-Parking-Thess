package com.example.parkpal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    long backPressedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        SectorList sectorList = getIntent().getParcelableExtra("sector");
        findViewById(R.id.loginbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView passwordField = findViewById(R.id.PasswordView);
                TextView usernameField = findViewById(R.id.UsernameView);
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                if(!isValidPassword(password)){
                    Toast.makeText(getApplicationContext(),
                            "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_LONG).show();
                    UserSession.isLoggedIn = true;
                    Intent i;
                    if(username.equals("root")){
                        i = new Intent(LoginActivity.this, RootActivity.class);
                        i.putExtra("sector", sectorList);
                    } else {
                        UserSession session = new UserSession(username,password);
                        i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("session", session);
                        i.putExtra("sector", sectorList);
                    }
                    startActivity(i);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }
    private boolean isValidPassword(String password) {
        if(password.length() < 8)
            return false;
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$";
        return password.matches(pattern);
    }

}
