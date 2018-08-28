//
//  TestModule.m
//  MysteriumVPN
//
//  Created by Arnas Dundulis on 27/08/2018.
//  Copyright © 2018 Mysterium Network. All rights reserved.
//

#import "TestModule.h"
#import <React/RCTLog.h>

@implementation TestModule

RCT_EXPORT_MODULE();

- (NSDictionary *)constantsToExport
{
    return @{ @"foo": @"bar" };
}

RCT_EXPORT_METHOD(startService)
{
    RCTLogInfo(@"Starting some sort of service");
}

@end
