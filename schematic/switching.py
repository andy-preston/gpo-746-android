""""The switching components and the 5V power supply"""

from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from regulator import RegulatorCircuit, RegulatorLabels
from custom_elements import ElectrolyticCapacitor, OldSchoolNC


class SwitchingBoard:
    """The switching components and the 5V power supply"""

    def __init__(self, dwg: Drawing):
        self.dwg = dwg
        self.regulator_circuit = RegulatorCircuit()
        self.input_header: elm.Element
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
            headroom=1,
        )

    def solenoid_transistors(self):
        """The bell solenoids and their switching transistors"""
        elm.Line().left(2).at(self.regulator_circuit.vdd_input)
        solenoid_common = self.dwg.here
        elm.Line().left(0.4)
        diode_common = Point(self.dwg.here)
        for position in ("Left", "Right"):
            if position == "Left":
                elm.Line().up(0.2).at(solenoid_common)
            else:
                elm.Line().down(0.2).at(solenoid_common)
            solenoid = (
                elm.Transformer(t1=7, t2=0)
                .right()
                .label(
                    f"{position} solenoid",
                    loc="top" if position == "Left" else "bottom",
                )
            )
            if position == "Right":
                solenoid.flip()
            elm.Diode().reverse().at(diode_common).toy(solenoid.p1).label("1N4001")
            elm.Line().to(solenoid.p1).hold()
            elm.Line().left(2)
            elm.Line().down(1.5)
            transistor = (
                elm.BjtNpn(circle=True)
                .anchor("collector")
                .right()
                .label("BC548B", loc="right")
            )
            elm.Resistor().left().at(transistor.base).label("4K7", ofst=(0, 0.4))
            if position == "Left":
                header = elm.Header(
                    rows=2, pinsleft=["B1", "B2"], pinalignleft="center"
                ).anchor("pin1")
                elm.Line().down(0.3).at(transistor.emitter)
                elm.Vss().label("0V")
            else:
                elm.Wire("-|").to(header.pin2)
                elm.Wire("|-").at(transistor.emitter).to(
                    self.regulator_circuit.vss_input
                )

    def dial_circuit(self):
        """The whole dial debounce circuit"""
        vdd = self.regulator_circuit.vdd_output
        vss = self.regulator_circuit.vss_output
        self.dwg.move_from(self.regulator_circuit.output, 3.3, 0)
        self.input_header = elm.Header(
            pinsleft=["", "D4", "D3"],
            pinalignleft="center",
            rows=3,
            pinsright=["D2", "", ""],
            pinalignright="center",
        ).anchor("pin1")

        elm.Line().left(1.8).at(self.input_header.pin1)
        self.dwg.push()
        elm.Line().up(1.6)
        elm.Resistor().toy(vdd).label("10K")
        self.dwg.pop()
        elm.Switch().toy(vss).label("Hook", loc="top")

        elm.Line().right(10.7).at(self.input_header.pin3)
        elm.Resistor().toy(vss).label("10K").hold()
        elm.Line().right(1)

        self.dial_header = elm.Header(
            rows=4, pinsright=["blue", "pink", "grey", "orange"], pinalignright="center"
        ).anchor("pin3")
        elm.Wire("|-").at(self.dial_header.pin1).to(vdd)
        elm.Wire("|-").at(self.dial_header.pin4).to(vss)
        self.dwg.move_from(self.dial_header.pin2, -1, 0)
        elm.Line().up(2.1)
        elm.Resistor().toy(vdd).label("10K")

    def debounce(self):
        """Debounce the dial pulse line with a 555"""
        vss = self.regulator_circuit.vss_output
        vdd = self.regulator_circuit.vdd_output
        self.dwg.move_from(self.input_header.pin2, 4, 0.4)
        chip = elm.Ic(
            size=(4.5, 2.5),
            pins=[
                elm.IcPin(name="RST", side="top", pin="4"),
                elm.IcPin(name="VCC", side="top", pin="8"),
                elm.IcPin(name="OUT", side="left", pin="3"),
                elm.IcPin(name="GND", side="bottom", pin="1"),
                elm.IcPin(name="CTL", side="bottom", pin="5"),
                elm.IcPin(name="TRIG", side="right", pin="2"),
                elm.IcPin(name="THRESH", side="left", pin="6"),
                elm.IcPin(name="DIS", side="left", pin="7"),
            ],
            label="555",
            pinspacing=0.8,
        ).right()
        elm.Line().at(chip.RST).toy(vdd)
        elm.Line().at(chip.VCC).toy(vdd)
        elm.Line().left(2).at(chip.OUT)
        elm.Wire("|-").to(self.input_header.pin2)
        OldSchoolNC().down(1).at(chip.GND)
        elm.Line().toy(vss)
        OldSchoolNC().down(1).at(chip.CTL)
        elm.Capacitor().toy(vss).label("10n")
        elm.Wire("|-").at(chip.TRIG).to(self.dial_header.pin2)
        elm.Line().left(0.7).at(chip.DIS)
        elm.Resistor().toy(vdd).label("47K").hold()
        elm.Line().toy(chip.THRESH)
        elm.Line().to(chip.THRESH).hold()
        OldSchoolNC().down(1.2)
        elm.Line().down(0.5)
        OldSchoolNC().down(1)
        ElectrolyticCapacitor().toy(vss).label("1μ")

    def dial(self):
        """The actual dial in the phone"""
        self.dwg.move_from(self.dial_header.pin2, 3, -1.8)
        header = elm.Header(
            rows=5,
            pinsleft=["blue", "grey", "pink", "orange", "brown"],
            pinalignleft="center",
        )
        self.dwg.move(2, 2.5)
        dpst = (
            elm.SwitchDpst()
            .label(
                "Noramlly Open\nClosed while\ndial in motion",
                loc="right",
                ofst=(1, -0.2),
            )
            .right()
        )
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
        elm.Switch().right(2).label(
            "Normally Closed\n50ms open pulses", loc="right", ofst=(0.5, -0.2)
        )
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
        self.debounce()
        self.dial()
        self.dwg.here = self.regulator_circuit.vss_input
