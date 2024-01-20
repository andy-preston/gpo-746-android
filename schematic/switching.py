""""The switching components and the 5V power supply"""

from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from regulator import RegulatorCircuit, RegulatorLabels


class SwitchingBoard:
    """The switching components and the 5V power supply"""

    def __init__(self, dwg: Drawing):
        self.dwg = dwg
        self.regulator_circuit = RegulatorCircuit()
        self.dial_header: elm.Element

    def psu(self):
        """The 5V power supply"""
        self.regulator_circuit.draw(
            self.dwg,
            RegulatorLabels(
                chip="7805",
                input_voltage="8V",
                output_voltage="5V",
                top_resistor="1K8",
                bottom_resistor="1K2",
            ),
        )

    def solenoid_transistors(self):
        """The bell solenoids and their switching transistors"""
        elm.Line().left(2).at(self.regulator_circuit.vdd_input)
        solenoids = {
            "top": elm.Transformer(t1=7, t2=0).right(),
            "bottom": elm.Transformer(t1=7, t2=0)
            .right()
            .flip()
            .label("Bell\nSolenoids", loc="bottom"),
        }
        elm.Line().left(1)
        diode_common = Point(self.dwg.here)
        for position, solenoid in solenoids.items():
            elm.Diode().reverse().at(diode_common).toy(solenoid.p1).label("1N4001")
            elm.Line().to(solenoid.p1).hold()
            elm.Line().left(1.5)
            transistor = (
                elm.BjtNpn(circle=True)
                .anchor("collector")
                .right()
                .label("BC548B", loc="top")
            )
            elm.Resistor().at(transistor.base).left().label("4K7", ofst=(0, 0.4))
            if position == "top":
                header = elm.Header(
                    rows=2, pinsleft=["B1", "B2"], pinalignleft="center"
                ).anchor("pin1")
                elm.Vss().label("0V").at(transistor.emitter)
            else:
                elm.Wire("-|").to(header.pin2)
                elm.Wire("|-").at(transistor.emitter).to(
                    self.regulator_circuit.vss_input
                )

    def dial_circuit(self):
        """The whole dial debounce circuit"""
        self.dwg.move_from(self.regulator_circuit.output, 3.3, 0)
        vdd = self.regulator_circuit.vdd_output
        vss = self.regulator_circuit.vss_output
        io_header = elm.Header(
            pinsleft=["D4", "D3", ""],
            pinalignleft="center",
            rows=3,
            pinsright=["", "", "D2"],
            pinalignright="center",
        ).anchor("pin1")

        elm.Line().left(1.8).at(io_header.pin3)
        elm.Resistor().toy(vdd).label("10K").hold()
        elm.Switch().toy(vss).label("hook", loc="top")

        elm.Line().right(10.7).at(io_header.pin2)
        elm.Resistor().toy(vss).label("10K").hold()
        elm.Line().right(1)

        self.dial_header = elm.Header(
            rows=4, pinsright=["blue", "pink", "grey", "orange"], pinalignright="center"
        ).anchor("pin3")
        elm.Wire("|-").at(self.dial_header.pin1).to(vdd).color("blue")
        elm.Wire("|-").at(self.dial_header.pin4).to(vss).color("blue")

        elm.Line().left(1).at(self.dial_header.pin2)
        elm.Resistor().toy(vdd).label("10K").hold()


    def dial(self):
        """The actual dial in the phone"""
        self.dwg.move_from(self.dial_header.pin2, 3, -1.8)
        header = elm.Header(
            rows=5,
            pinsleft=["blue", "grey", "pink", "orange", "brown"],
            pinalignleft="center",
        )
        self.dwg.move(2, 2.5)
        dpst = elm.SwitchDpst().label("Trigger", loc="top").right()
        blue = header.pin1
        grey = header.pin2
        pink = header.pin3
        orange = header.pin4
        brown = header.pin5
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
        """Draw switching board"""
        self.psu()
        self.solenoid_transistors()
        self.dial_circuit()
        self.dial()
        self.dwg.here = self.regulator_circuit.vss_input
