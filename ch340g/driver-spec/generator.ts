import { LanguageModule, Variable } from "./language_module.ts";
import { type HexNumber } from './hex.ts';
import {
    readRequestCode,
    type ReadRequestName,
    writeRequestCode,
    type WriteRequestName
} from "./request.ts";
import {
    readRegister,
    type ReadRegisterName,
    writeRegister,
    type WriteRegisterName
} from "./register.ts";
import { BufferSize } from './buffer_size.ts';
import { type BulkInputEndpoint } from './endpoint.ts';

type Steps = {
    read: (
        requestName: ReadRequestName,
        registerName: ReadRegisterName,
        variable: string
    ) => Steps;

    bulkRead: (endpoint: BulkInputEndpoint, variableName: string) => Steps;

    write: (
        requestName: WriteRequestName,
        registerName: WriteRegisterName,
        value: HexNumber
    ) => Steps;

    writeControl: (variableName: string) => Steps;

    check: (variableName: string, value: HexNumber) => Steps;

    setBoolean: (booleanName: string, value: boolean) => Steps;

    setBooleanFromBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => Steps;

    ifConditionSetBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => Steps;

    invertBits: (variableName: string) => Steps;

    defineVariable: (variable: Variable, initialValue: HexNumber) => Steps;

    end: () => void;
}

export type Method = (name: string, parameters?: Array<Variable>) => Steps;
export type Property = (variable: Variable) => void;

let languageModule: LanguageModule;
let funcBody: string;

const output = (code: string): void => {
    if (code) {
        console.log(code);
    }
}

const stepGenerator: Steps = {
    read: (
        requestName: ReadRequestName,
        registerName: ReadRegisterName,
        variable: string
    ): Steps => {
        funcBody = funcBody + languageModule.read(
            `${requestName} ${registerName} ${variable}`,
            readRequestCode[requestName],
            readRegister[registerName],
            variable
        );
        return stepGenerator;
    },

    bulkRead: (endpoint: BulkInputEndpoint, variableName: string): Steps => {
        funcBody = funcBody + languageModule.bulkRead(endpoint, variableName);
        return stepGenerator;
    },

    write: (
        requestName: WriteRequestName,
        registerName: WriteRegisterName,
        value: HexNumber
    ): Steps => {
        funcBody = funcBody + languageModule.write(
            `${requestName} ${registerName} ${value}`,
            writeRequestCode[requestName],
            writeRegister[registerName],
            value
        );
        return stepGenerator;
    },

    writeControl: (variableName: string): Steps => {
        funcBody = funcBody + languageModule.write(
            `Write handshake control ${variableName}`,
            writeRequestCode.VendorModemControl,
            variableName,
            "0x00"
        );
        return stepGenerator;
    },

    check: (variableName: string, value: HexNumber): Steps => {
        funcBody = funcBody + languageModule.check(variableName, value);
        return stepGenerator;
    },

    setBoolean: (booleanName: string, value: boolean): Steps => {
        funcBody = funcBody + languageModule.setBoolean(booleanName, value);
        return stepGenerator;
    },

    setBooleanFromBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): Steps => {
        funcBody = funcBody + languageModule.setBooleanFromBit(
            booleanValue,
            bitwiseName,
            bitMask
        );
        return stepGenerator;
    },

    ifConditionSetBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): Steps => {
        funcBody = funcBody + languageModule.ifConditionSetBit(
            booleanValue,
            bitwiseName,
            bitMask
        );
        return stepGenerator;
    },

    invertBits: (variableName: string): Steps => {
        funcBody = funcBody + languageModule.invertBits(variableName);
        return stepGenerator;
    },

    defineVariable: (variable: Variable, initialValue: HexNumber): Steps => {
        funcBody = funcBody + languageModule.defineVariable(
            variable,
            initialValue
        );
        return stepGenerator;
    },

    end: (): void => {
        output(funcBody + languageModule.functionFooter());
    }
}

const methodGenerator: Method = (
    name: string,
    parameters?: Array<Variable>
): Steps => {
    funcBody = languageModule.functionHeader(name, parameters);
    return stepGenerator;
}

const propertyGenerator: Property = (variable: Variable): void => {
    output(languageModule.defineVariable(variable));
}

type Specification = (
    methodGenerator: Method,
    propertyGenerator: Property
) => void;

const supportedLanguages = ["C", "Kotlin"] as const;
export type LanguageFlag = (typeof supportedLanguages)[number];

type GeneratorParameters = {
    language: LanguageFlag,
    timeout: number,
    bufferSize: BufferSize,
    buildSpec: Specification
}

export const generator = (parameters: GeneratorParameters) => {
    if (!supportedLanguages.includes(parameters.language)) {
        throw `${parameters.language} is not supported. Only [${supportedLanguages}]`;
    }
    import(`./language_${parameters.language}.ts`.toLowerCase()).then(
        (module) => {
            languageModule = module.default;
            output(languageModule.prologue(
                parameters.timeout,
                parameters.bufferSize
            ));
            parameters.buildSpec(methodGenerator, propertyGenerator);
            output(languageModule.epilogue());
        }
    );
}
