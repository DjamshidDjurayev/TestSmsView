package co.djurayev.smsview.utils;

import android.content.Context;

public class Utils {
  public static int toPx(Context context, int dp) {
    return (int)((dp * context.getResources().getDisplayMetrics().density) + 0.5);
  }
}
