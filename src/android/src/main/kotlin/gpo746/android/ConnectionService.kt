package andyp.gpo746.android

import android.telecom.Connection 
import android.telecom.ConnectionRequest 
import android.telecom.ConnectionService as BaseConnectionService
import android.telecom.PhoneAccountHandle 

class ConnectionService : BaseConnectionService() {
    override public fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest 
    ): Connection {
    }

    override public fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest 
    ) {
    }

    override public fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest 
    ): Connection {
    }
    
    override public fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest 
    ) {
    }
}
