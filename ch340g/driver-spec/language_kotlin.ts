import {
    type LanguageModule,
    type Variable,
    type BooleanString,
    booleanStrings
} from "./language_module.ts";
import { type HexNumber } from './hex.ts';
import { BulkInputEndpoint } from './endpoint.ts';
import { type BufferSize } from './buffer_size.ts';
import {
    type ReadRequestCode,
    type WriteRequestCode
} from "./request.ts";
import {
    type ReadRegisterAddress,
    type WriteRegisterAddress
} from "./register.ts";

let timeout = 0;

const typeConversion = {
    boolean: 'Boolean',
    byte: 'Byte',
    integer: 'Int',
} as const;

const defaultValues = {
    boolean: 'false',
    byte: '0',
    integer: '0',
} as const;

const parameterMapper = (parameter: Variable): string =>
    parameter.name + ": " + typeConversion[parameter.type];

const functionParameters = (parameters?: Array<Variable>): string => (
    parameters === undefined ? "" : parameters.map(parameterMapper).join(", ")
);

const language: LanguageModule = {
    epilogue: (): string => "}\n",

    prologue: (useTimeout: number, bufferSize: BufferSize): string => {
        timeout = useTimeout;
        return `package com.gitlab.edgeeffect.gpo746

import com.gitlab.edgeeffect.gpo746.CH340GResult
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDeviceConnection

open class CH340GDriver() {
    private var buffer = ByteArray(${bufferSize})
    private var status = 0
    protected lateinit var usbDeviceConnection: UsbDeviceConnection`;
    },

    functionHeader: (name: string, parameters?: Array<Variable>): string =>
        `\n    public fun ${name}(${functionParameters(parameters)}): CH340GResult {\n`,

    functionFooter: (): string => "        return CH340GResult.Success(0)\n    }",

    read: (
        title: string,
        request: ReadRequestCode,
        register: ReadRegisterAddress,
        variableName: string
    ): string => `        status = usbDeviceConnection.controlTransfer(
            UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_IN,
            ${request},
            ${register},
            0,
            buffer,
            buffer.size,
            ${timeout}
        )
        if (status < 0) {
            return CH340GResult.Error("${title} - error $status")
        }
        ${variableName} = (buffer[1].toUInt().or(buffer[0].toUInt().shl(8))).toInt()\n`,

    bulkRead: (endpoint: BulkInputEndpoint, variableName: string): string =>
        ////////////////////////////////////////////////////////////////////////
        //
        // And how do we access the buffer?
        //
        ////////////////////////////////////////////////////////////////////////
        `        ${variableName} = usbDeviceConnection.bulkTransfer(
            ${endpoint},
            buffer,
            buffer.size,
            ${timeout}
        )\n`,

    write: (
        title: string,
        request: WriteRequestCode,
        register: WriteRegisterAddress|string,
        value: HexNumber
    ): string => `        status = usbDeviceConnection.controlTransfer(
            UsbConstants.USB_TYPE_VENDOR or UsbConstants.USB_DIR_OUT,
            ${request},
            ${register},
            ${value},
            null,
            0,
            ${timeout}
        )
        if (status < 0) {
            return CH340GResult.Error("${title} control out - error $status")
        }\n`,

    check: (variableName: string, value: HexNumber): string =>
        `        if (${variableName} != ${value}) {
            return CH340GResult.Error(
                "${variableName} should be ${value}, but is $${variableName}"
            )
        }\n`,

    setBoolean: (booleanName: string, value: boolean): string =>
        `        ${booleanName} = ${value ? 'true' : 'false'}\n`,

    setBooleanFromBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): string => `        ${booleanValue} = ${bitwiseName}.and(${bitMask}) == ${bitMask}\n`,

    ifConditionSetBit: (
        booleanValue: string,
        bitwiseName: string,
        bitMask: HexNumber
    ): string => {
        const simple = {
            "true": `${bitwiseName}.or(${bitMask}u)`,
            "false": `${bitwiseName}.and(inv(${bitMask}u))`,
        } as const;
        const operation = booleanStrings.includes(booleanValue as BooleanString) ?
            simple[booleanValue as BooleanString] :
            `if (${booleanValue}) ${simple.true} else ${simple.false}`
        return `        ${bitwiseName} = ${operation}\n`;
    },

    invertBits: (variableName: string): string =>
        `        ${variableName} = inv(${variableName})\n`,

    defineVariable: (
        variable: Variable,
        initialValue?: HexNumber
    ): string => {
        // we assume that if there's no initial value, it must be a property
        // and then there's less indentation
        const property = initialValue === undefined;
        const variableType = typeConversion[variable.type]
        const useInitialValue = property ?
            defaultValues[variable.type] :
            initialValue
        return (
            property ? "    protected " : "        "
        ) + `var ${variable.name}: ${variableType} = ${useInitialValue}\n`;
    }

};

export default language;
