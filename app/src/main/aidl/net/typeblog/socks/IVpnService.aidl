// IVpnService.aidl
package net.typeblog.socks;

// Declare any non-default types here with import statements

interface IVpnService {
    boolean isRunning();
    void stop();
}
