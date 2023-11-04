package gpo_746

import android.media.AudioTrack
import android.util.Log
import java.lang.Thread

final class ToneThread(sampleBytes: ByteArray, audioTrack: AudioTrack): Thread() {

    // Shared mutable state - Ooooooooh!
    var playing: Boolean = true

    val samples = sampleBytes
    val track = audioTrack

    override public fun run() {
        var starting: Boolean = true
        try {
            while (playing) {
                track.write(samples, 0, samples.size)
                if (starting) {
                    track.play()
                    starting = false
                }
            }
        } catch (e: Exception) {
            Log.e("gpo746", e.toString())
        } catch (e: OutOfMemoryError) {
            Log.e("gpo746", e.toString())
        }
        track.stop()
    }

    public fun stopPlaying() {
        playing = false;
    }
}
