""""Only named 'analog' to draw an arbitrary distinction between the two halves"""

import schemdraw
import schemdraw.elements as elm
from custom_elements import OldSchoolNC, ElectrolyticCapacitor


class AnalogPart:
    """The so-called 'analog' part of the drawing"""

    def __init__(self, dwg: schemdraw.Drawing):
        self.dwg = dwg
        self._0v = schemdraw.util.Point([0, 0])
        self._5v = schemdraw.util.Point([0, 0])
        self._20v = schemdraw.util.Point([0, 0])

    def psu(self):
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
        self.dwg += _7805
        self.dwg += (
            ElectrolyticCapacitor()
            .at(_7805.input)
            .toy(self._0v.y)
            .label("0.47μ", loc="bottom")
        )
        self.dwg.push()
        self.dwg += elm.Line().left()
        self.dwg += elm.Vss().label("0V")
        self.dwg += elm.Resistor().up(3.5).label("1K2 1W", loc="bottom")
        self.dwg += elm.Line().right().label("8v").hold()
        self.dwg += elm.Resistor().toy(self._5v.y).label("1K8 1W", loc="bottom")
        self.dwg += elm.Vdd().label("(Supply)\n20V")
        self._20v = self.dwg.here
        self.dwg.pop()
        self.dwg += elm.Wire("-|").to(_7805.ground)
        self.dwg += elm.Capacitor().at(_7805.output).toy(self._0v.y).label("100n")
        self.dwg += elm.Line().to(self._0v).hold()
        self.dwg += elm.Line().left()
        self.dwg += elm.Line().at(_7805.output).toy(self._5v.y)
        self.dwg += elm.Vdd().label("5V")
        self.dwg += elm.Line().to(self._5v)

    def solenoid_transistor(self, pin: str):
        """Transistor circuit for bell solenoid"""

        header = elm.Header(rows=1, pinsleft=[pin], pinalignleft="center")
        solenoid = elm.Transformer(t1=7, t2=0).right()
        transistor = elm.BjtNpn(circle=True).right().label("BC548B")
        diode = elm.Diode().label("IN4001").toy(self._20v)

        self.dwg += header
        self.dwg.move_from(header.pin1, 3, 0)
        self.dwg += transistor
        self.dwg += elm.Resistor().at(transistor.base).to(header.pin1).label("4K7")
        if pin == "B1":
            self.dwg += elm.Vss().label("0V").at(transistor.emitter)
        else:
            self.dwg += elm.Wire("|-").at(transistor.emitter).to(self._0v)
        self.dwg += elm.Line().at(transistor.collector).right()
        if pin == "B1":
            self.dwg += diode.down()
            self.dwg.move(1, 0.2)
            self.dwg += solenoid
        else:
            self.dwg += diode.up()
            self.dwg.move(1, -0.3)
            self.dwg += solenoid.flip()
        self.dwg += elm.Line().at(solenoid.p1).left(1)
        self.dwg += elm.Line().at(self._20v).tox(solenoid.p2)
        self.dwg += elm.Line().left(1).hold()
        self.dwg += elm.Line().to(solenoid.p2)

    def debounce(
        self,
        location: str,
        input_pin: schemdraw.util.Point,
        base_pin: schemdraw.util.Point,
        switch_pin: schemdraw.util.Point,
    ):
        """Half of the dial debounce circuit"""

        self.dwg += elm.Line().at(input_pin).right(1)
        self.dwg.push()
        if location == "top":
            self.dwg += elm.Line().up(1.5)
            self.dwg += elm.Capacitor().up(1.5).label("100n")
            self._5v = self.dwg.here
        else:
            self.dwg += elm.Line().down(1.5)
            self.dwg += elm.Capacitor().down(1.5).label("100n")
            self._0v = self.dwg.here
        self.dwg += elm.Wire("-|").to(base_pin)
        self.dwg.pop()
        self.dwg += elm.Resistor().right().label("18K", loc=location)
        self.dwg.push()
        if location == "top":
            self.dwg += elm.Line().up(1.5)
        else:
            self.dwg += elm.Line().down(1.5)
        self.dwg += elm.Diode().left().reverse().label("1N4148", loc=location)
        self.dwg.pop()
        self.dwg += elm.Line().right(1.1 if location == "top" else 2.2)
        self.dwg.push()
        if location == "top":
            self.dwg += OldSchoolNC().down(1.25)
            self.dwg += elm.Resistor().down(2.35).label("82k")
        else:
            self.dwg += OldSchoolNC().up(1.25)
            self.dwg += elm.Resistor().up(2.35).label("82k")
        self.dwg.pop()
        self.dwg += elm.Line().to(switch_pin)

    def draw(self) -> schemdraw.util.Point:
        """Draw the 'Analog' part - return anchor for positioning digital"""

        self.dwg.move(26, 0)
        io_header = elm.Header(rows=2, pinsleft=["D4", "D5"], pinalignleft="center")
        self.dwg += io_header
        self.dwg.move(7.5, -0.6)
        dial_header = elm.Header(
            rows=4, pinsright=["blue", "grey", "pink", "orange"], pinalignright="bottom"
        )
        self.dwg += dial_header
        self.debounce("top", io_header.pin1, dial_header.pin1, dial_header.pin2)
        self.debounce("bottom", io_header.pin2, dial_header.pin4, dial_header.pin3)
        self.dwg.move(-14, -0.5)
        self.psu()
        self.dwg.move(-23, 2)
        self.solenoid_transistor("B1")
        self.dwg.move(-8.05, -4.3)
        end_position = self.dwg.here
        self.solenoid_transistor("B2")

        return end_position
