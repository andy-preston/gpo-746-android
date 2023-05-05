import { type HexNumber } from './hex.ts';
import { LanguageModule, Variable } from "./language_module.ts";
import { type ReadRequestCode, type WriteRequestCode } from "./request.ts";
import { type ReadRegisterAddress, type WriteRegisterAddress } from "./register.ts";

let timeout = 0;

const typeConversion = {
    boolean: 'bool',
    byte: 'uint8_t',
    integer: 'uint16_t',
};

const parameterMapper = (parameter: Variable): string =>
    typeConversion[parameter.type] + " " + parameter.name;

const functionParameters = (parameters?: Array<Variable>): string => {
    if (parameters === undefined) {
        return 'void';
    }

    return parameters.map(parameterMapper).join(", ");
}

const language: LanguageModule = {
    setTimeout(useTimeout: number): void {
        timeout = useTimeout;
    },

    functionHeader: (name: string, parameters?: Array<Variable>): string =>
        `int ${name}(${functionParameters(parameters)}) {\n`,

    functionFooter: (): string => "    return true;\n}\n",

    read: (
        title: string,
        request: ReadRequestCode,
        register: ReadRegisterAddress,
        variableName: string
    ): string =>
        `    status = libusb_control_transfer(
        device,
        LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_IN,
        ${request},
        ${register},
        0,
        buffer.bytes,
        sizeof(buffer),
        ${timeout}
    );
    if (status < 0) {
        fprintf(stderr, "Failed to ${title}\\n");
        return false;
    }
    ${variableName} = buffer.words[0];\n`,

    bulkRead: (variableName: string): string =>
        `    status = libusb_bulk_transfer(
        device,
        0x02 | LIBUSB_ENDPOINT_IN,
        buffer.bytes,
        sizeof(buffer) - 1,
        &${variableName},
        0
    );
    if (status < 0) {
        fprintf(stderr, "Bulk Read Failed\n");
        return;
    }
    buffer.bytes[${variableName}] = 0;\n`,

    write: (
        title: string,
        request: WriteRequestCode,
        register: WriteRegisterAddress|string,
        value: HexNumber
    ): string =>
        `    status = libusb_control_transfer(
        device,
        LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_OUT,
        ${request},
        ${register},
        ${value},
        NULL,
        0,
        ${timeout}
    );
    if (status < 0) {
        fprintf(stderr, "Failed ${title}\\n");
        return false;
    }\n`,

    check: (variableName: string, value: HexNumber): string =>
        `    if (${variableName} != ${value}) {
        fprintf(
            stderr,
            "${variableName} should be ${value}, but is %08x\\n",
            ${variableName}
        );
    }\n`,

    setBoolean: (booleanName: string, value: boolean): string =>
        `    ${booleanName} = ${value ? 'true' : 'false'};\n`,

    setBooleanFromBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): string =>
        `    ${booleanName} = (${bitwiseName} & ${bitMask}) == ${bitMask};\n`,

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): string =>
        `    ${bitwiseName} = ${booleanName} ?
        (${bitwiseName} | ${bitMask}) :
        (${bitwiseName} & ~${bitMask});\n`,

    invertBits: (variableName: string): string =>
        `    ${variableName} = ~${variableName};\n`,

    defineVariable: (
        variable: Variable,
        initialValue?: HexNumber
    ): string => {
        // we assume that if there's no initial value, it must be a global
        // and then there's no indentation
        const global = initialValue === undefined;
        return (
            global ? "" : "    "
        ) + `${typeConversion[variable.type]} ${variable.name}` + (
            global ? "" : ` = ${initialValue}`
        ) + ";\n"
    }

};

export default language;
