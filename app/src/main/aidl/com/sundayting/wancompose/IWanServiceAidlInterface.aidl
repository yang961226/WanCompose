// IWanServiceAidlInterface.aidl
package com.sundayting.wancompose;
import com.sundayting.wancompose.IWanServiceCallbackListener;

interface IWanServiceAidlInterface {

    void sendMsg(String msg);

    void registerListener(IWanServiceCallbackListener listener);

    void unregisterListener(IWanServiceCallbackListener listener);

}