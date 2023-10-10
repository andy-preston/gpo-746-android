""""GPO-746 Microcontroller Schematic - 'Analog' part"""

import schemdraw
import schemdraw.elements as elm


def psu(dwg: schemdraw.Drawing) -> schemdraw.util.Point:
    """The 5V power supply"""
    _7805 = elm.Ic(
        pins=[
            elm.IcPin(side="left", pin="1", anchorname="input"),
            elm.IcPin(side="bottom", pin="2", anchorname="ground"),
            elm.IcPin(side="right", pin="3", anchorname="output"),
        ],
        leadlen=1,
        label="7805",
    )
    dwg += _7805
    dwg += elm.Capacitor().at(_7805.input).down().label(".47μ", loc="bottom")
    dwg.push()
    dwg += elm.Line().left()
    dwg += elm.Vss().label("0V")
    dwg += elm.Resistor().up().label("1K2 1W", loc="bottom")
    dwg += elm.Line().right().label("8v").hold()
    dwg += elm.Resistor().up().label("1K8 1W", loc="bottom")
    dwg += elm.Vdd().label("20V")
    _20v: schemdraw.util.Point = dwg.here
    dwg.pop()
    dwg += elm.Wire("-|").to(_7805.ground)
    dwg += elm.Capacitor().down().at(_7805.output).label("100n")
    dwg += elm.Line().left()
    dwg += elm.Vdd().at(_7805.output).label("5V")
    return _20v


def solenoid_transistor(
    dwg: schemdraw.Drawing, pin: str, move_x: float, move_y: float, _20v: elm.Element
):
    """Transistor circuit for bell solenoid"""
    header = elm.Header(rows=1, pinsleft=[pin], pinalignleft="center")
    solenoid = elm.Transformer(t1=7, t2=0).right()
    transistor = elm.BjtNpn().right().label("BC548B")
    diode = elm.Diode().label("IN4001").toy(_20v)

    dwg.move(move_x, move_y)
    dwg += header
    dwg.move_from(header.pin1, 3, 0)
    dwg += transistor
    dwg += elm.Resistor().at(transistor.base).to(header.pin1).label("4K7")
    dwg += elm.Vss().label("0V").at(transistor.emitter)
    dwg += elm.Line().at(transistor.collector).right()

    if pin == "B1":
        dwg += diode.down()
        dwg.move(1, 0.2)
        dwg += solenoid
    else:
        dwg += diode.up()
        dwg.move(1, -0.3)
        dwg += solenoid.flip()
    dwg += elm.Line().at(solenoid.p1).left(1)
    dwg += elm.Wire("-|").at(_20v).to(solenoid.p2)


def analog(dwg: schemdraw.Drawing) -> schemdraw.util.Point:
    """'Analog' part of the drawing - return anchor for positioning digital"""

    _20v: schemdraw.util.Point = psu(dwg)
    solenoid_transistor(dwg, "B1", 2, 5, _20v)
    solenoid_transistor(dwg, "B2", -8.05, -4.3, _20v)



    #dial_header = elm.Header(rows=2, pinsleft=["D4", "D5"], pinalignleft="center")
    #dwg.move_from(solenoid_header.pin2, 12, -0.33333)
    #dwg += dial_header

    return _20v
