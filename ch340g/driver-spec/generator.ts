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
import { LanguageModule, Variable } from "./language_module.ts";

export type LanguageFlag = "C" | "Kotlin";

type Steps = {
    read: (
        requestName: ReadRequestName,
        registerName: ReadRegisterName,
        variable: string
    ) => Steps;

    bulkRead: (variableName: string) => Steps;

    write: (
        requestName: WriteRequestName,
        registerName: WriteRegisterName,
        value: HexNumber
    ) => Steps;

    writeControl: (variableName: string) => Steps;

    check: (variableName: string, value: HexNumber) => Steps;

    setBoolean: (booleanName: string, value: boolean) => Steps;

    setBooleanFromBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => Steps;

    ifConditionSetBit: (
        booleanName: string,
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

    bulkRead: (variableName: string): Steps => {
        funcBody = funcBody + languageModule.bulkRead(variableName);
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
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): Steps => {
        funcBody = funcBody + languageModule.setBooleanFromBit(
            booleanName,
            bitwiseName,
            bitMask
        );
        return stepGenerator;
    },

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): Steps => {
        funcBody = funcBody + languageModule.ifConditionSetBit(
            booleanName,
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
        funcBody = funcBody + languageModule.functionFooter();
        console.log(funcBody);
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
    console.log(languageModule.defineVariable(variable));
}

export type Specification = (
    methodGenerator: Method,
    propertyGenerator: Property
) => void;

export const generator = (
    language: LanguageFlag,
    timeout: number,
    buildSpec: Specification
) => {
    import(`./language_${language}.ts`.toLowerCase()).then(
        (module) => {
            languageModule = module.default;
            languageModule.setTimeout(timeout);
            buildSpec(methodGenerator, propertyGenerator);
        }
    );
}
