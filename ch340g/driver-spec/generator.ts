import { type HexNumber } from './hex.ts';
import { requestCode, type RequestName } from "./request.ts";
import { register, type RegisterName } from "./register.ts";
import { LanguageModule, Variable } from "./language_module.ts";

export type LanguageFlag = "C" | "Kotlin";

type Steps = {
    input: (
        title: string,
        requestName: RequestName,
        registerName: RegisterName,
        variable: string
    ) => Steps;

    output: (
        title: string,
        requestName: RequestName,
        registerName: RegisterName,
        value: HexNumber
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
        requestName: RequestName,
        registerName: RegisterName,
        variable: string
    ): Steps => {
        funcBody = funcBody + languageModule.input(
            title,
            requestCode[requestName],
            register[registerName],
            variable
        );
        return stepGenerator;
    },

    output: (
        title: string,
        requestName: RequestName,
        registerName: RegisterName,
        value: HexNumber
    ): Steps => {
        funcBody = funcBody + languageModule.output(
            title,
            requestCode[requestName],
            register[registerName],
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
            requestCode.VendorModemControl,
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
    import(`./language_${language}.ts`.toLowerCase()).then(
        (module) => {
            languageModule = module.default;
            languageModule.setTimeout(timeout);
            buildSpec(methodGenerator, propertyGenerator);
        }
    );
}
