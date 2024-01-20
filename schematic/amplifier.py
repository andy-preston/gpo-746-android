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
        self.vss_0v: Point
        self.vdd_12v: Point
        self.chip: elm.Opamp
        self.input_header: elm.Header

    def inputs(self):
        """stereo to mono input"""
        elm.Line().left(2.5).at(self.chip.in2)
        self.dwg.push()
        self.dwg.move(-3.6, -2.1)
        self.input_header = elm.Header(
            rows=4, pinsleft=["left", "right", "B3", "ground"], pinalignleft="center"
        )
        self.dwg.pop()
        self.dwg.push()
        elm.Resistor().to(self.input_header.pin1).label("1K", ofst=(0, 0.4))
        self.dwg.pop()
        elm.Line().down(0.6)
        elm.Resistor().to(self.input_header.pin2).label("1K", ofst=(0, 0.4))
        elm.Line().at(self.input_header.pin4).toy(self.vss_0v.y)

    def control(self):
        """transistor and GPIO to switch amp on and off"""
        self.dwg.move_from(self.chip.vd, -0.75, -1.5)
        transistor = (
            elm.BjtNpn(circle=True).right().label("BC548B", loc="left", ofst=(0, 0.3))
        )
        elm.Line().at(self.chip.vd).to(transistor.collector)
        elm.Line().at(transistor.emitter).toy(self.vss_0v.y)
        elm.Resistor().right(3.3).at(self.input_header.pin3).label(
            "4K7", ofst=(0, -0.4)
        )
        elm.Wire("|-").to(transistor.base)

    def output(self):
        """noise filter and output stage"""
        ElectrolyticCapacitor().at(self.chip.n2).toy(self.vss_0v).label(
            "100μ", loc="bottom"
        )
        elm.Line().right(0.8).at(self.chip.out)
        elm.Capacitor().toy(self.vss_0v).label("100n", loc="bottom").hold()
        ElectrolyticCapacitor().right().label("1000μ")
        self.dwg.push()
        self.dwg.move(-0.3, -2.5)
        output_header = elm.Header(rows=2, pinsright=["+", "-"], pinalignleft="center")
        self.dwg.pop()
        elm.Line().to(output_header.pin1)
        elm.Wire("|-").at(output_header.pin2).to(self.vss_0v)

    def psu(self):
        """The 12V power supply"""
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
        )
        self.vss_0v = regulator_circuit.vss_output
        self.vdd_12v = regulator_circuit.vdd_output
        elm.Line().right(1.5).at(self.vdd_12v)
        ElectrolyticCapacitor().toy(self.vss_0v).label("100μ")
        elm.Line().to(self.vss_0v)
        self.dwg.move_from(self.vdd_12v, 2, -3)

    def the_chip(self):
        """The LM386 amplifier chip"""
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
        elm.Wire("|-").at(self.chip.vs).to(self.vdd_12v)
        elm.Line().left(1.7).at(self.chip.in1)
        OldSchoolNC().down().toy(self.vss_0v)

    def draw(self):
        """Draw amplifier board"""
        self.dwg.move(0, -5.5)
        self.psu()
        self.dwg.move(7, 1)
        self.the_chip()
        self.inputs()
        self.control()
        self.output()
