package co.djurayev.smsview;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    keyboardContainer.setOnKeyboardKeyListener(keyCode -> {
      if (keyCode >= 0) {
        inputCodeView.add(keyCode, true);
      } else {
        inputCodeView.delete(true);
      }
    });

    keyboardContainer.displayKeyboard();
    inputCodeView.setKeyboardContainer(keyboardContainer);
    inputCodeView.setAutoClearOnError(false);

    inputCodeView.setSmsInputCodeListener(new OnSmsInputCodeListener() {
      @Override public void onInputCompleted(@NonNull String code) {
        keyboardContainer.setIsEnabled(true);

        if (!"300000".equals(code)) {
          inputCodeView.showErrorView();
        }
      }

      @Override public void onCleared() {
        keyboardContainer.setIsEnabled(true);
      }
    });

    handler.postDelayed(() -> {
      keyboardContainer.setIsEnabled(false);
      inputCodeView.addAll("300000", true, 100);
    }, 3000);
  }
}
