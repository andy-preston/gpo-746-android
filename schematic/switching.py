""""The switching components and the 5V power supply"""

from typing import Optional
from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from custom_elements import OldSchoolNC
from regulator import RegulatorCircuit


class SwitchingBoard:
    """The switching components and the 5V power supply"""

    def __init__(self, dwg: Drawing):
        self.dwg = dwg
        self.vss_0v = Optional[Point]
        self.vdd_5v = Optional[Point]
        self._20v = Point([0, 0])
        self.hook_pin = Optional[Point]

    def psu(self):
        """The 5V power supply"""
        regulator_circuit = RegulatorCircuit(self.dwg, "7805", self.vdd_5v, self.vss_0v)
        regulator_circuit.draw("8V", "1K8", "1K2", "5V")
        elm.Line().right(5.5)
        self._20v = regulator_circuit.input_20v

    def solenoid_transistor(self, pin: str) -> float:
        """Transistor circuit for bell solenoid - return x position of header"""

        header = elm.Header(rows=1, pinsleft=[pin], pinalignleft="center")
        solenoid = elm.Transformer(t1=7, t2=0).right()
        if pin == "B2":
            solenoid.label("Telephone\nBell\nSolenoids", loc="bottom")
        transistor = elm.BjtNpn(circle=True).right().label("BC548B", loc="top")
        diode = elm.Diode().label("1N4001").toy(self._20v)
        self.dwg += header
        self.dwg.move_from(header.pin1, 3, 0)
        left_edge = Point(self.dwg.here).x
        self.dwg += transistor
        self.dwg += (
            elm.Resistor()
            .at(transistor.base)
            .to(header.pin1)
            .label("4K7", ofst=(0, 0.4))
        )
        if pin == "B1":
            self.dwg += elm.Vss().label("0V").at(transistor.emitter)
        else:
            self.dwg += elm.Wire("|-").at(transistor.emitter).to(self.vss_0v)
        self.dwg += elm.Line().right(2).at(transistor.collector)
        if pin == "B1":
            self.dwg += diode.down()
            self.dwg.move(1, 0.2)
            self.dwg += solenoid
        else:
            self.dwg += diode.up()
            self.dwg.move(1, -0.3)
            self.dwg += solenoid.flip()
        self.dwg += elm.Line().left(1).at(solenoid.p1)
        self.dwg += elm.Line().at(self._20v).tox(solenoid.p2)
        self.dwg += elm.Line().left(1).hold()
        self.dwg += elm.Line().to(solenoid.p2)
        return left_edge

    def debounce(
        self,
        location: str,
        input_pin: Point,
        base_pin: Point,
        switch_pin: Point,
    ):
        """Half of the dial debounce circuit"""

        self.dwg += elm.Line().right(1.5).at(input_pin)
        self.dwg.push()
        if location == "top":
            self.dwg += elm.Line().up(1.5)
            self.dwg += elm.Capacitor().up(1.5).label("100n")
            self.vdd_5v = Point(self.dwg.here)
        else:
            self.dwg += elm.Line().down(1.5)
            self.dwg += elm.Capacitor().down(1.5).label("100n")
            self.vss_0v = Point(self.dwg.here)
        self.dwg += elm.Wire("-|").to(base_pin)
        self.dwg.pop()
        self.dwg += elm.Resistor().right().label("18K", ofst=(0, -0.4))
        self.dwg.push()
        if location == "top":
            self.dwg += elm.Line().up(1.2)
            self.dwg += elm.Diode().left().label("1N4148", loc="top")
        else:
            self.dwg += elm.Line().down(1.2)
            self.dwg += elm.Diode().left().reverse().label("1N4148", loc="bottom")
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

    def dial_circuit(self):
        """The whole dial debounce circuit"""
        io_header = elm.Header(
            rows=3,
            pinsleft=["", "D3", "D4"],
            pinalignleft="center",
            pinsright=["D2", "", ""],
            pinalignright="center",
        )
        self.hook_pin = io_header.pin1
        self.dwg.move(8, -0.6)
        dial_header = elm.Header(
            rows=4, pinsright=["blue", "grey", "pink", "orange"], pinalignright="center"
        )
        self.debounce("top", io_header.pin2, dial_header.pin1, dial_header.pin2)
        self.debounce("bottom", io_header.pin3, dial_header.pin4, dial_header.pin3)

    def dial(self):
        """The actual dial in the phone"""
        dial_header = elm.Header(
            rows=5,
            pinsleft=["blue", "grey", "pink", "orange", "brown"],
            pinalignleft="center",
        )
        self.dwg.move(2, 2.5)
        dpst = elm.SwitchDpst().label("Trigger", loc="top")
        blue = dial_header.pin1
        grey = dial_header.pin2
        pink = dial_header.pin3
        orange = dial_header.pin4
        brown = dial_header.pin5
        elm.Line().at(grey).right(1.2)
        elm.Wire("|-").to(dpst.p1)
        elm.Line().at(blue).up(1)
        elm.Line().right(3.6).label("Dial", ofst=(0, 0.2))
        elm.Wire("|-").to(dpst.t1)
        elm.Line().at(pink).right(1.2)
        self.dwg.push()
        elm.Line().down(1)
        elm.Switch().right(2).label("50ms pulse", loc="bottom")
        elm.Line().down(0.75)
        elm.Line().left(2.5)
        elm.Wire("|-").to(orange)
        self.dwg.pop()
        elm.Line().to(dpst.p2)
        elm.Line().at(brown).down(1)
        elm.Line().right(3.6)
        elm.Wire("|-").to(dpst.t2)

    def hook(self):
        """Connections to the cradle/hook switch"""
        elm.Line().at(self.hook_pin).left(2)
        elm.Resistor().toy(self.vdd_5v).label("10K").hold()
        elm.Line().down(1)
        elm.Switch().toy(self.vss_0v).label("Cradle hook", loc="bottom")

    def draw(self):
        """
        Draw switching board

        All These relative moves are a bit horrible and make trying to modify
        the circuit kinda hard.
        It would be better if the various parts were properly arranged
        in relation to each other.
        """
        self.dial()
        self.dwg.move(-14, -0.5)
        self.dial_circuit()
        self.hook()
        self.dwg.move(-5, 2.5)
        self.psu()
        self.dwg.move_from(self._20v, -9, 2)
        self.solenoid_transistor("B1")
        self.dwg.move(-7.05, -4.3)
        left_edge = self.solenoid_transistor("B2")
        self.dwg.move_from(Point([left_edge, Point(self.vss_0v).y]), 0, 0)
