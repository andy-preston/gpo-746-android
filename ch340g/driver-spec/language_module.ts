import { type HexNumber } from './hex.ts';
import { type ReadRequestCode, type WriteRequestCode } from "./request.ts";
import { type ReadRegisterAddress, type WriteRegisterAddress } from "./register.ts";

export type VariableType = "boolean" | "byte" | "integer";

export type Variable = {
    name: string,
    type: VariableType
};

export type LanguageModule = {
    setTimeout: (useTimeout: number) => void;

    functionHeader: (name: string, parameters?: Array<Variable>) => string;

    functionFooter: () => string;

    read: (
        title: string,
        request: ReadRequestCode,
        register: ReadRegisterAddress,
        variableName: string
    ) => string;

    bulkRead:(variableName: string) => string;

    write: (
        title: string,
        request: WriteRequestCode,
        register: WriteRegisterAddress|string,
        value: HexNumber
    ) => string;

    check: (variableName: string, value: HexNumber) => string;

    setBoolean: (booleanName: string, value: boolean) => string;

    setBooleanFromBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => string;

    ifConditionSetBit: (
        booleanName: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => string;

    invertBits: (variableName: string) => string;

    defineVariable: (variable: Variable, initialValue?: HexNumber) => string;
};
