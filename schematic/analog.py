""""GPO-746 Microcontroller Schematic - 'Analog' part"""

from typing import Tuple
import schemdraw
import schemdraw.elements as elm

def psu(dwg: schemdraw.Drawing) -> Tuple[schemdraw.util.Point, ...]:
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
    _0v: schemdraw.util.Point = dwg.here
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
    return (_20v, _0v)


def solenoid_transistor(
    dwg: schemdraw.Drawing, pin: str, _20v: schemdraw.util.Point, _0v: schemdraw.util.Point
):
    """Transistor circuit for bell solenoid"""
    header = elm.Header(rows=1, pinsleft=[pin], pinalignleft="center")
    solenoid = elm.Transformer(t1=7, t2=0).right()
    transistor = elm.BjtNpn(circle=True).right().label("BC548B")
    diode = elm.Diode().label("IN4001").toy(_20v)

    dwg += header
    dwg.move_from(header.pin1, 3, 0)
    dwg += transistor
    dwg += elm.Resistor().at(transistor.base).to(header.pin1).label("4K7")
    if pin == "B1":
        dwg += elm.Vss().label("0V").at(transistor.emitter)
    else:
        dwg += elm.Wire("|-").at(transistor.emitter).to(_0v)
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

    _20v, _0v  = psu(dwg)
    dwg.move(1.3, 5)
    solenoid_transistor(dwg, "B1", _20v, _0v)
    dwg.move(-8.05, -4.3)
    solenoid_transistor(dwg, "B2", _20v, _0v)

    dwg.move(2, -3)
    io_header = elm.Header(rows=2, pinsleft=["D4", "D5"], pinalignleft="center")
    dwg += io_header
    dwg.move(5, -0.6)
    dial_header = elm.Header(rows=4, pinsright=["blue", "grey", "pink", "orange"], pinalignright="bottom")
    dwg += dial_header

    dwg += elm.Line().at(io_header.pin1).right(1)
    dwg.push()
    dwg += elm.Line().up(1)
    dwg += elm.Capacitor().up(2).label("100n")
    dwg += elm.Vdd().label("5V")
    dwg += elm.Wire("-|").to(dial_header.pin1)
    dwg.pop()
    dwg += elm.Resistor().right().label("18K")
    dwg.push()
    dwg += elm.Line().up(1)
    dwg += elm.Diode().left().reverse().label("1N4148")
    dwg.pop()
    dwg += elm.Line().to(dial_header.pin2)

    dwg += elm.Line().at(io_header.pin2).right(1)
    dwg.push()
    dwg += elm.Line().down(1)
    dwg += elm.Capacitor().down(2).label("100n")
    dwg += elm.Line().to(_0v).hold()
    dwg += elm.Wire("-|").to(dial_header.pin4)
    dwg.pop()
    dwg += elm.Resistor().right().label("18K", loc="bottom")
    dwg.push()
    dwg += elm.Line().down(1)
    dwg += elm.Diode().left().label("1N4148", loc="bottom")
    dwg.pop()
    dwg += elm.Line().to(dial_header.pin3)



    return _20v
