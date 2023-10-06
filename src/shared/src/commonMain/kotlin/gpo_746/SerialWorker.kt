package gpo_746

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class SerialWorker() {
    private val serialChannel = Channel<String>()
    private var serialJob: Job? = null

    public fun start(usbSystem: UsbSystemInterface) {
        serialJob = GlobalScope.launch {
            // Dummy empty first send, to block and give things time to settle
            serialChannel.send("")
            while (true) {
                serialChannel.send(
                    usbSystem.bulkRead().toByteArray().decodeToString()
                )
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    public fun bulkRead(): String {
        return runBlocking {
            if (serialChannel.isEmpty) "" else serialChannel.receive()
        }
    }
}
