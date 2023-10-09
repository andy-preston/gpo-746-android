""""GPO-746 Microcontroller Schematic"""

import schemdraw
import schemdraw.elements as elm


_2313 = elm.Ic(
    pins=[
        elm.IcPin(name="GND", side="bot", pin="10"),
        elm.IcPin(name="VCC", side="top", pin="20"),
        elm.IcPin(name="MOSI", side="top", pin="17"),
        elm.IcPin(name="MISO", side="top", pin="18"),
        elm.IcPin(name="UCSK", side="top", pin="19"),
        elm.IcPin(name="XTAL2", side="left", pin="5"),
        elm.IcPin(name="XTAL1", side="left", pin="4"),
        elm.IcPin(name="PD5", side="left", pin="9"),
        elm.IcPin(name="PD4", side="left", pin="8"),
        elm.IcPin(name="PD3", side="left", pin="7"),
        elm.IcPin(name="PB2", side="left", pin="14"),
        elm.IcPin(name="PB1", side="left", pin="13"),
        elm.IcPin(name=r"$\overline{RESET}$", side="left", pin="1", anchorname="reset"),
        elm.IcPin(name="PB3", side="right", pin="15"),
        elm.IcPin(name="PB4", side="right", pin="16"),
        elm.IcPin(name="PD2", side="right", pin="6"),
        elm.IcPin(name="PB0", side="right", pin="12"),
        elm.IcPin(name="PD6", side="right", pin="11"),
        elm.IcPin(name="RXD", side="right", pin="2"),
        elm.IcPin(name="TXD", side="right", pin="3"),
    ],
    pinspacing=1.5,
    leadlen=1,
    label="ATTiny 2313",
)

_340 = elm.Ic(
    pins=[
        elm.IcPin(name="XO", side="left", pin="8"),
        elm.IcPin(name="XI", side="left", pin="7"),
        elm.IcPin(name="V3", side="left", pin="4"),
        elm.IcPin(name=r"$\overline{RI}$", side="left", pin="11", anchorname="RI"),
        elm.IcPin(name=r"$\overline{RTS}$", side="left", pin="14", anchorname="RTS"),
        elm.IcPin(name="TXD", side="left", pin="2"),
        elm.IcPin(name="RXD", side="left", pin="3"),
        elm.IcPin(name=r"$\overline{DTR}$", side="right", pin="13", anchorname="DTR"),
        elm.IcPin(name="UD+", side="top", pin="5", anchorname="UDP"),
        elm.IcPin(name="UD-", side="top", pin="6", anchorname="UDM"),
        elm.IcPin(name="VCC", side="top", pin="16"),
        elm.IcPin(name="GND", side="bot", pin="1"),
        elm.IcPin(name=r"$\overline{CTS}$", side="bot", pin="9", anchorname="CTS"),
        elm.IcPin(name=r"$\overline{DSR}$", side="bot", pin="10", anchorname="DSR"),
        elm.IcPin(name=r"$\overline{DCD}$", side="bot", pin="12", anchorname="DCD"),
        elm.IcPin(name="R232", side="bot", pin="15"),
    ],
    pinspacing=1.8,
    leadlen=1,
    label="CH340G",
)


def _340_clock(dwg: schemdraw.Drawing):
    """Clock crystal and caps for CH340G"""
    dwg += elm.Crystal().endpoints(_340.XI, _340.XO).label("12 MHz")
    dwg += elm.Capacitor().at(_340.XO).left().label("22pf", loc="bottom")
    dwg += elm.Capacitor().at(_340.XI).left().label("22pf")
    dwg += elm.Line().down().toy(_340.XO)
    dwg += elm.Wire("|-").to(_340.GND)


def _2313_clock(dwg: schemdraw.Drawing) -> float:
    """Clock crystal and caps for ATTiny 2313"""
    dwg += elm.Crystal().endpoints(_2313.XTAL1, _2313.XTAL2).label("14.7456 MHz")
    dwg += elm.Capacitor().at(_2313.XTAL2).left().label("22pf", loc="bottom")
    dwg += elm.Capacitor().at(_2313.XTAL1).left().label("22pf")
    dwg += elm.Line().down().toy(_2313.XTAL2)
    the_left = dwg.here.x
    dwg += elm.Wire("|-").to(_2313.GND)
    return the_left


def not_connected(dwg: schemdraw.Drawing):
    """pins of the ICs that are not connected to anything"""
    for pin in [_340.DTR, _2313.MOSI, _2313.MISO, _2313.UCSK, _2313.PB4, _2313.PD2]:
        dwg += elm.NoConnect().at(pin)


