#import "AudioQueryPlugin.h"
#if __has_include(<audio_query/audio_query-Swift.h>)
#import <audio_query/audio_query-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "audio_query-Swift.h"
#endif

@implementation AudioQueryPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAudioQueryPlugin registerWithRegistrar:registrar];
}
@end
