""""GPO-746 Microcontroller Schematic"""

import schemdraw
import schemdraw.elements as elm


def solenoid_transistor(dwg: schemdraw.Drawing, pin: elm.Element, move_y: float, _20v: elm.Element):
    """transistor circuit for bell solenoid"""
    solenoid = elm.Transformer(t1=7, t2=0).right()
    transistor = elm.BjtNpn().right().label("BC108")
    diode = elm.Diode().label("IN4001").toy(_20v)

    dwg.move_from(pin, 4, move_y)
    dwg += transistor
    dwg += elm.Resistor().at(transistor.base).left(2).label("4K7")
    dwg += elm.Wire("|-").to(pin)
    dwg += elm.GroundChassis().at(transistor.emitter)
    dwg += elm.Line().at(transistor.collector).right()

    if move_y > 0:
        dwg += diode.down()
        dwg.move(1, 0.3)
        dwg += solenoid
    else:
        dwg += diode.up()
        dwg.move(1, -0.3)
        dwg += solenoid.flip()
    dwg += elm.Line().at(solenoid.p1).left(1)
    dwg += elm.Wire("-|").at(_20v).to(solenoid.p2)


def analog(dwg: schemdraw.Drawing) -> elm.Element:
    """'Analog' part of the drawing"""

    solenoid_header = elm.Header(rows=3, pinsleft=["B1", "20V", "B2"], pinalignleft="center")
    dwg += solenoid_header
    solenoid_transistor(dwg, solenoid_header.pin1, 1.8, solenoid_header.pin2)
    solenoid_transistor(dwg, solenoid_header.pin3, -3.2, solenoid_header.pin2)



    #dial_header = elm.Header(rows=2, pinsleft=["D4", "D5"], pinalignleft="center")
    #dwg.move_from(solenoid_header.pin2, 12, -0.33333)
    #dwg += dial_header

    return solenoid_header.pin2
