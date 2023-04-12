import { RequestCode, Register, RegisterPair } from "./enums.ts";
import { LanguageModule } from "./language_module.ts";

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

    check: (
        variable: string,
        value: number
    ) => Steps;

    end: () => void;
}

export type Method = (name: string) => Steps;

let languageModule: LanguageModule;

let funcBody: string;

const stepGenerator: Steps = {
    input: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        variable: string
    ) => {
        funcBody = funcBody + languageModule.input(title, request, register, variable);
        return stepGenerator;
    },

    output: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        value: number
    ) => {
        funcBody = funcBody + languageModule.output(title, request, register, value);
        return stepGenerator;
    },

    check: (
        variable: string,
        value: number
    ) => {
        funcBody = funcBody + languageModule.check(variable, value);
        return stepGenerator;
    },

    end: () => {
        funcBody = funcBody + languageModule.functionFooter();
        console.log(funcBody);
    }
}

const methodGenerator: Method = (name: string): Steps => {
    funcBody = languageModule.functionHeader(name);
    return stepGenerator;
}

export type Specification = (methodGenerator: Method) => void;

export const generator = (
    language: LanguageFlag,
    timeout: number,
    buildSpec: Specification
) => {
    import(`./language_${language}.ts`).then(
        (module) => {
            languageModule = module.default;
            languageModule.setTimeout(timeout);
            buildSpec(methodGenerator);
        }
    );
}
