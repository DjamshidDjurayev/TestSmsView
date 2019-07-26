package co.djurayev.smsview;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import co.djurayev.smsview.keyboard.KeyboardContainer;
import co.djurayev.smsview.listeners.OnSmsInputCodeListener;
import co.djurayev.smsview.smsview.InputCodeView;

public class MainActivity extends AppCompatActivity {
  private KeyboardContainer keyboardContainer;
  private InputCodeView inputCodeView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    keyboardContainer = findViewById(R.id.keyboard_container);
    inputCodeView = findViewById(R.id.input_code_view);
    inputCodeView.setAnimated(true);
    inputCodeView.setKeyboardContainer(keyboardContainer);
    inputCodeView.setAutoClearOnError(false);

    keyboardContainer.displayKeyboard();

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

      @Override public void onError(String code) {

      }
    });
  }
}
