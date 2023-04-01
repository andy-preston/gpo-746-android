enum SystemType { linux, android };

export default {
    "linux": SystemType.linux,
    "android": SystemType.android,

    "values": function(systemType: SystemType): Record<string, unknown> {
        return {
            "usb_timeout": 1000,

            // Request Codes
            "vendor_get_version": 0x5F,
            "vendor_read_registers": 0x95,
            "vendor_write_registers": 0x9A,
            "vendor_serial_init": 0xA1, // Init?? Reset??
            "vendor_modem_ctrl": 0xA4,

            // Registers
            "GCL1": 0x06, // AKA STATUS
            "GCL2": 0x07, // AKA STATUS
            "LCR1": 0x18,
            "LCR2": 0x25,

            // Register Pairs
            // REG_BREAK1 0x05
            "baudrate_1": 0x1312, // Prescaler 12 - Divisor 13
            "baudrate_2": 0x0F2C,
            // REG_BPS_MOD 0x14
            // REG_BPS_PAD 0x0F

            // Modem Control / GCL Bits (depends on version)
            "DTR": 0x20,
            "RTS": 0x40,

            // GCL Bits
            // On Android, these values are in the low nybble,
            // But on Linux/LibUSB we need to use the high nybble.
            // I have no idea why, Other registers are
            // consistent across both implementations.
            "CTS": systemType == SystemType.linux ? 0x10 : 0x01,
            "DSR": systemType == SystemType.linux ? 0x20 : 0x02,
            "RI": systemType == SystemType.linux ? 0x40 : 0x04,
            "DCD": systemType == SystemType.linux ? 0x80 : 0x08,

            // LCR1 Bits
            "LCR1_mask": 0xAF,
            "LCR1_enable_RX": 0x80,
            "LCR1_enable_TX": 0x40,
            "LCR1_parity_enable": 0x08,
            "LCR_CS5": 0x00, // Not defined in FreeBSD, only in NetBSD
            "LCR_CS6": 0x01, // Not defined in FreeBSD, only in NetBSD
            "LCR_CS7": 0x02, // Not defined in FreeBSD, only in NetBSD
            "LCR1_CS8": 0x03, // The only one in FreeBSD

            // LCR2 Bits
            "LCR2_mask": 0x07,
            "LCR2_parity_even": 0x07,  // FreeBSD says 0x07 Linux & NetBSD says 0x10
            "LCR2_parity_odd": 0x06,   // FreeBSD says 0x06         NetBSD says 0x00
            "LCR2_parity_mark": 0x05,  // FreeBSD says 0x05         NetBSD says 0x20
            "LCR2_parity_space": 0x04, // FreeBSD says 0x04         NetBSD says 0x30

            "LCR_stop_bits": 0x04, // Should this actually be STOP_BITS_2

            // LCR Bits
            "LCR_mark_space": 0x20 // Only in the Linux Driver
        };
    }
};
