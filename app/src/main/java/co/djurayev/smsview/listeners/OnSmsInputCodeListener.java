package co.djurayev.smsview.listeners;

import android.support.annotation.NonNull;

public interface OnSmsInputCodeListener {
  void onInputCompleted(@NonNull String code);

  void onCleared();
}