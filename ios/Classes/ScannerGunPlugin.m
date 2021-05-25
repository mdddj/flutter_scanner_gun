#import "ScannerGunPlugin.h"
#if __has_include(<scanner_gun/scanner_gun-Swift.h>)
#import <scanner_gun/scanner_gun-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "scanner_gun-Swift.h"
#endif

@implementation ScannerGunPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftScannerGunPlugin registerWithRegistrar:registrar];
}
@end
