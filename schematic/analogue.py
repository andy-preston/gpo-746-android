""""GPO-746 Microcontroller Schematic"""

import schemdraw
import schemdraw.elements as elm


def analogue(dwg: schemdraw.Drawing) -> elm.Element:
    """Analogue part of the drawing"""

    solenoid_header = elm.Header(rows=2, pinsleft=["B1", "B2"], pinalignleft="center")
    dwg += solenoid_header
    dwg.move_from(solenoid_header.pin1, 4, 2)

    transistor1 = elm.BjtNpn().right()
    dwg += transistor1
    dwg += elm.Resistor().at(transistor1.base).left(2).label("4K7")
    dwg += elm.Wire("|-").to(solenoid_header.pin1)
    dwg += elm.GroundChassis().at(transistor1.emitter)
    dwg += elm.Line().at(transistor1.collector).right()
    dwg.push()
    dwg += elm.Diode().down().label("1N4001")
    dwg += elm.Line().right(1)

    solenoid1 = elm.Transformer(t1=7, t2=0).right()
    dwg += solenoid1
    dwg.pop()
    dwg += elm.Wire("-|").to(solenoid1.p1)

    transistor2 = elm.BjtNpn().right()
    dwg.move_from(solenoid_header.pin2, 4, -2)
    dwg += transistor2
    dwg += elm.Resistor().at(transistor2.base).left(2).label("4K7")
    dwg += elm.Wire("|-").to(solenoid_header.pin2)
    dwg += elm.GroundChassis().at(transistor2.emitter)
    dwg += elm.Line().at(transistor2.collector).right()
    dwg.push()
    dwg += elm.Diode().down().label("1N4001")
    dwg += elm.Line().right(1)

    solenoid2 = elm.Transformer(t1=7, t2=0).right()
    dwg += solenoid2
    dwg.pop()
    dwg += elm.Wire("-|").to(solenoid2.p1)

    dial_header = elm.Header(rows=2, pinsleft=["D4", "D5"], pinalignleft="center")
    dwg.move_from(solenoid_header.pin2, 12, -0.33333)
    dwg += dial_header

    return solenoid_header.pin2
