import {default as baud} from "./baud.ts";
import {default as timer1} from "./timer1.ts";

const cpuClockFrequency = 14745600;

console.log(baud(cpuClockFrequency, 9600));
console.log(timer1(cpuClockFrequency).join("\n"));
