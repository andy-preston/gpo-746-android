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
        self.input_20v: Point
        self.ground_point: Point
        self.output_point: Point

    def draw(
        self,
        dwg: Drawing,
        labels: RegulatorLabels,
        vdd_y: float,
        vss_0v_y: float,
    ):
        """draw the circuit and determine the anchor points"""
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
        elm.Line().left(2).at(regulator.input).label(labels.input_voltage)
        input_point = Point(dwg.here)
        elm.Resistor().at(input_point).toy(vss_0v_y).label(
            f"{labels.bottom_resistor}\n1W", loc="bottom"
        )
        ground_left = dwg.here
        elm.Resistor().at(input_point).toy(vdd_y).label(
            f"{labels.top_resistor}\n1W", loc="bottom"
        )
        self.input_20v = Point(dwg.here)
        elm.Vdd().label("(Supply)\n20V")
        ElectrolyticCapacitor().at(regulator.input).toy(vss_0v_y).label(
            "47μ", loc="bottom"
        )
        elm.Line().at(regulator.ground).toy(vss_0v_y)
        elm.Capacitor().at(regulator.output).toy(vss_0v_y).label("100n")
        elm.Vss().label("0V")
        self.ground_point = Point(dwg.here)
        elm.Line().to(ground_left).hold()
        elm.Line().toy(vdd_y).at(regulator.output)
        elm.Vdd().label(labels.output_voltage)
        self.output_point = dwg.here
