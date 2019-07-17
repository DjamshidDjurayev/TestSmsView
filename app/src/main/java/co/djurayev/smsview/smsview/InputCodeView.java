package co.djurayev.smsview.smsview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.djurayev.smsview.R;
import co.djurayev.smsview.keyboard.KeyboardContainer;
import co.djurayev.smsview.listeners.AnimationsListener;
import co.djurayev.smsview.listeners.OnSmsInputCodeListener;
import co.djurayev.smsview.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class InputCodeView extends FrameLayout {
  @BindView(R.id.underline_1) View underline1;
  @BindView(R.id.underline_2) View underline2;
  @BindView(R.id.underline_3) View underline3;
  @BindView(R.id.underline_4) View underline4;
  @BindView(R.id.underline_5) View underline5;
  @BindView(R.id.underline_6) View underline6;

  @BindView(R.id.input_1) TextView input1;
  @BindView(R.id.input_2) TextView input2;
  @BindView(R.id.input_3) TextView input3;
  @BindView(R.id.input_4) TextView input4;
  @BindView(R.id.input_5) TextView input5;
  @BindView(R.id.input_6) TextView input6;

  @BindView(R.id.container_1) View container1;
  @BindView(R.id.container_2) View container2;
  @BindView(R.id.container_3) View container3;
  @BindView(R.id.container_4) View container4;
  @BindView(R.id.container_5) View container5;
  @BindView(R.id.container_6) View container6;
  @BindView(R.id.separator_container) View separatorContainer;
  @BindView(R.id.separator) TextView separator;

  private static final int ANIMATION_DURATION = 400;

  private final List<View> underlines = new ArrayList<>(6);
  private final List<TextView> inputs = new ArrayList<>(6);
  private final List<View> containers = new ArrayList<>(7);

  private OnSmsInputCodeListener smsInputCodeListener;
  private int cursorPosition = 0;
  private Animation viewAnimation;
  private int inputTextColor;
  private int underlineColor;
  private int underlineWidth;
  private int underlineHeight;
  private float inputTextSize;
  private int inputMargin;
  private float separatorSize;
  private int separatorColor;
  private boolean autoClearOnError = false;
  private Handler handler;

  private KeyboardContainer keyboardContainer;

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

    View rootView = inflate(getContext(), R.layout.input_code_view, this);
    ButterKnife.bind(rootView);

    this.containers.add(container1);
    this.containers.add(container2);
    this.containers.add(container3);
    this.containers.add(separatorContainer);
    this.containers.add(container4);
    this.containers.add(container5);
    this.containers.add(container6);

    this.inputs.add(input1);
    this.inputs.add(input2);
    this.inputs.add(input3);
    this.inputs.add(input4);
    this.inputs.add(input5);
    this.inputs.add(input6);

    this.underlines.add(underline1);
    this.underlines.add(underline2);
    this.underlines.add(underline3);
    this.underlines.add(underline4);
    this.underlines.add(underline5);
    this.underlines.add(underline6);

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
  }

  private void setValues() {
    for (int i = 0; i < underlines.size(); i++) {
      underlines.get(i).setBackgroundColor(underlineColor);
      underlines.get(i)
          .setLayoutParams(new LinearLayout.LayoutParams(underlineWidth, underlineHeight));

      inputs.get(i).setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize);
      inputs.get(i).setTextColor(inputTextColor);
    }

    for (int i = 0; i < containers.size(); i++) {
      View view = containers.get(i);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
      params.setMargins(inputMargin, params.topMargin, inputMargin, params.bottomMargin);
      view.setLayoutParams(params);
    }

    separator.setTextSize(TypedValue.COMPLEX_UNIT_SP, separatorSize);
    separator.setTextColor(separatorColor);
  }

  public void setSmsInputCodeListener(OnSmsInputCodeListener smsInputCodeListener) {
    this.smsInputCodeListener = smsInputCodeListener;
  }

  @MainThread public void add(int value, boolean animated) {
    if (cursorPosition >= inputs.size()) return;

    TextView codeView = inputs.get(cursorPosition);

    cursorPosition++;

    codeView.setText(String.valueOf(value));
    codeView.setTag("added");

    if (animated) {
      AnimatorSet animatorSet = new AnimatorSet();

      ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(codeView, View.ALPHA, 0,1)
          .setDuration(300);
      alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

      ObjectAnimator translateAnimator =
          ObjectAnimator.ofFloat(codeView, "translationY", codeView.getHeight(), 0)
              .setDuration(ANIMATION_DURATION);
      translateAnimator.setInterpolator(new OvershootInterpolator());

      animatorSet.playTogether(alphaAnimator, translateAnimator);

      codeView.clearAnimation();
      animatorSet.start();
    }

    if (cursorPosition == inputs.size() && smsInputCodeListener != null) {
      smsInputCodeListener.onInputCompleted(getFullCode());
    }
  }

  public String getFullCode() {
    StringBuilder stringBuilder = new StringBuilder(6);
    for (int i = 0; i < inputs.size(); i++) {
      stringBuilder.append(inputs.get(i).getText().toString().trim());
    }
    return String.valueOf(stringBuilder);
  }

  @MainThread public void delete(boolean animated) {
    if (cursorPosition <= 0) return;

    cursorPosition--;

    final TextView codeView = inputs.get(cursorPosition);
    codeView.setTag("");

    if (animated) {
      AnimatorSet animatorSet = new AnimatorSet();

      ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(codeView, View.ALPHA, 1,0)
          .setDuration(300);
      alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

      ObjectAnimator translationAnimator =
          ObjectAnimator.ofFloat(codeView, "translationY", 0, codeView.getHeight())
              .setDuration(ANIMATION_DURATION);
      translationAnimator.setInterpolator(new AnticipateOvershootInterpolator());
      translationAnimator.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          if (TextUtils.isEmpty(codeView.getTag().toString())) {
            codeView.setText("");
          }
        }
      });

      codeView.clearAnimation();
      animatorSet.playTogether(alphaAnimator, translationAnimator);
      animatorSet.start();
    } else {
      codeView.setText("");
    }

    if (smsInputCodeListener != null && cursorPosition == 0) {
      smsInputCodeListener.onCleared();
    }
  }

  @MainThread public void clearAll(final boolean animated, int delayTime) {
    if (cursorPosition != 0) {
      setCursorPosition(inputs.size());

      for (int i = 0; i < inputs.size(); i++) {
        handler.postDelayed(() -> delete(animated), delayTime + delayTime * i);
      }
    }
  }

  @MainThread public void addAll(String fullInput, final boolean animated, int delayTime) {
    if (TextUtils.isEmpty(fullInput)) return;

    setCursorPosition(0);

    final char[] inputArray = fullInput.toCharArray();

    for (int i = 0; i < inputArray.length; i++) {
      final int finalI = i;
      handler.postDelayed(
          () -> add(Integer.valueOf(String.valueOf(inputArray[finalI])), animated), delayTime + delayTime * i);
    }
  }

  public void showErrorView() {
    if (viewAnimation != null && !viewAnimation.hasEnded()) viewAnimation.cancel();

    if (keyboardContainer != null) keyboardContainer.setIsEnabled(false);

    viewAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
    viewAnimation.setAnimationListener(new AnimationsListener() {
      @Override public void onAnimationStart(Animation animation) {
        shakeViews();
      }

      @Override public void onAnimationEnd(Animation animation) {
        resetViews();

        if (autoClearOnError) {
          clearAll(true, 100);
        } else {
          if (keyboardContainer != null) keyboardContainer.setIsEnabled(true);
        }
      }
    });
    startAnimation(viewAnimation);
  }

  private void resetViews() {
    for (int i = 0; i < underlines.size(); i++) {
      underlines.get(i).setBackgroundColor(underlineColor);
      inputs.get(i).setTextColor(inputTextColor);
    }
    separator.setTextColor(separatorColor);
  }

  private void shakeViews() {
    for (int i = 0; i < underlines.size(); i++) {
      underlines.get(i)
          .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
      inputs.get(i).setTextColor(ContextCompat.getColor(getContext(), R.color.red));
    }
    separator.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
  }

  private void setCursorPosition(int start) {
    cursorPosition = start;
  }

  public void setKeyboardContainer(KeyboardContainer keyboardContainer) {
    this.keyboardContainer = keyboardContainer;
  }

  public void setAutoClearOnError(boolean autoClearOnError) {
    this.autoClearOnError = autoClearOnError;
  }
}