def interconnect(dwg: schemdraw.Drawing):
    """simple interconnects from one IC to the other"""
    dwg += elm.Line().endpoints(_340.RI, _2313.PB0)
    dwg += elm.Line().endpoints(_340.RTS, _2313.PD6)
    dwg += elm.Line().endpoints(_340.RXD, _2313.TXD)
    dwg += elm.Line().endpoints(_340.TXD, _2313.RXD)
    dwg += elm.Line().endpoints(_340.GND, _2313.GND)


def main_vcc(dwg: schemdraw.Drawing) -> float:
    """VCC line across the top and define where the top actually is"""
    dwg += elm.Line().up().at(_340.VCC)
    the_top: float = dwg.here.y
    dwg += elm.Wire("-|").to(_2313.VCC)
    return the_top


def io_header(dwg: schemdraw.Drawing):
    """A header connecting the IO pins to the 'analog' circuitry"""
    header = elm.Header(
        rows=5, pinsleft=["B1", "B2", "D3", "D4", "D5"], pinalignleft="center"
    )
    dwg.move_from(_2313.PD4, -1.5, 0)
    dwg += header
    dwg += elm.Line().at(_2313.PB1).to(header.pin1)
    dwg += elm.Line().at(_2313.PB2).to(header.pin2)
    dwg += elm.Line().at(_2313.PD3).to(header.pin3)
    dwg += elm.Line().at(_2313.PD4).to(header.pin4)
    dwg += elm.Line().at(_2313.PD5).to(header.pin5)


def usb(dwg: schemdraw.Drawing):
    """The USB header and USB/main power selection switch"""
    header = elm.Header(
        rows=4, pinsleft=["5V", "D-", "D+", "GND"], pinalignleft="center"
    )
    dwg += header
    dwg += elm.Line().down(0.3).at(header.pin4)
    dwg += elm.GroundChassis()
    dwg += elm.Wire("-|").at(_340.UDP).to(header.pin3)
    dwg += elm.Wire("|-").at(_340.UDM).to(header.pin2)
    dwg += (
        elm.Switch()
        .at(header.pin1)
        .right()
        .label(
            label="Open when PSU is\nconnected. Closed\nonly for USB testing",
            halign="right",
            ofst=(1.4, 0.1),
        )
    )
    dwg += elm.Line().up(1.8)


def diagnostic_led(dwg: schemdraw.Drawing):
    """LED and CL Resistor for PB3 diagnostic output"""
    dwg += elm.Resistor().at(_2313.PB3).right().label("220Ω")
    dwg += elm.LED().down().toy(_2313.GND).label("diagnostic", loc="top")


def rx_led(dwg: schemdraw.Drawing):
    """LED and CL Resistor for RX data indicator"""
    dwg += elm.Resistor().down().label("1K")
    dwg += elm.LED().down(3.2).label("RX", loc="top")


def _2313_reset(dwg: schemdraw.Drawing, the_top: float):
    """Pull up resistor on 2313 reset pin"""
    dwg += elm.Resistor().at(_2313.reset).left(1.5).label("1K")
    dwg += elm.Line().toy(the_top)


def _340_decoupling(dwg: schemdraw.Drawing, the_top: float):
    """Decoupling cap for the CH340G"""
    dwg.here = (_340.VCC.x, the_top)
    dwg += elm.Line().right(4.5)
    dwg += elm.Capacitor().down().toy(_340.GND).label("100n")
    dwg += elm.Line().to(_340.GND)


def _2313_decoupling(dwg: schemdraw.Drawing, the_top: float, the_left: float):
    """Decoupling cap for the ATTiny 2313"""
    dwg.here = (the_left, the_top)
    dwg += elm.Line().tox(_2313.VCC).hold()
    dwg += elm.Capacitor().down().toy(_2313.GND).label("100n")


def v3_and_ri_stuff(dwg: schemdraw.Drawing):
    """CH340G - a cap to ground on V3 and a pull-down resistor for RI"""
    dwg += elm.Capacitor().at(_340.V3).left().label("100n")
    dwg += elm.Line().down().toy(_340.XI).hold()
    dwg += elm.Resistor().up(1.666666).label("10K")


def digital(dwg: schemdraw.Drawing):
    """Digital part of the drawing"""

    dwg += _2313.right()
    dwg.move(17, 0)
    dwg += _340.right()

    the_top = main_vcc(dwg)
    interconnect(dwg)
    not_connected(dwg)

    _340_clock(dwg)
    _340_decoupling(dwg, the_top)
    v3_and_ri_stuff(dwg)
    dwg.move_from(_340.UDP, -5, -0.9)
    usb(dwg)
    dwg.move_from(_340.UDP, -8, 3)
    rx_led(dwg)

    the_left: float = _2313_clock(dwg)
    _2313_reset(dwg, the_top)
    _2313_decoupling(dwg, the_top, the_left)
    io_header(dwg)
    diagnostic_led(dwg)
