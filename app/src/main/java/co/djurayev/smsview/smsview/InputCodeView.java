package co.djurayev.smsview.smsview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import co.djurayev.smsview.R;
import co.djurayev.smsview.keyboard.KeyboardContainer;
import co.djurayev.smsview.listeners.AnimationsListener;
import co.djurayev.smsview.listeners.OnSmsInputCodeListener;
import co.djurayev.smsview.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class InputCodeView extends LinearLayout {

  private OnSmsInputCodeListener smsInputCodeListener;
  private int cursorPosition = 0;
  private int inputTextColor;
  private int underlineColor;
  private int underlineWidth;
  private int underlineHeight;
  private float inputTextSize;
  private int inputMargin;
  private float separatorSize;
  private int separatorColor;
  private boolean autoClearOnError = false;
  private boolean isAnimated = false;
  private int delayTime = 100;

  private Handler handler;
  private KeyboardContainer keyboardContainer;
  private Animation shakeAnimation;
  private InputItem separator;

  private final List<InputItem> inputs = new ArrayList<>(6);

  public InputCodeView(@NonNull Context context) {
    super(context);
    initialize(context, null);
  }

  public InputCodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize(context, attrs);
  }

  public InputCodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(context, attrs);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public InputCodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initialize(context, attrs);
  }

  private void initialize(@NonNull Context context, @Nullable AttributeSet attrs) {
    handler = new Handler(Looper.getMainLooper());

    LinearLayout.LayoutParams params
        = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    params.gravity = Gravity.CENTER;
    setLayoutParams(params);
    setOrientation(HORIZONTAL);

    inflate(getContext(), R.layout.input_code_view, this);

    separator = findViewById(R.id.divider);

    this.inputs.add(findViewById(R.id.input_1));
    this.inputs.add(findViewById(R.id.input_2));
    this.inputs.add(findViewById(R.id.input_3));
    this.inputs.add(findViewById(R.id.input_4));
    this.inputs.add(findViewById(R.id.input_5));
    this.inputs.add(findViewById(R.id.input_6));

    if (attrs != null) {
      TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputCodeView);

      try {
        underlineColor = typedArray.getColor(R.styleable.InputCodeView_underlineColor, Color.BLACK);
        underlineWidth = typedArray.getDimensionPixelSize(R.styleable.InputCodeView_underlineWidth,
            Utils.toPx(context, 20));
        underlineHeight = typedArray.getDimensionPixelSize(R.styleable.InputCodeView_underHeight,
            Utils.toPx(context, 1));
        inputTextSize = typedArray.getDimension(R.styleable.InputCodeView_inputTextSize, 30);
        inputTextColor = typedArray.getColor(R.styleable.InputCodeView_inputTextColor, Color.GRAY);
        inputMargin = typedArray.getDimensionPixelSize(R.styleable.InputCodeView_inputMargin,
            Utils.toPx(context, 5));
        separatorSize = typedArray.getDimension(R.styleable.InputCodeView_separatorSize, 30);
        separatorColor = typedArray.getColor(R.styleable.InputCodeView_separatorColor, Color.GRAY);

        setValues();
      } finally {
        if (typedArray != null) typedArray.recycle();
      }
    } else {
      setDefaultValues();
    }
  }

  private void setDefaultValues() {
    underlineColor = Color.BLACK;
    underlineWidth = Utils.toPx(getContext(), 20);
    underlineHeight = Utils.toPx(getContext(), 1);
    inputTextSize = 30;
    inputTextColor = Color.GRAY;
    inputMargin = Utils.toPx(getContext(), 5);
    separatorSize = 30;
    separatorColor = Color.GRAY;

    setValues();
  }

  private void setValues() {
    for (int i = 0; i < inputs.size(); i++) {
      InputItem item = inputs.get(i);
      item.setUnderlineColor(underlineColor);
      item.setUnderlineParams(underlineWidth, underlineHeight);
      item.setTextSize(inputTextSize);
      item.setTextColor(inputTextColor);
      item.setContainerMargin(inputMargin);
    }

    separator.setInputText("-");
    separator.setTextSize(separatorSize);
    separator.setTextColor(separatorColor);
    separator.setUnderlineColor(android.R.color.transparent);
  }

  @MainThread public void add(int value) {
    if (cursorPosition >= inputs.size()) return;

    inputs.get(cursorPosition).add(isAnimated, value);

    cursorPosition++;

    if (cursorPosition == inputs.size() && smsInputCodeListener != null) {
      smsInputCodeListener.onInputCompleted(getFullCode());
    }
  }

  public String getFullCode() {
    StringBuilder stringBuilder = new StringBuilder(6);
    for (int i = 0; i < inputs.size(); i++) {
      stringBuilder.append(inputs.get(i).getInputText().toString().trim());
    }
    return String.valueOf(stringBuilder);
  }

  @MainThread public void delete() {
    if (cursorPosition <= 0) return;

    cursorPosition--;

    inputs.get(cursorPosition).remove(isAnimated);

    if (smsInputCodeListener != null && cursorPosition == 0) {
      smsInputCodeListener.onCleared();
    }
  }

  @MainThread public void clearAll() {
    if (cursorPosition != 0) {
      setCursorPosition(inputs.size());

      for (int i = 0; i < inputs.size(); i++) {
        handler.postDelayed(this::delete, delayTime + delayTime * i);
      }
    }
  }

  @MainThread public void addAll(String fullInput) {
    if (TextUtils.isEmpty(fullInput)) return;

    setCursorPosition(0);

    final char[] inputArray = fullInput.toCharArray();

    for (int i = 0; i < inputArray.length; i++) {
      final int finalI = i;
      handler.postDelayed(
          () -> add(Integer.valueOf(String.valueOf(inputArray[finalI]))),
          delayTime + delayTime * i);
    }
  }

  public void showErrorView() {
    if (shakeAnimation != null && !shakeAnimation.hasEnded()) shakeAnimation.cancel();

    if (keyboardContainer != null) keyboardContainer.setIsEnabled(false);

    shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
    shakeAnimation.setAnimationListener(new AnimationsListener() {
      @Override public void onAnimationStart(Animation animation) {
        setErrorViews();

        if (smsInputCodeListener != null) smsInputCodeListener.onError(getFullCode());
      }

      @Override public void onAnimationEnd(Animation animation) {
        resetViews();

        if (autoClearOnError) {
          clearAll();
        } else {
          if (keyboardContainer != null) keyboardContainer.setIsEnabled(true);
        }
      }
    });
    startAnimation(shakeAnimation);
  }

  private void resetViews() {
    for (int i = 0; i < inputs.size(); i++) {
      inputs.get(i).setUnderlineColor(underlineColor);
      inputs.get(i).setTextColor(inputTextColor);
    }

    separator.setTextColor(separatorColor);
  }

  private void setErrorViews() {
    for (int i = 0; i < inputs.size(); i++) {
      inputs.get(i)
          .setUnderlineColor(ContextCompat.getColor(getContext(), R.color.red));
      inputs.get(i).setTextColor(ContextCompat.getColor(getContext(), R.color.red));
    }
    separator.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
  }

  private void setCursorPosition(int start) {
    cursorPosition = start;
  }

  public void setKeyboardContainer(KeyboardContainer keyboardContainer) {
    this.keyboardContainer = keyboardContainer;

    keyboardContainer.setOnKeyboardKeyListener(keyCode -> {
      if (keyCode >= 0) {
        add(keyCode);
      } else {
        delete();
      }
    });
  }

  public void setAutoClearOnError(boolean autoClearOnError) {
    this.autoClearOnError = autoClearOnError;
  }

  public void setAnimated(boolean animated) {
    this.isAnimated = animated;
  }

  public void setSmsInputCodeListener(OnSmsInputCodeListener smsInputCodeListener) {
    this.smsInputCodeListener = smsInputCodeListener;
  }
}