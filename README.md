# TestSmsView

![alt text][logo]

[logo]: https://github.com/DjamshidDjurayev/TestSmsView/blob/master/sms_input.gif

```java
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
```
