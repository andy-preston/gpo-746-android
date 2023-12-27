"""78 series regulator with capacitors and voltage divider"""
from typing import Optional
from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from schemdraw.elements import Ic, IcPin
from custom_elements import ElectrolyticCapacitor


# pylint: disable=too-few-public-methods
class RegulatorCircuit:
    """78 series regulator with capacitors and voltage divider"""

    def __init__(self, dwg: Drawing, regulator_label, vdd_y: float, vss_0v_y: float):
        self.dwg = dwg
        self.regulator = Ic(
            pins=[
                IcPin(side="left", pin="1", anchorname="input"),
                IcPin(side="bottom", pin="2", anchorname="ground"),
                IcPin(side="right", pin="3", anchorname="output"),
            ],
            leadlen=1,
            label=regulator_label,
        )
        self.vss_0v_y = vss_0v_y
        self.vdd_y = vdd_y
        self.input_20v = Optional[Point]
        self.ground_point = Optional[Point]
        self.output_point = Optional[Point]

    def draw(
        self,
        input_voltage: str,
        top_resistor: str,
        bottom_resistor: str,
        output_voltage: str,
    ):
        """draw the circuit and determine the anchor points"""
        self.dwg += self.regulator.right()
        self.dwg += elm.Line().left(2).at(self.regulator.input).label(input_voltage)
        input_point = Point(self.dwg.here)

        self.dwg += (
            elm.Resistor()
            .at(input_point)
            .toy(self.vss_0v_y)
            .label(f"{bottom_resistor}\n1W", loc="bottom")
        )
        ground_left = self.dwg.here
        self.dwg += (
            elm.Resistor()
            .at(input_point)
            .toy(self.vdd_y)
            .label(f"{top_resistor}\n1W", loc="bottom")
        )
        self.input_20v = Point(self.dwg.here)
        self.dwg += elm.Vdd().label("(Supply)\n20V")

        self.dwg += (
            ElectrolyticCapacitor()
            .at(self.regulator.input)
            .toy(self.vss_0v_y)
            .label("47μ", loc="bottom")
        )
        self.dwg += elm.Line().at(self.regulator.ground).toy(self.vss_0v_y)
        self.dwg += (
            elm.Capacitor().at(self.regulator.output).toy(self.vss_0v_y).label("100n")
        )
        self.dwg += elm.Vss().label("0V")
        self.ground_point = self.dwg.here
        self.dwg += elm.Line().to(ground_left).hold()
        self.dwg += elm.Line().toy(self.vdd_y).at(self.regulator.output)
        self.dwg += elm.Vdd().label(output_voltage)
        self.output_point = self.dwg.here
