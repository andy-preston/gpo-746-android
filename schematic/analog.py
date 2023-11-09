""""Only named 'analog' to draw an arbitrary distinction between the two halves"""

from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from custom_elements import OldSchoolNC, ElectrolyticCapacitor


class AnalogPart:
    """The so-called 'analog' part of the drawing"""

    def __init__(self, dwg: Drawing):
        self.dwg = dwg
        self._0v = Point([0, 0])
        self._5v = Point([0, 0])
        self._20v = Point([0, 0])
        self.hook_pin = Point([0, 0])

    def psu(self):
        """The 5V power supply"""

        _7805 = elm.Ic(
            pins=[
                elm.IcPin(side="left", pin="1", anchorname="input"),
                elm.IcPin(side="bottom", pin="2", anchorname="ground"),
                elm.IcPin(side="right", pin="3", anchorname="output"),
            ],
            leadlen=1,
            label="7805",
        )
        self.dwg += _7805.right()
        self.dwg += (
            ElectrolyticCapacitor()
            .at(_7805.input)
            .toy(self._0v.y)
            .label("0μ47", loc="bottom")
        )
        self.dwg.push()
        self.dwg += elm.Line().left()
        self.dwg += elm.Vss().label("0V")
        self.dwg += elm.Resistor().up(3.5).label("1K2 1W", loc="bottom")
        self.dwg += elm.Line().right().label("8v").hold()
        self.dwg += elm.Resistor().toy(self._5v.y).label("1K8 1W", loc="bottom")
        self.dwg += elm.Vdd().label("(Supply)\n20V")
        self._20v = Point(self.dwg.here)
        self.dwg.pop()
        self.dwg += elm.Wire("-|").to(_7805.ground)
        self.dwg += elm.Capacitor().at(_7805.output).toy(self._0v.y).label("100n")
        self.dwg += elm.Line().to(self._0v).hold()
        self.dwg += elm.Line().left()
        self.dwg += elm.Line().at(_7805.output).toy(self._5v.y)
        self.dwg += elm.Vdd().label("5V")
        self.dwg += elm.Line().to(self._5v)

    def solenoid_transistor(self, pin: str):
        """Transistor circuit for bell solenoid"""

        header = elm.Header(rows=1, pinsleft=[pin], pinalignleft="center")
        solenoid = elm.Transformer(t1=7, t2=0).right()
        if pin == "B2":
            solenoid.label("Telephone\nBell\nSolenoids", loc="bottom")
        transistor = elm.BjtNpn(circle=True).right().label("BC548B", loc="top")
        diode = elm.Diode().label("1N4001").toy(self._20v)
        self.dwg += header
        self.dwg.move_from(header.pin1, 3, 0)
        self.dwg += transistor
        self.dwg += elm.Resistor().at(transistor.base).to(header.pin1).label("4K7")
        if pin == "B1":
            self.dwg += elm.Vss().label("0V").at(transistor.emitter)
        else:
            self.dwg += elm.Wire("|-").at(transistor.emitter).to(self._0v)
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
            self._5v = Point(self.dwg.here)
        else:
            self.dwg += elm.Line().down(1.5)
            self.dwg += elm.Capacitor().down(1.5).label("100n")
            self._0v = Point(self.dwg.here)
        self.dwg += elm.Wire("-|").to(base_pin)
        self.dwg.pop()
        self.dwg += elm.Resistor().right().label("18K", loc=location)
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
            pinsleft=["", "D4", "D5"],
            pinalignleft="center",
            pinsright=["D3", "", ""],
            pinalignright="center",
        )
        self.dwg += io_header
        self.hook_pin = io_header.pin1
        self.dwg.move(8, -0.6)
        dial_header = elm.Header(
            rows=4, pinsright=["blue", "grey", "pink", "orange"], pinalignright="center"
        )
        self.dwg += dial_header
        self.debounce("top", io_header.pin2, dial_header.pin1, dial_header.pin2)
        self.debounce("bottom", io_header.pin3, dial_header.pin4, dial_header.pin3)

    def dial(self):
        """The actual dial in the phone"""
        dial_header = elm.Header(
            rows=5,
            pinsleft=["blue", "grey", "pink", "orange", "brown"],
            pinalignleft="center",
        )
        dpst = elm.SwitchDpst()

        self.dwg += dial_header
        self.dwg.move(2, 2.5)
        self.dwg += dpst
        blue = dial_header.pin1
        grey = dial_header.pin2
        pink = dial_header.pin3
        orange = dial_header.pin4
        brown = dial_header.pin5
        self.dwg += elm.Line().at(grey).right(1.2)
        self.dwg += elm.Wire("|-").to(dpst.p1)
        self.dwg += elm.Line().at(blue).up(1)
        self.dwg += elm.Line().right(3.6).label("Telephone\nDial\n\n")
        self.dwg += elm.Wire("|-").to(dpst.t1)
        self.dwg += elm.Line().at(pink).right(1.2)
        self.dwg.push()
        self.dwg += elm.Line().down(1)
        self.dwg += elm.Switch().right(2)
        self.dwg += elm.Line().down(0.75)
        self.dwg += elm.Line().left(2.5)
        self.dwg += elm.Wire("|-").to(orange)
        self.dwg.pop()
        self.dwg += elm.Line().to(dpst.p2)
        self.dwg += elm.Line().at(brown).down(1)
        self.dwg += elm.Line().right(3.6)
        self.dwg += elm.Wire("|-").to(dpst.t2)

    def hook(self):
        """connections to the cradle/hook switch"""
        self.dwg += elm.Line().at(self.hook_pin).left(2)
        self.dwg += elm.Resistor().toy(self._5v).label("10K").hold()
        self.dwg += elm.Line().down(1)
        self.dwg += elm.Switch().toy(self._0v).label("Telephone\nHook", loc="bottom")

    def draw(self) -> Point:
        """Draw the 'Analog' part - return anchor for positioning digital"""
        self.dial()
        self.dwg.move(-14, -0.5)
        self.dial_circuit()
        self.hook()
        self.dwg.move(-5, 2.5)
        self.psu()
        self.dwg.move(-22, 2)
        self.solenoid_transistor("B1")
        self.dwg.move(-7.05, -4.3)
        end_position = Point(self.dwg.here)
        self.solenoid_transistor("B2")
        return end_position
