import {default as baud} from "./baud.ts";
import {default as timer1} from "./timer1.ts";

const cpuClockFrequency : number = 14745600;

console.log(
    baud(cpuClockFrequency, 9600).concat(
        timer1(cpuClockFrequency)
    ).join("\n") + "\n"
);
