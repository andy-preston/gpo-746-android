package andyp.gpo746.android

import android.os.Handler
import android.os.Looper

private const val LOOPER_DELAY_MILLISECONDS: Long = 1000

abstract class PollingActivity : IdleActivity() {

    private val handler = Handler(Looper.getMainLooper())

    protected open fun pollIncoming() {
        handler.removeCallbacks(pollHandlerForOutgoing)
        handler.postDelayed(pollHandlerForIncoming, LOOPER_DELAY_MILLISECONDS)
    }

    private val pollHandlerForIncoming = object : Runnable {
        override fun run() {
            pollIncoming()
        }
    }

    protected open fun pollOutgoing() {
        handler.removeCallbacks(pollHandlerForIncoming)
        handler.postDelayed(pollHandlerForOutgoing, LOOPER_DELAY_MILLISECONDS)
    }

    private val pollHandlerForOutgoing = object : Runnable {
        override fun run() {
            pollOutgoing()
        }
    }

    protected fun hookIsUp(): Boolean {
        val hookUp = ch340g.readHandshake()
        hookIndicator.setChecked(hookUp)
        return hookUp
    }
}
