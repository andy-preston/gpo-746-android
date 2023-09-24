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
        // Can't say I'm mad keen on that "string" there
        // It's so we can do requestCode.VendorModemControl and what's usually
        // a register is then a variable
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

    /* We don't end up with particularly idiomatic C using these methods
     * for bit manipulation. But I think it's a good compromise to get both
     * Kotlin and C out of the same spec. Ironically, the C code it does produce
     * is probably closer to the machine code it'll eventually compile down to
     */

    ifConditionSetBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ) => string;

    invertBits: (variableName: string) => string;

    defineVariable: (variable: Variable, initialValue?: HexNumber) => string;
};
