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

ioHeader = elm.Header(
    rows=5, pinsleft=["B1", "B2", "D3", "D4", "D5"], pinalignleft="center"
)
solenoidHeader = elm.Header(rows=2, pinsleft=["B1", "B2"], pinalignleft="center")
usb = elm.Header(rows=4, pinsleft=["5V", "D-", "D+", "GND"], pinalignleft="center")
q1 = elm.BjtNpn().right()
q2 = elm.BjtNpn().right()
solenoid1 = elm.Transformer(t1=7, t2=0).right()
solenoid2 = elm.Transformer(t1=7, t2=0).right()


def schematic(dwg: schemdraw.Drawing):
    """Main body of the drawing"""
    dwg.config(fontsize=12)

    dwg += solenoidHeader
    dwg.move_from(solenoidHeader.pin1, 4, 2)
    dwg += q1
    dwg += elm.Resistor().at(q1.base).left(2).label("4K7")
    dwg += elm.Wire("|-").to(solenoidHeader.pin1)
    dwg += elm.Ground().at(q1.emitter)
    dwg += elm.Line().at(q1.collector).right()
    dwg.push()
    dwg += elm.Diode().down().label("1N4001")
    dwg += elm.Line().right(1)
    dwg += solenoid1
    dwg.pop()
    dwg += elm.Wire("-|").to(solenoid1.p1)

    dwg.move_from(solenoidHeader.pin2, 4, -2)
    dwg += q2
    dwg += elm.Resistor().at(q2.base).left(2).label("4K7")
    dwg += elm.Wire("|-").to(solenoidHeader.pin2)
    dwg += elm.Ground().at(q2.emitter)
    dwg += elm.Line().at(q2.collector).right()
    dwg.push()
    dwg += elm.Diode().down().label("1N4001")
    dwg += elm.Line().right(1)
    dwg += solenoid2
    dwg.pop()
    dwg += elm.Wire("-|").to(solenoid2.p1)

    dwg.move_from(solenoidHeader.pin2, 4, -25)
    dwg += _2313.right()
    dwg.move(17, 0)
    dwg += _340.right()

    dwg += elm.Crystal().endpoints(_340.XI, _340.XO).label("12 MHz")
    dwg += elm.Capacitor().at(_340.XO).left().label("22pf", loc="bottom")
    dwg += elm.Capacitor().at(_340.XI).left().label("22pf")
    dwg += elm.Line().down().toy(_340.XO)
    dwg += elm.Wire("|-").to(_340.GND)
    dwg += elm.Line().to(_2313.GND)

    dwg.move_from(_340.UDP, -8, -1.5)
    dwg += usb
    dwg += elm.Line().down(0.3).at(usb.pin4)
    dwg += elm.Ground()
    dwg += elm.Line().at(_340.UDP).left(2)
    dwg += elm.Wire("|-").to(usb.pin3)
    dwg += elm.Line().at(_340.UDM).up(1)
    dwg += elm.Line().left(6)
    dwg += elm.Wire("|-").to(usb.pin2)
    dwg += elm.Switch().at(usb.pin1).right()
    dwg += elm.Line().up(2.4)

    dwg += elm.Capacitor().at(_340.V3).left().label("100n")
    dwg += elm.Line().down().toy(_340.XI)
    dwg += elm.Line().at(_340.RI).left()
    dwg.push()
    dwg += elm.Line().to(_2313.PB0)
    dwg += elm.Line().at(_340.RTS).to(_2313.PD6)
    dwg += elm.Line().endpoints(_340.RXD, _2313.TXD)
    dwg += elm.Line().endpoints(_340.TXD, _2313.RXD)
    dwg.pop()
    dwg += elm.Resistor().down().toy(_340.V3).label("10K")
    dwg += elm.NoConnect().at(_340.DTR)
    dwg += elm.Line().up().at(_340.VCC)
    dwg.push()
    dwg += elm.Line().tox(_2313.VCC)
    dwg.pop()
    dwg += elm.Resistor().right(2).label("1K")
    dwg += elm.LED().right(3).label("power")
    dwg += elm.Line().down().toy(_340.VCC)
    dwg.push()
    dwg += elm.Capacitor().to(_340.VCC).label("100n")
    dwg.pop()
    dwg += elm.Wire("|-").to(_340.GND)

    dwg.move_from(_2313.PD4, -1.5, 0)
    dwg += ioHeader
    dwg += elm.Line().at(_2313.PB1).to(ioHeader.pin1)
    dwg += elm.Line().at(_2313.PB2).to(ioHeader.pin2)
    dwg += elm.Line().at(_2313.PD3).to(ioHeader.pin3)
    dwg += elm.Line().at(_2313.PD4).to(ioHeader.pin4)
    dwg += elm.Line().at(_2313.PD5).to(ioHeader.pin5)
    dwg += elm.NoConnect().at(_2313.MOSI)
    dwg += elm.NoConnect().at(_2313.MISO)
    dwg += elm.NoConnect().at(_2313.UCSK)
    dwg += elm.NoConnect().at(_2313.PB4)
    dwg += elm.NoConnect().at(_2313.PD2)

    dwg += elm.Resistor().at(_2313.PB3).right().label("220Ω")
    dwg += elm.LED().down().toy(_2313.GND).label("diagnostic", loc="top")
    dwg += elm.Resistor().at(_2313.reset).left(2).label("1K")
    dwg += elm.Line().up(6.333)
    dwg.push()
    dwg += elm.Wire("-|").to(_2313.VCC)
    dwg.pop()
    dwg += elm.Line().left(1)
    dwg += elm.Capacitor().down().toy(_2313.XTAL1).label("100n")
    dwg += elm.Crystal().endpoints(_2313.XTAL1, _2313.XTAL2).label("14.7456 MHz")
    dwg += elm.Capacitor().at(_2313.XTAL2).left().label("22pf", loc="bottom")
    dwg += elm.Capacitor().at(_2313.XTAL1).left().label("22pf")
    dwg += elm.Line().down().toy(_2313.XTAL2)
    dwg += elm.Wire("|-").to(_2313.GND)
