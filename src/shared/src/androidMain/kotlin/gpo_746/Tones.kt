package gpo_746

final class Tones {
    val dial_tone_8000Hz_8bit_mono = byteArrayOf(
        // I really should do a Fourier analysis of this and work out what it's
        // made of. I started with a 32bit float 44.1 recording and cut it down,
        // dropped the word-length and down-sampled it checking it still sounded
        // right and it's still fine.
          4,   43,   55,   67,   76,   67,   60,   38,   24,    2,
        -17,  -33,  -36,  -41,  -31,  -22,  -10,
          7,   16,   28,   36,   31,   29,   22,    1,
         -8,  -22,  -30,  -34,  -33,  -21,  -20,   -9,
         39,   38,   51,   72,   67,   66,   70,   36,
        // This little `-9, 0,` "blip" offended my sense of symmetry and
        // I tried to cut it out and it drastically altered the timbre.
        // I really should do a Fourier analysis.
         -9,    0,
        -46,  -65,  -83,  -93,  -79,  -83,  -32,  -13,
         20,   59,   64,  116,   98,  111,   93,   80,   60,
        -16,   -6,  -78,  -69,  -90, -118,  -83,  -84,  -42,  -26,
         17,   47,   49,   91,   93,   88,   92,   66,   35,   20,
        -10,  -54,  -48,  -67,  -61,  -56,  -46,  -19,  -8
    )
}
