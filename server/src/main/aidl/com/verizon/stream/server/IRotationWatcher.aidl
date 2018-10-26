// IRotationWatcher.aidl
package com.verizon.stream.server;

// Declare any non-default types here with import statements

interface IRotationWatcher {
    oneway void onRotationChanged(int rotation);
}
