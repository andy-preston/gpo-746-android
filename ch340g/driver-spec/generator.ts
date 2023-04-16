import { RequestCode, Register, RegisterPair } from "./enums.ts";
import { LanguageModule, Variable } from "./language_module.ts";

export enum LanguageFlag {
    C = "c",
    Kotlin = "kotlin"
}

type Steps = {
    input: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        variable: string
    ) => Steps;

    output: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        value: number
    ) => Steps;

    modemControl: (
        title: string,
        variableName: string
    ) => Steps;

    check: (
        variableName: string,
        value: number
    ) => Steps;

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: number
    ) => Steps;

    invertBits: (
        variableName: string
    ) => Steps;

    defineVariable: (
        variable: Variable,
        initialValue: number
    ) => Steps;

    end: () => void;
}

export type Method = (name: string, parameters?: Array<Variable>) => Steps;
export type Property = (variable: Variable) => void;

let languageModule: LanguageModule;

let funcBody: string;

const stepGenerator: Steps = {
    input: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        variable: string
    ): Steps => {
        funcBody = funcBody + languageModule.input(
            title,
            request,
            register,
            variable
        );
        return stepGenerator;
    },

    output: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        value: number
    ): Steps => {
        funcBody = funcBody + languageModule.output(
            title,
            request,
            register,
            value
        );
        return stepGenerator;
    },

    modemControl: (
        title: string,
        variableName: string
    ): Steps => {
        funcBody = funcBody + languageModule.output(
            title,
            RequestCode.VendorModemControl,
            variableName,
            0
        );
        return stepGenerator;
    },

    check: (
        variableName: string,
        value: number
    ): Steps => {
        funcBody = funcBody + languageModule.check(variableName, value);
        return stepGenerator;
    },

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: number
    ): Steps => {
        funcBody = funcBody + languageModule.ifConditionSetBit(
            booleanName,
            bitwiseName,
            bitMask
        );
        return stepGenerator;
    },

    invertBits: (
        variableName: string
    ): Steps => {
        funcBody = funcBody + languageModule.invertBits(variableName);
        return stepGenerator;
    },

    defineVariable: (
        variable: Variable,
        initialValue: number
    ): Steps => {
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
    import(`./language_${language}.ts`).then(
        (module) => {
            languageModule = module.default;
            languageModule.setTimeout(timeout);
            buildSpec(methodGenerator, propertyGenerator);
        }
    );
}
