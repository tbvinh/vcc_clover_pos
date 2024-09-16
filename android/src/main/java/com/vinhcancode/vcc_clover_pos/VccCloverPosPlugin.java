package com.vinhcancode.vcc_clover_pos;

import androidx.annotation.NonNull;
import android.app.Activity;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import android.os.Handler;
import android.os.Looper;

import android.app.AlertDialog;
import android.content.Context;

import com.clover.remote.client.CloverConnector;
import com.clover.remote.client.CloverDeviceConfiguration;
import com.clover.remote.client.DefaultCloverConnectorListener;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.USBCloverDeviceConfiguration;
import com.clover.remote.client.WebSocketCloverDeviceConfiguration;
import com.clover.remote.client.messages.ConfirmPaymentRequest;


import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/** VccCloverPosPlugin */
public class VccCloverPosPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  private static final String APP_ID = "com.cloverconnector.java.simple.sample:2.0.0";
  private static final String POS_NAME = "Clover Simple Sample Java";
  private static final String DEVICE_NAME = "Clover Device123";
  private AlertDialog pairingCodeDialog;

  private Context context;
  private Activity activity;


  private void connect(){
//    ICloverConnector colorConnector = new CloverConnector
  }
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "vcc_clover_pos");
    channel.setMethodCallHandler(this);
    this.context = flutterPluginBinding.getApplicationContext();

  }
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }
  @Override
  public void onDetachedFromActivity() {
    this.activity = null;
  }
  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
  }
  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // Handle activity detachment for configuration changes if needed
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {

      result.success("VCC Android " + android.os.Build.VERSION.RELEASE);

    } else if (call.method.equals("connectClover")) {

      // Extract IP and port from the MethodCall arguments
      String ip = call.argument("ip");
//      Integer port = call.argument("port")!=null? Integer.valueOf(call.argument("port")): 12345;
      Integer port = 12345;
      System.out.println("Before Connecting to Clover on IP: " + ip + ", Port: " + port);

      // Implement your connection logic here using the IP and port
      getNetworkConfiguration(ip, port);

      result.success("result.success ***** Connecting to Clover on IP: " + ip + ", Port: " + port);


    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  public CloverDeviceConfiguration getNetworkConfiguration(String ip) {
    return getNetworkConfiguration(ip, null);
  }

  public CloverDeviceConfiguration getNetworkConfiguration(String ip, Integer port) {
    Integer dvcPort = port != null ? port : Integer.valueOf(12345);
    try {
      URI endpoint = new URI("ws://" + ip + ":" + dvcPort + "/remote_pay");
      KeyStore trustStore = createTrustStore();

      System.out.println("getNetworkConfiguration **** socket: ==> ws://" + ip + ":" + dvcPort + "/remote_pay");
      // For WebSocket configuration, we must handle the device pairing via callback
      CloverDeviceConfiguration pairingCode = new WebSocketCloverDeviceConfiguration(endpoint, APP_ID, trustStore, POS_NAME, DEVICE_NAME, null) {
        @Override
        public void onPairingCode(final String pairingCode) {
          System.out.println("getNetworkConfiguration **** ==> onPairingCode Start ");

          activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              System.out.println("socket: ==> onPairingCode LOOP");
              // If we previously created a dialog and the pairing failed, reuse
              // the dialog previously created so that we don't get a stack of dialogs
              if (pairingCodeDialog != null) {
                pairingCodeDialog.setMessage("Enter pairing code: " + pairingCode);
              } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Pairing Code");
                builder.setMessage("Enter pairing code: " + pairingCode);
                pairingCodeDialog = builder.create();
              }
              pairingCodeDialog.show();
            }
          });
        }

        @Override
        public void onPairingSuccess(String authToken) {
          System.out.println("getNetworkConfiguration ****: ==> onPairingSuccess");
          activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (pairingCodeDialog != null && pairingCodeDialog.isShowing()) {
                pairingCodeDialog.dismiss();
                pairingCodeDialog = null;
              }
              System.out.println("getNetworkConfiguration ****: ==> Pairing Successful");
              //textConnect.setText("Pairing Successful!");
            }
          });
        }
      };
      return pairingCode;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.err.println("Error creating CloverDeviceConfiguration");
    return null;
  }

  private static KeyStore createTrustStore() {
    try {
      String storeType = KeyStore.getDefaultType();
      KeyStore trustStore = KeyStore.getInstance(storeType);
      char[] TRUST_STORE_PASSWORD = "clover".toCharArray();
      trustStore.load(null, TRUST_STORE_PASSWORD);

      // Load the old "dev" cert.  This should be valid for all target environments (dev, stg, sandbox, prod).
      Certificate cert = loadCertificateFromResource("/certs/device_ca_certificate.crt");
      trustStore.setCertificateEntry("dev", cert);

      // Now load the environment specific cert (e.g. prod).  Always retrieving this cert from prod as it is really
      // only valid in prod at this point, and we also don't have a mechanism within the SDK of specifying the target
      // environment.
      cert = loadCertificateFromResource("/certs/env_device_ca_certificate.crt");
      trustStore.setCertificateEntry("prod", cert);

      return trustStore;
    } catch (Throwable t) {
      t.printStackTrace();
    }
    return null;
  }

  private static Certificate loadCertificateFromResource(String name) {
    System.out.println("Loading cert:  " + name);

    InputStream is = null;
    try {
      is = VccCloverPosPlugin.class.getResourceAsStream(name);

      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      return cf.generateCertificate(is);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (Exception ex) {
          // NO-OP
        }
      }
    }
  }
}
