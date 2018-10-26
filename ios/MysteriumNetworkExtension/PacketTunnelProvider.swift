//
//  PacketTunnelProvider.swift
//  MysteriumNetworkExtension
//
//  Created by Arnas Dundulis on 26/10/2018.
//  Copyright © 2018 Mysterium Network. All rights reserved.
//

import NetworkExtension
import Mysterium

class PacketTunnelProvider: NEPacketTunnelProvider {

    override func startTunnel(options: [String : NSObject]? = nil, completionHandler: @escaping (Error?) -> Void) {
        MysteriumNewNode()
    }
}
