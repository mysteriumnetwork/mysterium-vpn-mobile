/*
 * Copyright (C) 2018 The "MysteriumNetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#import "MysteriumClientModule.h"
#import <React/RCTLog.h>

@import NetworkExtension;

@implementation MysteriumClientModule

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(startService:(NSInteger)port
                  statusResolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    [NETunnelProviderManager loadAllFromPreferencesWithCompletionHandler:^(NSArray<NETunnelProviderManager *> *managers,
                                                                           NSError *error) {
        if (error) {
            [self callFailure:reject withError:error];
            return;
        }
        NETunnelProviderManager *manager = managers.firstObject ?: [NETunnelProviderManager new];
        [self setupVPNWithManager:manager success:resolve failure:reject];
    }];
}

- (void)setupVPNWithManager:(NETunnelProviderManager *)tunnelProviderManager
                    success:(RCTPromiseResolveBlock)success
                    failure:(RCTPromiseRejectBlock)failure
{
    __weak NETunnelProviderManager *weakTunnelProviderManager = tunnelProviderManager;
    [tunnelProviderManager loadFromPreferencesWithCompletionHandler:^(NSError *error) {
        if (error) {
            [self callFailure:failure withError:error];
            return;
        }

        NETunnelProviderProtocol *tunnelProtocol = [NETunnelProviderProtocol new];
        tunnelProtocol.serverAddress = @""; // can't be nil
        tunnelProtocol.providerBundleIdentifier = @"network.mysterium.vpn.MysteriumNetworkExtension";
        weakTunnelProviderManager.protocolConfiguration = tunnelProtocol;
        weakTunnelProviderManager.localizedDescription = @"Mysterium VPN client";
        weakTunnelProviderManager.enabled = YES;
        [weakTunnelProviderManager saveToPreferencesWithCompletionHandler:^(NSError *error) {
            if (error) {
                [self callFailure:failure withError:error];
                return;
            }
            NSError *startError;
            [weakTunnelProviderManager.connection startVPNTunnelAndReturnError:&error];
            if (startError) {
                [self callFailure:failure withError:startError];
                return;
            }
            success(@[@0]); //return 0 for successful start
        }];
    }];
}

- (void)callFailure:(RCTPromiseRejectBlock)failure withError:(NSError *)error
{
    assert(error);
    failure([NSString stringWithFormat: @"%ld", (long)[error code]], [error localizedDescription], error);
}

@end
