import { type HexNumber } from './hex.ts';
import { LanguageModule, Variable } from "./language_module.ts";
import { type RequestCode } from "./request.ts";
import { type RegisterAddress } from "./register.ts";

let timeout = 0;

const typeConversion = {
    boolean: 'bool',
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
        register: RegisterAddress|string,
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

    input: (
        title: string,
        request: RequestCode,
        register: RegisterAddress,
        variableName: string
    ): string =>
        `   status = libusb_control_transfer(
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

    check: (
        variableName: string,
        value: HexNumber
    ): string =>
        `    if (${variableName} != ${value}) {
        fprintf(stderr, "${variableName} should be ${value}, but is %08x\\n", ${variableName});
    }\n`,

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): string =>
        `    ${bitwiseName} = ${booleanName} ?
        (${bitwiseName} | ${bitMask}) :
        (${bitwiseName} & ~${bitMask});\n`,

    invertBits: (
        variableName: string
    ): string => `    ${variableName} = ~${variableName};\n`,

    defineVariable: (
        variable: Variable,
        initialValue?: HexNumber
    ): string =>
        `    ${typeConversion[variable.type]} ${variable.name}` + (
            initialValue !== undefined ? ` = ${initialValue}` : ""
        ) + ";\n"

};

export default language;
