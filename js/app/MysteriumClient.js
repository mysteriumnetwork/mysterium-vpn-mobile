/**
 * This exposes the native MysteriumClient module as a JS module. This has a
 * function 'startService' which takes the following parameters:
 *
 * 1. int port: port number for the service to use
 * 2. Promise service_status: The status of the service after starting
 */
import {NativeModules} from 'react-native';
module.exports = NativeModules.MysteriumClientModule;
