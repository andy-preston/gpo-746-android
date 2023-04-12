export const hex = (value: number, digits: number): string =>
    value == 0 ? "0" :
        "0x" + (("0".repeat(digits) + value.toString(16)).slice(-digits))


export const assert = (name: string, expected: number, actual: number): void => {
    if (actual != expected) {
        throw new Error(
            `expecting ${name} to be ${hex(expected, 4)}, not ${hex(actual, 4)}`
        );
    }
}
