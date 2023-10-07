""""GPO-746 Microcontroller Schematic"""

import schemdraw
import schemdraw.elements as elm

elm.style(elm.STYLE_IEC)

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
        elm.IcPin(name="TXD", side="right", pin="3"),
        elm.IcPin(name="RXD", side="right", pin="2"),
    ],
    pinspacing=1.5,
    leadlen=1,
    label="ATTiny\n2313",
)

_340 = elm.Ic(
    pins=[
        elm.IcPin(name="XO", side="left", pin="8"),
        elm.IcPin(name="XI", side="left", pin="7"),
        elm.IcPin(name="V3", side="left", pin="4"),
        elm.IcPin(name=r"$\overline{RI}$", side="left", pin="11", anchorname="RI"),
        elm.IcPin(name=r"$\overline{RTS}$", side="left", pin="14", anchorname="RTS"),
        elm.IcPin(name="RXD", side="left", pin="3"),
        elm.IcPin(name="TXD", side="left", pin="2"),
        elm.IcPin(name=r"$\overline{DTR}$", side="right", pin="13", anchorname="DTR"),
        elm.IcPin(name="UD+", side="top", pin="5"),
        elm.IcPin(name="UD-", side="top", pin="6"),
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


def schematic(dwg: schemdraw.Drawing):
    """Main body of the drawing"""

    dwg.config(fontsize=12)

    dwg += _2313

    dwg.move(17, 0)
    dwg += _340
    dwg += elm.Crystal().endpoints(_340.XI, _340.XO).label("12 MHz")
    dwg += elm.Capacitor().at(_340.XO).left().label("22pf")
    dwg += elm.Capacitor().at(_340.XI).left().label("22pf")
    dwg += elm.Line().down().toy(_340.XO)
    dwg += elm.Wire("|-").to(_340.GND)
    dwg += elm.Line().to(_2313.GND)

    dwg += elm.Capacitor().at(_340.V3).left().label("100n")
    dwg += elm.Line().down().toy(_340.XI)
    dwg += elm.Line().at(_340.RI).left()
    dwg.push()
    dwg += elm.Line().to(_2313.PB0)
    dwg += elm.Line().at(_340.RTS).to(_2313.PD6)
    dwg.pop()
    dwg += elm.Resistor().down().toy(_340.V3).label("10K")
    dwg += elm.Dot(open=True).at(_340.DTR)
    dwg += elm.Line().up().at(_340.VCC)
    dwg.push()
    dwg += elm.Line().tox(_2313.VCC)
    dwg.pop()
    dwg += elm.Resistor().right(2).label("1K")
    dwg += elm.LED().right(3)
    dwg += elm.Line().down().toy(_340.VCC)
    dwg.push()
    dwg += elm.Capacitor().to(_340.VCC).label("100n")
    dwg.pop()
    dwg += elm.Wire("|-").to(_340.GND)

    dwg += elm.Dot(open=True).at(_2313.MOSI)
    dwg += elm.Dot(open=True).at(_2313.MISO)
    dwg += elm.Dot(open=True).at(_2313.UCSK)
    dwg += elm.Dot(open=True).at(_2313.PB4)
    dwg += elm.Dot(open=True).at(_2313.PD2)

    dwg += elm.Resistor().at(_2313.PB3).right().label("220Ω")
    dwg += elm.LED().down().toy(_2313.GND)

    dwg += elm.Resistor().at(_2313.reset).left(2).label("1K")
    dwg += elm.Line().up(6.333)
    dwg += elm.Wire("-|").to(_2313.VCC)

    dwg += elm.Crystal().endpoints(_2313.XTAL1, _2313.XTAL2).label("14.7456 MHz")
    dwg += elm.Capacitor().at(_2313.XTAL2).left().label("22pf")
    dwg += elm.Capacitor().at(_2313.XTAL1).left().label("22pf")
    dwg += elm.Line().down().toy(_2313.XTAL2)
    dwg += elm.Wire("|-").to(_2313.GND)

    dwg += elm.Line().endpoints(_2313.TXD, _340.RXD)
    dwg += elm.Line().endpoints(_2313.RXD, _340.TXD)
