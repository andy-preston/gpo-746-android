import {
    type LanguageModule,
    type Variable,
    type BooleanString,
    booleanStrings
} from "./language_module.ts";
import { type HexNumber } from './hex.ts';
import { BulkInputEndpoint } from './endpoint.ts';
import { type BufferSize } from './buffer_size.ts';
import { type ReadRequestCode, type WriteRequestCode } from "./request.ts";
import {
    type ReadRegisterAddress,
    type WriteRegisterAddress
} from "./register.ts";

let timeout = 0;

const typeConversion = {
    boolean: 'bool',
    byte: 'uint8_t',
    integer: 'int',
} as const;

const parameterMapper = (parameter: Variable): string =>
    typeConversion[parameter.type] + " " + parameter.name;

const functionParameters = (parameters?: Array<Variable>): string => (
    parameters === undefined ? "void" : parameters.map(parameterMapper).join(", ")
);

const language: LanguageModule = {
    epilogue: (): string => "",

    prologue: (useTimeout: number, bufferSize: BufferSize): string => {
        timeout = useTimeout;
        return `int status = 0;

int operationFailed(int operationStatus, char *message) {
    status = operationStatus;
    if (status != 0) {
        fprintf(
            stderr,
            "%s\\n%d - %s\\n",
            message,
            status,
            libusb_error_name(status)
        );
        return false;
    }
    return true;
}

union Buffer {
    uint8_t bytes[${bufferSize}];
    uint16_t words[${bufferSize / 2}];
} buffer;\n`;
    },

    functionHeader: (name: string, parameters?: Array<Variable>): string =>
        `int ${name}(${functionParameters(parameters)}) {\n`,

    functionFooter: (): string => "    return true;\n}\n",

    read: (
        title: string,
        request: ReadRequestCode,
        register: ReadRegisterAddress,
        variableName: string
    ): string => `    if (operationFailed(
        libusb_control_transfer(
            device,
            LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_IN,
            ${request},
            ${register},
            0,
            buffer.bytes,
            sizeof(buffer),
            ${timeout}
        ),
        "Failed to ${title}"
    )) {
        return false;
    }
    ${variableName} = buffer.words[0];\n`,

    bulkRead: (endpoint: BulkInputEndpoint, variableName: string): string =>
        `    if (operationFailed(
        libusb_bulk_transfer(
            device,
            ${endpoint},
            buffer.bytes,
            sizeof(buffer) - 1,
            &${variableName},
            0
        ),
        "Bulk Read Failed"
    )) {
        return false;
    }
    buffer.bytes[${variableName}] = 0;\n`,

    write: (
        title: string,
        request: WriteRequestCode,
        register: WriteRegisterAddress|string,
        value: HexNumber
    ): string =>
        `    if (operationFailed(
        libusb_control_transfer(
            device,
            LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_OUT,
            ${request},
            ${register},
            ${value},
            NULL,
            0,
            ${timeout}
        ),
        "Failed ${title}"
    )) {
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
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): string =>
        `    ${booleanValue} = (${bitwiseName} & ${bitMask}) == ${bitMask};\n`,

    ifConditionSetBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): string => {
        const simple = {
            "true": `(${bitwiseName} | ${bitMask})`,
            "false": `(${bitwiseName} & ~${bitMask})`,
        } as const;
        const operation = booleanStrings.includes(booleanValue as BooleanString) ?
            simple[booleanValue as BooleanString] :
            `${booleanValue} ? ${simple.true} : ${simple.false}`
        return `    ${bitwiseName} = ${operation}\n`;
    },

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
