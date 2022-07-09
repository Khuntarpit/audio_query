package com.audio.query.audio_query;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import android.app.Application;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.audio.query.audio_query.delegate.AudioQueryDelegate;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** AudioQueryPlugin */
public class AudioQueryPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {

  private static final String CHANNEL_NAME = "audio_query";
  private AudioQueryDelegate m_delegate;
  private FlutterPluginBinding m_pluginBinding;
  private ActivityPluginBinding m_activityBinding;
  private MethodChannel channel;
  private Application application;



  // These are null when not using v2 embedding;
  private Lifecycle lifecycle;
  private LifeCycleObserver observer;

  private AudioQueryPlugin(AudioQueryDelegate delegate){
    m_delegate = delegate;
  }

  public AudioQueryPlugin(){}

  public static void registerWith(Registrar registrar) {
    if (registrar.activity() == null)
      return;


    Application application = null;
    if (registrar.context() != null) {
      application = (Application) (registrar.context().getApplicationContext());
    }

    final AudioQueryPlugin plugin = new AudioQueryPlugin();
    Log.i("AUDIO_QUERY", "Using V1 EMBEDDING");
    plugin.setup(registrar.messenger(), application, registrar.activity(), registrar, null);
  }


  @Override
  public void onMethodCall(MethodCall call, Result result) {

    String source = call.argument("source");
    if (source != null ){

      switch (source){
        case "artist":
          m_delegate.artistSourceHandler(call, result);
          break;

        case "album":
          m_delegate.albumSourceHandler(call, result);
          break;

        case "song":
          m_delegate.songSourceHandler(call, result);
          break;

        case "genre":
          m_delegate.genreSourceHandler(call, result);
          break;

        case "playlist":
          m_delegate.playlistSourceHandler(call, result);
          break;


        case "artwork":
          m_delegate.artworkSourceHandler(call, result);
          break;

        default:
          result.error("unknown_source",
                  "method call was made by an unknown source", null);
          break;

      }
    }

    else {
      result.error("no_source", "There is no source in your method call", null);
    }
  }

  // embeding V2 implementation
  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    m_pluginBinding = binding;

  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) { tearDown();}

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    m_activityBinding = binding;
    Log.i("AUDIO_QUERY", "Using V2 EMBEDDING:: activity = " + binding.getActivity() );
    setup(
            m_pluginBinding.getBinaryMessenger(),
            (Application) m_pluginBinding.getApplicationContext(),

            m_activityBinding.getActivity(),
            null,
            m_activityBinding
    );
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
  }

  private void setup(
          final BinaryMessenger messenger,
          final Application application,
          final Activity activity, final PluginRegistry.Registrar registrar,
          final ActivityPluginBinding activityBinding){

    this.application = application;


    if (registrar != null) {
      // V1 embedding  delegate creation
      m_delegate = AudioQueryDelegate.instance(registrar);
      observer = new LifeCycleObserver(activity);
      application.registerActivityLifecycleCallbacks(observer);

    }

    else {
      // V2 embedding setup for activity listeners.
      if (m_delegate == null)
        m_delegate = AudioQueryDelegate.instance(application.getApplicationContext(), activity);

      activityBinding.addRequestPermissionsResultListener(m_delegate);

      lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(activityBinding);

      //activityBinding.
      observer = new LifeCycleObserver(activityBinding.getActivity());
      lifecycle.addObserver(observer);

    }

    if (channel == null) {
      channel = new MethodChannel(messenger, CHANNEL_NAME);
      channel.setMethodCallHandler(new AudioQueryPlugin(m_delegate));
    }

  }

  private void tearDown() {
    if(m_activityBinding != null){
      m_activityBinding.removeRequestPermissionsResultListener(m_delegate);
      m_activityBinding = null;
    }
    if (lifecycle != null) {
      lifecycle.removeObserver(observer);
      lifecycle = null;
    }
    m_delegate = null;
    if (channel != null) {
      channel.setMethodCallHandler(null);
      channel = null;
    }
    if(application != null){
      application.unregisterActivityLifecycleCallbacks(observer);
      application = null;
    }
  }

  private class LifeCycleObserver
          implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private final Activity thisActivity;

    LifeCycleObserver(Activity activity) {
      this.thisActivity = activity;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {}

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {}

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {}

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {}

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
      onActivityStopped(thisActivity);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
      onActivityDestroyed(thisActivity);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {
      if (thisActivity == activity && activity.getApplicationContext() != null) {
        ((Application) activity.getApplicationContext())
                .unregisterActivityLifecycleCallbacks(
                        this); // Use getApplicationContext() to avoid casting failures
      }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }
  } //// LifeCycleObserver end

}
