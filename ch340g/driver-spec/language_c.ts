import { LanguageModule } from "./language_module.ts";
import { RequestCode, Register, RegisterPair } from "./enums.ts";
import { hex } from './values.ts';

let timeout = 0;

const language: LanguageModule = {
    setTimeout(useTimeout: number): void {
        timeout = useTimeout;
    },

    functionHeader: (name: string): string => `int ${name}(void) {\n`,

    functionFooter: (): string => "    return true;\n}\n",

    output: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        value: number
    ): string =>
        `    status = libusb_control_transfer(
        device,
        LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_OUT,
        ${hex(request, 2)},
        ${hex(register, 4)},
        ${hex(value, 4)},
        NULL,
        0,
        ${timeout}
    );
    if (status < 0) {
        fprintf(stderr, "Failed ${title}\\n");
        return false;
    }\n`,

    input: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        variable: string
    ): string =>
        `   status = libusb_control_transfer(
        device,
        LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_IN,
        ${hex(request, 2)},
        ${hex(register, 4)},
        0,
        byteBuffer,
        sizeof(byteBuffer),
        ${timeout}
    );
    if (status < 0) {
        fprintf(stderr, "Failed to ${title}\\n");
        return false;
    }
    ${variable} = intBuffer[0];\n`,

    check: (
        variable: string,
        value: number
    ): string =>
        `    if (${variable} != ${hex(value, 4)}) {
        fprintf(stderr, "${variable} should be %08x, but is %08x\\n", ${value}, ${variable});
    }\n`
};

export default language;
