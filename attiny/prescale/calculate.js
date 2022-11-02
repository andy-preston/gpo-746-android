import {default as baud} from "./baud.js";
import {default as timer1} from "./timer1.js";

const cpuClockFrequency = 14745600;

console.log(
    baud(cpuClockFrequency, 9600).concat(
        timer1(cpuClockFrequency)
    ).join("\n") + "\n"
);
