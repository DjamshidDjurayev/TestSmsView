package co.djurayev.smsview;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import co.djurayev.smsview.keyboard.KeyboardContainer;
import co.djurayev.smsview.listeners.OnSmsInputCodeListener;
import co.djurayev.smsview.smsview.InputCodeView;

public class MainActivity extends AppCompatActivity {
  private KeyboardContainer keyboardContainer;
  private InputCodeView inputCodeView;
  private Handler handler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    handler = new Handler(Looper.getMainLooper());

    keyboardContainer = findViewById(R.id.keyboard_container);
    inputCodeView = findViewById(R.id.input_code_view);

    keyboardContainer.setOnKeyPressListener(keyCode -> {
      if (keyCode >= 0) {
        inputCodeView.add(keyCode, true);
      } else {
        inputCodeView.delete(true);
      }
    });

    keyboardContainer.displayKeyboard();

    inputCodeView.setListener(new OnSmsInputCodeListener() {
      @Override public void onInputCompleted(@NonNull String code) {
        keyboardContainer.setIsEnabled(true);
        //inputCodeView.showErrorView();
      }

      @Override public void onCleared() {
        Toast.makeText(MainActivity.this, "cleared", Toast.LENGTH_SHORT).show();
      }
    });

    handler.postDelayed(() -> {
      keyboardContainer.setIsEnabled(false);
      inputCodeView.addAll("300000", true, 100);
    }, 3000);
  }
}