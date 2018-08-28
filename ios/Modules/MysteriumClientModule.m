//
//  MysteriumClientModule.m
//  MysteriumVPN
//
//  Created by Arnas Dundulis on 27/08/2018.
//  Copyright © 2018 Mysterium Network. All rights reserved.
//

#import "MysteriumClientModule.h"
#import <React/RCTLog.h>

@implementation MysteriumClientModule

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(startService:(NSInteger)port
                  statusResolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve(@[@0]); //return 0 for successful start
}

@end
