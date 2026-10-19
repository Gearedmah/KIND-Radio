package org.kindradio.app;
import android.content.Context;
import android.net.Uri;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;
class AudioServer {
 private final Context context; private final ExecutorService pool=Executors.newCachedThreadPool();
 private ServerSocket server; private volatile Uri song;
 AudioServer(Context c){context=c;}
 void setSong(Uri u){song=u;}
 void start() throws IOException {
   server=new ServerSocket(); server.setReuseAddress(true); server.bind(new InetSocketAddress(8765));
   pool.execute(()->{while(!server.isClosed()){try{Socket s=server.accept();pool.execute(()->serve(s));}catch(IOException e){break;}}});
 }
 void stop(){try{if(server!=null)server.close();}catch(IOException ignored){} pool.shutdownNow();}
 private void serve(Socket s){
  try(s){s.setSoTimeout(15000); BufferedReader r=new BufferedReader(new InputStreamReader(s.getInputStream()));
   String request=r.readLine();if(request==null)return;String line;while((line=r.readLine())!=null&&!line.isEmpty()){}
   OutputStream out=s.getOutputStream();Uri selected=song;
   if(selected==null||!request.startsWith("GET /audio")){out.write("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\nConnection: close\r\n\r\n".getBytes());return;}
   String mime=context.getContentResolver().getType(selected);if(mime==null||!mime.startsWith("audio/"))mime="audio/mpeg";
   // Stream until EOF. Some Cast receivers require Content-Length and range support: follow-up test.
   out.write(("HTTP/1.1 200 OK\r\nContent-Type: "+mime+"\r\nAccess-Control-Allow-Origin: *\r\nConnection: close\r\n\r\n").getBytes());
   try(InputStream in=context.getContentResolver().openInputStream(selected)){if(in!=null){byte[] buf=new byte[32768];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);}}
  }catch(Exception ignored){}
 }
}
