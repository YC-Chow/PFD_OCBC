package sg.edu.np.pfd_ocbc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        String accno = intent.getStringExtra("accno");

        TextView test = findViewById(R.id.testtext);

        test.setText(accno);
    }
}