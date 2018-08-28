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

- (NSDictionary *)constantsToExport
{
    return @{ @"foo": @"bar" };
}

RCT_EXPORT_METHOD(startService:(NSString *)name completion:(RCTResponseSenderBlock)completion)
{
    NSString *newName = [NSString stringWithFormat:@"%@bar", name];
    completion(@[[NSNull null], @[newName]]);
}

@end
