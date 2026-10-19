package org.kindradio.app;
import android.content.Context;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import java.util.List;
public class CastProvider implements OptionsProvider {
 public CastOptions getCastOptions(Context c) { return new CastOptions.Builder().setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID).build(); }
 public List<SessionProvider> getAdditionalSessionProviders(Context c) { return null; }
}
