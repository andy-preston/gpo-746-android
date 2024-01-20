"""78 series regulator with capacitors and voltage divider"""

from dataclasses import dataclass
from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from schemdraw.elements import Ic, IcPin
from custom_elements import ElectrolyticCapacitor


@dataclass
class RegulatorLabels:
    """The text labels for parts of the regulator circuit"""

    chip: str
    input_voltage: str
    output_voltage: str
    top_resistor: str
    bottom_resistor: str


# pylint: disable=too-few-public-methods
class RegulatorCircuit:
    """78 series regulator with capacitors and voltage divider"""

    def __init__(self):
        self.vdd_input: Point
        self.vdd_output: Point
        self.vss_input: Point
        self.vss_output: Point
        self.output: Point

    def draw(self, dwg: Drawing, labels: RegulatorLabels, headroom: int = 0):
        """draw the circuit and determine the anchor points"""
        input_point = Point(dwg.here)
        elm.Resistor().at(input_point).down().label(
            f"{labels.bottom_resistor}\n1W", loc="bottom"
        )
        self.vss_input = Point(dwg.here)
        elm.Vss().label("0V")
        elm.Resistor().at(input_point).up().label(
            f"{labels.top_resistor}\n1W", loc="bottom"
        )
        if headroom > 0:
            elm.Line().up(headroom)
        self.vdd_input = Point(dwg.here)
        elm.Vdd().label("(Supply)\n20V")
        elm.Line().right(2).at(input_point).label(labels.input_voltage)
        ElectrolyticCapacitor().toy(self.vss_input.y).label("47μ", loc="bottom").hold()
        dwg.move(1, -1)
        regulator = Ic(
            plblofst=0.15,
            pins=[
                IcPin(side="left", pin="1", anchorname="input"),
                IcPin(side="bottom", pin="2", anchorname="ground"),
                IcPin(side="right", pin="3", anchorname="output"),
            ],
            leadlen=1,
            label=labels.chip,
        )
        regulator.right()
        self.output = regulator.pin3
        elm.Line().at(regulator.ground).toy(self.vss_input.y)
        elm.Capacitor().at(regulator.output).toy(self.vss_input.y).label("100n")
        self.vss_output = Point(dwg.here)
        elm.Line().at(regulator.output).toy(self.vdd_input.y)
        self.vdd_output = Point(dwg.here)
        elm.Vdd().label(labels.output_voltage)
        elm.Line().at(self.vss_input).to(self.vss_output)
