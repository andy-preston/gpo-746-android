import { RequestCode, Register, RegisterPair } from "./enums.ts";

export interface LanguageModule {
    setTimeout: (useTimeout: number) => void;

    functionHeader: (name: string) => string;

    functionFooter: () => string;

    output: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        value: number
    ) => string;

    input: (
        title: string,
        request: RequestCode,
        register: Register|RegisterPair,
        variable: string
    ) => string;

    check: (
        variable: string,
        value: number
    ) => string;
}
