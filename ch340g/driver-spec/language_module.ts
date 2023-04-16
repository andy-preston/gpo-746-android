import { RequestCode, Register, RegisterPair } from "./enums.ts";

export enum VariableType {
    boolean = 'boolean',
    byte = 'byte',
}

export interface Variable {
    name: string,
    type: VariableType
}

export interface LanguageModule {
    setTimeout: (useTimeout: number) => void;

    functionHeader: (name: string, parameters?: Array<Variable>) => string;

    functionFooter: () => string;

    output: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair|string,
        value: number
    ) => string;

    input: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        variable: string
    ) => string;

    check: (
        variableName: string,
        value: number
    ) => string;

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: number
    ) => string;

    invertBits: (
        variableName: string
    ) => string;

    defineVariable: (
        variable: Variable,
        initialValue: number,
    ) => string;
}
