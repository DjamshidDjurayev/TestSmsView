package co.djurayev.smsview.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import co.djurayev.smsview.R;
import co.djurayev.smsview.listeners.KeyboardActionListener;
import co.djurayev.smsview.listeners.OnKeyboardKeyListener;

public class KeyboardContainer extends FrameLayout {
  private static final int ANIMATION_DURATION = 600;
  private KeyboardView keyboardView;
  private OnKeyboardKeyListener onKeyPressListener;
  private float viewHeight;
  private boolean isShown;
  private boolean isAnimating;
  private boolean isEnabled = true;

  public KeyboardContainer(@NonNull Context context) {
    super(context);
    init();
  }

  public KeyboardContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public KeyboardContainer(@NonNull Context context, @Nullable AttributeSet attrs,
      int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public KeyboardContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.container_keyboard_view, this);

    this.keyboardView = findViewById(R.id.keyboard_view);

    keyboardView.setPreviewEnabled(false);
    keyboardView.setKeyboard(new Keyboard(getContext(), R.xml.keyboard_pin_view));
    keyboardView.setOnKeyboardActionListener(new KeyboardActionListener() {
      @Override public void onKey(int primaryCode, int[] keyCodes) {
        if (onKeyPressListener != null && isEnabled) onKeyPressListener.onKeyPressed(primaryCode);
      }
    });

    keyboardView.post(() -> {
      viewHeight = keyboardView.getHeight();
      keyboardView.setVisibility(View.GONE);
    });
  }

  public void displayKeyboard() {
    keyboardView.post(() -> {
      ObjectAnimator animator =
          ObjectAnimator.ofFloat(keyboardView, "translationY", viewHeight, 0f)
              .setDuration(ANIMATION_DURATION);
      animator.setInterpolator(new AccelerateDecelerateInterpolator());
      animator.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationStart(Animator animation) {
          isAnimating = true;
        }

        @Override public void onAnimationEnd(Animator animation) {
          isAnimating = false;
          isShown = true;
        }
      });
      animator.start();
      keyboardView.setVisibility(VISIBLE);
    });
  }

  public void hideKeyboard() {
    ObjectAnimator animator =
        ObjectAnimator.ofFloat(keyboardView, "translationY", 0f, viewHeight)
            .setDuration(ANIMATION_DURATION);
    animator.setInterpolator(new AccelerateDecelerateInterpolator());
    animator.addListener(new AnimatorListenerAdapter() {
      @Override public void onAnimationStart(Animator animation) {
        isAnimating = true;
      }

      @Override public void onAnimationEnd(Animator animation) {
        isAnimating = false;
        keyboardView.setVisibility(View.GONE);
        isShown = false;
      }
    });
    animator.start();
  }

  @Override public boolean isShown() {
    return isShown;
  }

  public void setIsEnabled(boolean enabled) {
    this.isEnabled = enabled;
  }

  public boolean isAnimating() {
    return isAnimating;
  }

  public void setOnKeyPressListener(OnKeyboardKeyListener onKeyPressListener) {
    this.onKeyPressListener = onKeyPressListener;
  }
}