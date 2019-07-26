package co.djurayev.smsview.smsview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import co.djurayev.smsview.R;

public class InputItem extends FrameLayout {
  private static final int ANIMATION_DURATION = 400;

  private TextView inputText;
  private View inputUnderline;
  private View container;

  public InputItem(Context context) {
    super(context);
    initialize(null);
  }

  public InputItem(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initialize(attrs);
  }

  public InputItem(Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize(attrs);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public InputItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initialize(attrs);
  }

  private void initialize(AttributeSet attrs) {
    inflate(getContext(), R.layout.item_input, this);

    inputText = findViewById(R.id.input);
    inputUnderline = findViewById(R.id.underline);
    container = findViewById(R.id.container);
  }

  public void setUnderlineColor(int color) {
    inputUnderline.setBackgroundColor(color);
  }

  public void setUnderlineParams(int... params) {
    inputUnderline.setLayoutParams(new LinearLayout.LayoutParams(
        params[0], params[1]
    ));
  }

  public void setTextSize(float size) {
    inputText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  public void setTextColor(int color) {
    inputText.setTextColor(color);
  }

  public void setContainerMargin(int... args) {
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) container.getLayoutParams();
    params.setMargins(args[0], params.topMargin, args[0], params.bottomMargin);
    container.setLayoutParams(params);
  }

  public void setInputText(CharSequence text) {
    inputText.setText(text);
  }

  public CharSequence getInputText() {
    return inputText.getText();
  }

  public TextView getTextView() {
    return inputText;
  }

  public void add(boolean animated, int value) {
    inputText.setText(String.valueOf(value));
    inputText.setTag("added");

    if (animated) {
      AnimatorSet animatorSet = new AnimatorSet();

      ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(inputText, "alpha", 0, 1)
          .setDuration(300);
      alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

      ObjectAnimator translateAnimator =
          ObjectAnimator.ofFloat(inputText, "translationY", inputText.getHeight(), 0)
              .setDuration(ANIMATION_DURATION);
      translateAnimator.setInterpolator(new OvershootInterpolator());

      animatorSet.playTogether(alphaAnimator, translateAnimator);

      inputText.clearAnimation();
      animatorSet.start();
    }
  }

  public void remove(boolean animated) {
    inputText.setTag("");

    if (animated) {
      AnimatorSet animatorSet = new AnimatorSet();

      ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(inputText, "alpha", 1, 0)
          .setDuration(300);
      alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

      ObjectAnimator translationAnimator =
          ObjectAnimator.ofFloat(inputText, "translationY", 0, inputText.getHeight())
              .setDuration(ANIMATION_DURATION);
      translationAnimator.setInterpolator(new AnticipateOvershootInterpolator());
      translationAnimator.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          if (TextUtils.isEmpty(inputText.getTag().toString())) {
            inputText.setText("");
          }
        }
      });

      inputText.clearAnimation();
      animatorSet.playTogether(alphaAnimator, translationAnimator);
      animatorSet.start();
    } else {
      inputText.setText("");
    }
  }
}
