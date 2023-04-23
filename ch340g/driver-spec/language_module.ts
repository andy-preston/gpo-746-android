import { type HexNumber } from './hex.ts';
import { type RequestCode } from "./request.ts";
import { type RegisterAddress } from "./register.ts";

export type VariableType = "boolean"|"byte";

export type Variable = {
    name: string,
    type: VariableType
};

export type LanguageModule = {
    setTimeout: (useTimeout: number) => void;

    functionHeader: (name: string, parameters?: Array<Variable>) => string;

    functionFooter: () => string;

    output: (
        title: string,
        request: RequestCode,
        register: RegisterAddress|string,
        value: HexNumber
    ) => string;

    input: (
        title: string,
        request: RequestCode,
        register: RegisterAddress,
        variable: string
    ) => string;

    check: (
        variableName: string,
        value: HexNumber
    ) => string;

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => string;

    invertBits: (
        variableName: string
    ) => string;

    defineVariable: (
        variable: Variable,
        initialValue?: HexNumber
    ) => string;
};
