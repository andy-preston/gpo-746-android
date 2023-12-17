package andyp.gpo746.android

import android.telecom.CallAudioState
import android.telecom.Connection as BaseConnection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService as BaseConnectionService
import android.telecom.PhoneAccountHandle

class Connection: BaseConnection() {
    override public fun onShowIncomingCallUi() {
    }

    // This method was deprecated in API level 34
    override public fun onCallAudioStateChanged(state: CallAudioState) {
    }

    override public fun onHold() {
    }

    override public fun onUnhold() {
    }

    override public fun onAnswer() {
    }

    override public fun onReject() {
    }

    override public fun onDisconnect() {
    }
}

class ConnectionService : BaseConnectionService() {
    override public fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): BaseConnection {
        return Connection()
    }

    override public fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ) {
    }

    override public fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): BaseConnection {
        return Connection()
    }

    override public fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ) {
    }
}
