"""Switched Amplifier for phone receiver"""

from typing import Optional
from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from custom_elements import ElectrolyticCapacitor, OldSchoolNC
from regulator import RegulatorCircuit


class AmplifierBoard:
    """Switched Amplifier for phone receiver"""

    def __init__(self, dwg: Drawing):
        self.dwg = dwg
        self.vss_0v: Optional[float] = None
        self.the_left: Optional[float] = None
        self._12v: Optional[Point] = None
        self.chip = (
            elm.Opamp()
            .right()
            .flip()
            .label("LM386", loc="center", ofst=(-0.2, 0))
            .label("7", "n2", ofst=(-0.1, -0.25), halign="right", valign="top")
            .label("4", "vd", ofst=(-0.1, -0.2), halign="right", valign="top")
            .label("6", "vs", ofst=(-0.1, 0.2), halign="right", valign="bottom")
            .label("2", "in1", ofst=(-0.1, 0.1), halign="right", valign="bottom")
            .label("3", "in2", ofst=(-0.1, 0.1), halign="right", valign="bottom")
            .label("5", "out", ofst=(-0.1, 0.1), halign="left", valign="bottom")
        )
        self.input_header = elm.Header(
            rows=4, pinsleft=["left", "right", "B3", "ground"], pinalignleft="center"
        )
        self.output_header = elm.Header(
            rows=2, pinsright=["+", "-"], pinalignleft="center"
        )

    def inputs(self):
        """stereo to mono input"""
        self.dwg += elm.Line().at(self.chip.in2).left(2.5)
        self.dwg.push()
        self.dwg.move(-3.3, -2.1)
        self.dwg += self.input_header
        self.dwg.pop()
        self.dwg.push()
        self.dwg += elm.Resistor().to(self.input_header.pin1).label("1K", ofst=(0, 0.4))
        self.dwg.pop()
        self.dwg += elm.Line().down(0.6)
        self.dwg += elm.Resistor().to(self.input_header.pin2).label("1K", ofst=(0, 0.4))
        self.the_left = Point(self.dwg.here).x

    def control(self):
        """transistor and GPIO to switch amp on and off"""
        transistor = (
            elm.BjtNpn(circle=True).right().label("BC548B", loc="left", ofst=(0, 0.3))
        )
        self.dwg.move_from(self.chip.vd, -0.75, -1.5)
        self.dwg += transistor
        self.dwg += elm.Line().at(self.chip.vd).to(transistor.collector)
        self.dwg += elm.Line().at(transistor.emitter).down(1)
        self.vss_0v = Point(self.dwg.here).y
        self.dwg += (
            elm.Resistor()
            .right()
            .at(self.input_header.pin3)
            .label("4K7", ofst=(0, -0.4))
        )
        self.dwg += elm.Wire("|-").to(transistor.base)

    def output(self):
        """noise filter and output stage"""
        self.dwg += (
            ElectrolyticCapacitor()
            .at(self.chip.n2)
            .toy(self.vss_0v)
            .label("100μ", loc="bottom")
        )
        self.dwg += elm.Line().at(self.chip.out).right(1)
        self.dwg += elm.Capacitor().toy(self.vss_0v).label("100n", loc="bottom").hold()
        self.dwg += ElectrolyticCapacitor().right().label("1000μ")
        self.dwg.push()
        self.dwg.move(-0.3, -2.5)
        self.dwg += self.output_header
        self.dwg.pop()
        self.dwg += elm.Line().to(self.output_header.pin1)

    def psu_and_ground_rail(self):
        """The 12V power supply and ground rail"""
        self.dwg += elm.Line().at(self.chip.vs).up(1.2)
        vdd = self.dwg.here
        self.dwg.move(-13, -4)
        regulator_circuit = RegulatorCircuit(self.dwg, "7812", vdd.y, self.vss_0v)
        regulator_circuit.draw("15.4V", "1K4", "4K7", "12V")
        self.dwg += elm.Line().at(vdd).to(regulator_circuit.output_point)
        self.dwg.move_from(regulator_circuit.output_point, 1.5, 0)
        self.dwg += ElectrolyticCapacitor().toy(self.vss_0v).label("100μ")
        self.ground_rail(regulator_circuit.ground_point)

    def ground_rail(self, ground_point: Point):
        """Ground rail portion of previous function"""
        self.dwg.here = Point([self.chip.n2.x, self.vss_0v])
        self.dwg += elm.Line().left(1.75).at(self.chip.in1)
        self.dwg += OldSchoolNC().down().toy(self.vss_0v)
        self.dwg += elm.Line().at(self.input_header.pin4).toy(self.vss_0v)
        self.dwg += elm.Line().at(self.output_header.pin2).toy(self.vss_0v)
        self.dwg += elm.Line().to(ground_point)

    def draw(self):
        """Draw amplifier board - return anchor for positioning next board"""
        origin = Point(self.dwg.here)
        self.dwg.move(21, -4.5)
        self.dwg += self.chip
        self.inputs()
        self.control()
        self.output()
        self.psu_and_ground_rail()
        self.dwg.move_from(origin, 0, -9)
