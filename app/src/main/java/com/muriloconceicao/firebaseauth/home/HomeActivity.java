package com.muriloconceicao.firebaseauth.home;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.muriloconceicao.firebaseauth.R;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }
}