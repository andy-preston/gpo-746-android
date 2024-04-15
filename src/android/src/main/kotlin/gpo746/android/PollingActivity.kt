package andyp.gpo746.android

import android.os.Handler
import android.os.Looper

private const val LOOPER_DELAY_MILLISECONDS: Long = 1000

abstract class PollingActivity : IdleActivity() {

    protected val handler = Handler(Looper.getMainLooper())

    protected abstract val pollHandlerForIncoming: Runnable

    protected abstract val pollHandlerForOutgoing: Runnable

    protected fun hookIsUp(): Boolean {
        val hookUp = ch340g.readHandshake()
        hookIndicator.setChecked(hookUp)
        return hookUp
    }

    protected fun hookPolling(pollHandler: Runnable) {
        handler.removeCallbacks(pollHandlerForIncoming)
        handler.removeCallbacks(pollHandlerForOutgoing)
        handler.postDelayed(pollHandler, LOOPER_DELAY_MILLISECONDS)
    }
}
