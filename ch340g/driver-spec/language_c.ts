import { LanguageModule, VariableType, Variable } from "./language_module.ts";
import { RequestCode, Register, RegisterPair } from "./enums.ts";
import { hex } from './values.ts';

let timeout = 0;

const typeConversion = {
    boolean: 'uint8_t',
    byte: 'uint8_t'
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

    output: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair|string,
        value: number
    ): string =>
        `    status = libusb_control_transfer(
        device,
        LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_OUT,
        ${hex(request, 2)},
        ${typeof register == "number" ? hex(register, 4) : register},
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
        variableName: string
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
    ${variableName} = intBuffer[0];\n`,

    check: (
        variableName: string,
        value: number
    ): string =>
        `    if (${variableName} != ${hex(value, 4)}) {
        fprintf(stderr, "${variableName} should be %08x, but is %08x\\n", ${value}, ${variableName});
    }\n`,

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: number
    ): string =>
        `    ${bitwiseName} = ${booleanName} ?
        (${bitwiseName} | ${hex(bitMask, 2)}) :
        (${bitwiseName} & ~${hex(bitMask, 2)});\n`,

    invertBits: (
        variableName: string
    ): string => `    ${variableName} = ~${variableName};\n`,

    defineVariable: (
        variable: Variable,
        initialValue: number,
    ): string =>
        `    ${typeConversion[variable.type]} ${variable.name} = ${initialValue};\n`

};

export default language;
