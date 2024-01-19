""""The switching components and the 5V power supply"""

from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from custom_elements import OldSchoolNC
from regulator import RegulatorCircuit, RegulatorLabels


class SwitchingBoard:
    """The switching components and the 5V power supply"""

    def __init__(self, dwg: Drawing):
        self.dwg = dwg
        self.vss_0v: Point
        self.vdd_5v: Point
        self._20v: Point

    def psu(self):
        """The 5V power supply"""
        regulator_circuit = RegulatorCircuit()
        regulator_circuit.draw(
            self.dwg,
            RegulatorLabels(
                chip="7805",
                input_voltage="8V",
                output_voltage="5V",
                top_resistor="1K8",
                bottom_resistor="1K2",
            ),
            self.vdd_5v.y,
            self.vss_0v.y,
        )
        self._20v = regulator_circuit.input_20v

    def solenoid_transistor(self, pin: str) -> float:
        """Transistor circuit for bell solenoid - return x position of header"""

        header = elm.Header(rows=1, pinsleft=[pin], pinalignleft="center")
        self.dwg.move_from(header.pin1, 3, 0)
        left_edge = Point(self.dwg.here).x
        transistor = elm.BjtNpn(circle=True).right().label("BC548B", loc="top")
        elm.Resistor().at(transistor.base).to(header.pin1).label("4K7", ofst=(0, 0.4))
        if pin == "B1":
            elm.Vss().label("0V").at(transistor.emitter)
        else:
            elm.Wire("|-").at(transistor.emitter).to(self.vss_0v)
        elm.Line().right(2).at(transistor.collector)
        elm.Diode().toy(self._20v).label("1N4001")
        self.dwg.move(1, 0.2 if pin == "B1" else -0.3)
        solenoid = elm.Transformer(t1=7, t2=0).right()
        if pin == "B2":
            solenoid.flip().label("Bell\nSolenoids", loc="bottom")
        elm.Line().left(1).at(solenoid.p1)
        elm.Line().at(self._20v).tox(solenoid.p2)
        elm.Line().left(1).hold()
        elm.Line().to(solenoid.p2)
        return left_edge

    def save_vdd_vss(self):
        here = Point(self.dwg.here)
        self.vdd_5v = Point((here.x - 3.7, here.y + 5))
        self.vss_0v = Point((here.x - 3.7, here.y - 2))

    def dial_circuit(self):
        """The whole dial debounce circuit"""
        dial_header = elm.Header(
            rows=4, pinsright=["blue", "pink", "grey", "orange"], pinalignright="center"
        )
        self.dwg.move(-8, 0)
        io_header = elm.Header(
            pinsleft=["D4", "D3", ""],
            pinalignleft="center",
            rows=3,
            pinsright=["", "", "D2"],
            pinalignright="center",
        )
        self.save_vdd_vss()

        elm.Wire("|-").at(dial_header.pin1).to(self.vdd_5v)
        elm.Wire("|-").at(dial_header.pin4).to(self.vss_0v)

        elm.Line().at(dial_header.pin2).left(1)
        self.dwg.push()
        elm.Resistor().toy(self.vdd_5v).label("10K")
        self.dwg.pop()
        elm.Line().to(io_header.pin1)

        elm.Line().at(dial_header.pin3).left(1)
        self.dwg.push()
        elm.Resistor().toy(self.vss_0v).label("10K")
        self.dwg.pop()
        elm.Line().to(io_header.pin2)

        elm.Line().left(2).at(io_header.pin3)
        elm.Resistor().toy(self.vdd_5v).label("10K").hold()
        elm.Switch().toy(self.vss_0v).label("hook", loc="top")

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
        elm.Line().right(1.2).at(grey)
        elm.Wire("|-").to(dpst.p1)
        elm.Line().up(1).at(blue)
        elm.Line().right(3.6).label("Dial", ofst=(0, 0.2))
        elm.Wire("|-").to(dpst.t1)
        elm.Line().right(1.2).at(pink)
        self.dwg.push()
        elm.Line().down(1)
        elm.Switch().right(2).label("50ms pulse", loc="bottom")
        elm.Line().down(0.75)
        elm.Line().left(2.5)
        elm.Wire("|-").to(orange)
        self.dwg.pop()
        elm.Line().to(dpst.p2)
        elm.Line().down(1).at(brown)
        elm.Line().right(3.6)
        elm.Wire("|-").to(dpst.t2)

    def draw(self):
        """
        Draw switching board

        All These relative moves are a bit horrible and make trying to modify
        the circuit kinda hard.
        It would be better if the various parts were properly arranged
        in relation to each other.
        """
        self.dial()
        self.dwg.move(-6, -1.2)
        self.dial_circuit()
        self.dwg.move(-5, 2.5)
        self.psu()
        self.dwg.move_from(self._20v, -9, 2)
        self.solenoid_transistor("B1")
        self.dwg.move(-7.05, -4.3)
        left_edge = self.solenoid_transistor("B2")
        self.dwg.here = (left_edge, Point(self.vss_0v).y)
