package org.kindradio.app;
import android.content.*;import android.net.Uri;import android.os.Bundle;import android.view.*;import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;import androidx.mediarouter.app.MediaRouteButton;
import com.google.android.gms.cast.*;import com.google.android.gms.cast.framework.*;import java.net.*;import java.util.*;
public class MainActivity extends AppCompatActivity {
 private static final int PICK=7; private AudioServer server; private Uri file;private TextView status;private CastContext cast;
 @Override public void onCreate(Bundle b){super.onCreate(b);
  LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setPadding(35,55,35,35);
  TextView heading=new TextView(this);heading.setText("KIND RADIO  •  v0.1");heading.setTextSize(25);l.addView(heading);
  status=new TextView(this);status.setText("Select a local MP3, then connect to your Vizio.");status.setPadding(0,30,0,30);l.addView(status);
  Button choose=new Button(this);choose.setText("1. Choose audio file");l.addView(choose);
  choose.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("audio/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK);});
  TextView tip=new TextView(this);tip.setText("2. Tap the Cast icon below and choose Vizio");l.addView(tip);
  MediaRouteButton route=new MediaRouteButton(this);l.addView(route,new LinearLayout.LayoutParams(160,110));
  Button play=new Button(this);play.setText("3. Cast selected music");l.addView(play);play.setOnClickListener(v->castSong());
  Button pause=new Button(this);pause.setText("Pause / Resume");l.addView(pause);pause.setOnClickListener(v->{CastSession cs=cast.getSessionManager().getCurrentCastSession();if(cs!=null&&cs.getRemoteMediaClient()!=null){RemoteMediaClient c=cs.getRemoteMediaClient();if(c.isPaused())c.play();else c.pause();}});
  setContentView(l);
  try{server=new AudioServer(this);server.start();cast=CastContext.getSharedInstance(this);CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),route);}catch(Exception e){status.setText("Initialization error: "+e.getMessage());}
 }
 @Override protected void onActivityResult(int req,int result,Intent data){super.onActivityResult(req,result,data);if(req==PICK&&result==RESULT_OK&&data!=null){file=data.getData();server.setSong(file);status.setText("Selected: "+file.getLastPathSegment());}}
 private String localIp(){try{Enumeration<NetworkInterface> n=NetworkInterface.getNetworkInterfaces();while(n.hasMoreElements()){NetworkInterface x=n.nextElement();if(!x.isUp()||x.isLoopback())continue;Enumeration<InetAddress> a=x.getInetAddresses();while(a.hasMoreElements()){InetAddress ip=a.nextElement();if(ip instanceof Inet4Address&&ip.isSiteLocalAddress()&&!ip.isLoopbackAddress())return ip.getHostAddress();}}}catch(Exception ignored){}return null;}
 private void castSong(){if(file==null){status.setText("Choose an audio file first.");return;}if(cast==null){status.setText("Cast not initialized");return;}CastSession session=cast.getSessionManager().getCurrentCastSession();if(session==null||!session.isConnected()){status.setText("Connect to Vizio with Cast button first.");return;}String ip=localIp();if(ip==null){status.setText("Cannot determine phone's local IP.");return;}String mime=getContentResolver().getType(file);if(mime==null)mime="audio/mpeg";
  MediaInfo media=new MediaInfo.Builder("http://"+ip+":8765/audio").setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).setContentType(mime).build();
  session.getRemoteMediaClient().load(new MediaLoadRequestData.Builder().setMediaInfo(media).setAutoplay(true).build());status.setText("Sent stream URL to Vizio: "+ip+":8765. If silent, receiver may need HTTP range support.");
 }
 @Override protected void onDestroy(){if(server!=null)server.stop();super.onDestroy();}
}
