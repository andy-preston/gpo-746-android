import { type HexNumber } from './hex.ts';
import { type ReadRequestCode, type WriteRequestCode } from "./request.ts";
import { type ReadRegisterAddress, type WriteRegisterAddress } from "./register.ts";
import { BulkInputEndpoint } from './endpoint.ts';
import { BufferSize } from './buffer_size.ts';

export const booleanStrings = ["true", "false"] as const;
export type BooleanString = (typeof booleanStrings)[number];

export type VariableType = "boolean" | "byte" | "integer";

export type Variable = {
    name: string,
    type: VariableType
};

export type LanguageModule = {
    prologue: (useTimeout: number, bufferSize: BufferSize) => string;
    epilogue: () => string;

    functionHeader: (name: string, parameters?: Array<Variable>) => string;

    functionFooter: () => string;

    read: (
        title: string,
        request: ReadRequestCode,
        register: ReadRegisterAddress,
        variableName: string
    ) => string;

    bulkRead:(endpoint: BulkInputEndpoint, variableName: string) => string;

    write: (
        title: string,
        request: WriteRequestCode,
        register: WriteRegisterAddress|string,
        value: HexNumber
    ) => string;

    check: (variableName: string, value: HexNumber) => string;

    setBoolean: (booleanName: string, value: boolean) => string;

    setBooleanFromBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => string;

    ifConditionSetBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => string;

    invertBits: (variableName: string) => string;

    defineVariable: (variable: Variable, initialValue?: HexNumber) => string;
};
