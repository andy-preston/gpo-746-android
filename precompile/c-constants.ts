import {default as constants} from "./constants.ts";
import {default as code} from "./code.ts";

const c_constants = function(
    constants: Record<string, unknown>
): Array<string> {
    const result: Array<string> = [];
    for (const [name, value] of Object.entries(constants)) {
        result.push("#define " + name.toUpperCase() + " " + value);
    }
    return result;
}

console.log(code(c_constants(constants.values(constants.linux))));
