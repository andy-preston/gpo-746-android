import {default as baud} from "./baud.ts";
import {default as timer1} from "./timer1.ts";
import {default as code} from "./code.ts";

const cpuClockFrequency = 14745600;

console.log(code(
    baud(cpuClockFrequency, 9600).concat(timer1(cpuClockFrequency))
));
