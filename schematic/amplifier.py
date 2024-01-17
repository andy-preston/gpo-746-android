"""Switched Amplifier for phone receiver"""

from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from custom_elements import ElectrolyticCapacitor, OldSchoolNC
from regulator import RegulatorCircuit, RegulatorLabels


class AmplifierBoard:
    """Switched Amplifier for phone receiver"""

    def __init__(self, dwg: Drawing):
        self.dwg = dwg
        self.vss_0v: float
        self.the_left: float
        self._12v: Point
        self.chip: elm.Opamp
        self.input_header: elm.Header
        self.output_header: elm.Header

    def inputs(self):
        """stereo to mono input"""
        elm.Line().left(2.5).at(self.chip.in2)
        self.dwg.push()
        self.dwg.move(-3.3, -2.1)
        self.input_header = elm.Header(
            rows=4, pinsleft=["left", "right", "B3", "ground"], pinalignleft="center"
        )
        self.dwg.pop()
        self.dwg.push()
        elm.Resistor().to(self.input_header.pin1).label("1K", ofst=(0, 0.4))
        self.dwg.pop()
        elm.Line().down(0.6)
        elm.Resistor().to(self.input_header.pin2).label("1K", ofst=(0, 0.4))
        self.the_left = Point(self.dwg.here).x

    def control(self):
        """transistor and GPIO to switch amp on and off"""
        self.dwg.move_from(self.chip.vd, -0.75, -1.5)
        transistor = (
            elm.BjtNpn(circle=True).right().label("BC548B", loc="left", ofst=(0, 0.3))
        )
        elm.Line().at(self.chip.vd).to(transistor.collector)
        elm.Line().down(1).at(transistor.emitter)
        self.vss_0v = Point(self.dwg.here).y
        (elm.Resistor().right().at(self.input_header.pin3).label("4K7", ofst=(0, -0.4)))
        elm.Wire("|-").to(transistor.base)

    def output(self):
        """noise filter and output stage"""
        (
            ElectrolyticCapacitor()
            .at(self.chip.n2)
            .toy(self.vss_0v)
            .label("100μ", loc="bottom")
        )
        elm.Line().right(1).at(self.chip.out)
        elm.Capacitor().toy(self.vss_0v).label("100n", loc="bottom").hold()
        ElectrolyticCapacitor().right().label("1000μ")
        self.dwg.push()
        self.dwg.move(-0.3, -2.5)
        self.output_header = elm.Header(
            rows=2, pinsright=["+", "-"], pinalignleft="center"
        )
        self.dwg.pop()
        elm.Line().to(self.output_header.pin1)

    def psu_and_ground_rail(self):
        """The 12V power supply and ground rail"""
        elm.Line().up(1.2).at(self.chip.vs)
        vdd = self.dwg.here
        self.dwg.move(-13, -4)
        regulator_circuit = RegulatorCircuit()
        regulator_circuit.draw(
            self.dwg,
            RegulatorLabels(
                chip="7812",
                input_voltage="15.4V",
                output_voltage="12V",
                top_resistor="1K4",
                bottom_resistor="4K7",
            ),
            vdd.y,
            self.vss_0v,
        )
        elm.Line().at(vdd).to(regulator_circuit.output_point)
        self.dwg.move_from(regulator_circuit.output_point, 1.5, 0)
        ElectrolyticCapacitor().toy(self.vss_0v).label("100μ")
        self.ground_rail(regulator_circuit.ground_point)

    def ground_rail(self, ground_point: Point):
        """Ground rail portion of previous function"""
        self.dwg.here = Point([self.chip.n2.x, self.vss_0v])
        elm.Line().left(1.75).at(self.chip.in1)
        OldSchoolNC().down().toy(self.vss_0v)
        elm.Line().at(self.input_header.pin4).toy(self.vss_0v)
        elm.Line().at(self.output_header.pin2).toy(self.vss_0v)
        elm.Line().to(ground_point)

    def draw(self):
        """Draw amplifier board - return anchor for positioning next board"""
        origin = Point(self.dwg.here)
        self.dwg.move(21, -4.5)
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
        self.inputs()
        self.control()
        self.output()
        self.psu_and_ground_rail()
        self.dwg.move_from(origin, 0, -9)
