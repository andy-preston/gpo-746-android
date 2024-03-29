"""GPO-746 Microcontroller And USB Schematic"""

from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm


class MicrocontrollerBoard:
    """GPO-746 Microcontroller And USB Schematic"""

    def __init__(self, dwg: Drawing):
        self.dwg = dwg
        self._2313: elm.Ic
        self._340: elm.Ic
        self.vcc_5v: float
        self.vss_0v: float
        self.the_right: float
        self.the_left: float

    def at_tiny_2313(self) -> elm.Ic:
        """chip layout"""
        return elm.Ic(
            lofst=0.2,
            pins=[
                elm.IcPin(name="GND", side="bot", pin="10"),
                elm.IcPin(name="VCC", side="top", pin="20"),
                elm.IcPin(
                    name=r"$\overline{RESET}$", side="top", pin="1", anchorname="reset"
                ),
                elm.IcPin(name="MOSI", side="top", pin="17"),
                elm.IcPin(name="MISO", side="top", pin="18"),
                elm.IcPin(name="UCSK", side="top", pin="19"),
                elm.IcPin(name="XTAL2", side="left", pin="5"),
                elm.IcPin(name="XTAL1", side="left", pin="4"),
                elm.IcPin(name="PD4", side="left", pin="8"),
                elm.IcPin(name="PD3", side="left", pin="7"),
                elm.IcPin(name="PD2", side="left", pin="6"),
                elm.IcPin(name="PB3", side="left", pin="16"),
                elm.IcPin(name="PB2", side="left", pin="14"),
                elm.IcPin(name="PB1", side="left", pin="13"),
                elm.IcPin(name="PB4", side="right", pin="15"),
                elm.IcPin(name="PD6", side="right", pin="11"),
                elm.IcPin(name="PD5", side="right", pin="9"),
                elm.IcPin(name="PB0", side="right", pin="12"),
                elm.IcPin(name="TXD", side="right", pin="3"),
                elm.IcPin(name="RXD", side="right", pin="2"),
            ],
            pinspacing=1.37,
            leadlen=1,
            label="ATTiny 2313",
        )

    def ch_340_g(self) -> elm.Ic:
        """chip layout"""
        return elm.Ic(
            lofst=0.2,
            pins=[
                elm.IcPin(
                    name=r"$\overline{DTR}$",
                    side="left",
                    pin="13",
                    anchorname="DTR",
                ),
                elm.IcPin(
                    name=r"$\overline{RTS}$",
                    side="left",
                    pin="14",
                    anchorname="RTS",
                ),
                elm.IcPin(
                    name=r"$\overline{RI}$", side="left", pin="11", anchorname="RI"
                ),
                elm.IcPin(name="RXD", side="left", pin="3"),
                elm.IcPin(name="TXD", side="left", pin="2"),
                elm.IcPin(name="UD+", side="top", pin="5", anchorname="UDP"),
                elm.IcPin(name="UD-", side="top", pin="6", anchorname="UDM"),
                elm.IcPin(name="XO", side="right", pin="8"),
                elm.IcPin(name="XI", side="right", pin="7"),
                elm.IcPin(name="V3", side="right", pin="4"),
                elm.IcPin(name="VCC", side="top", pin="16"),
                elm.IcPin(name="GND", side="bot", pin="1"),
                elm.IcPin(
                    name=r"$\overline{CTS}$", side="bot", pin="9", anchorname="CTS"
                ),
                elm.IcPin(
                    name=r"$\overline{DSR}$", side="bot", pin="10", anchorname="DSR"
                ),
                elm.IcPin(
                    name=r"$\overline{DCD}$", side="bot", pin="12", anchorname="DCD"
                ),
                elm.IcPin(name="R232", side="bot", pin="15"),
            ],
            pinspacing=1.8,
            leadlen=1,
            label="CH340G",
        )

    def _340_clock_and_caps(self) -> float:
        """V3 Clock crystal and caps for CH340G"""
        elm.Crystal().endpoints(self._340.XI, self._340.XO).label(
            "12 MHz", loc="bottom"
        )
        elm.Capacitor().at(self._340.XO).right().label("22p", loc="bottom")
        elm.Capacitor().at(self._340.XI).right().label("22p")
        the_right: float = Point(self.dwg.here).x
        elm.Capacitor().at(self._340.V3).right().label("100n")
        elm.Line().down().toy(self._340.XI).hold()
        elm.Capacitor().up().toy(self.vcc_5v).label("100n")
        elm.Wire("|-").to(Point([self._340.VCC.x, self.vcc_5v]))
        return the_right

    def _2313_clock(self) -> float:
        """Clock crystal and caps for ATTiny 2313"""
        elm.Crystal().endpoints(self._2313.XTAL1, self._2313.XTAL2).label("14.7456 MHz")
        elm.Capacitor().at(self._2313.XTAL2).left().label("22p", loc="bottom")
        elm.Capacitor().at(self._2313.XTAL1).left().label("22p")
        elm.Line().down().toy(self._2313.XTAL2)
        the_left = Point(self.dwg.here).x
        elm.Wire("|-").to(self._2313.GND)
        return the_left

    def not_connected(self):
        """pins of the ICs that are not connected to anything"""
        for pin in [
            self._340.TXD,
            self._2313.RXD,
            self._2313.MOSI,
            self._2313.MISO,
            self._2313.UCSK,
        ]:
            elm.NoConnect().at(pin)

    def interconnect_and_ground(self) -> float:
        """simple interconnects from one IC to the other and grounding"""
        elm.Line().endpoints(self._340.RI, self._2313.PB0).label(
            "Hook up →", loc="bottom"
        )
        elm.Line().endpoints(self._340.DTR, self._2313.PD6).label(
            "← Amp. on", loc="bottom"
        )
        elm.Line().endpoints(self._340.RTS, self._2313.PD5).label(
            "← Ring", loc="bottom"
        )
        elm.Line().endpoints(self._340.RXD, self._2313.TXD).label(
            "Dialled digit →", loc="bottom"
        )
        elm.Line().at(self._2313.GND).tox(self.the_right)
        vss_0v: float = Point(self.dwg.here).y
        elm.Line().toy(self._340.XI)
        for pin in [
            self._340.GND,
            self._340.CTS,
            self._340.DSR,
            self._340.DCD,
            self._340.R232,
        ]:
            elm.Line().at(pin).toy(vss_0v)
        return vss_0v

    def main_vcc(self) -> float:
        """VCC line across the top and define where the top actually is"""
        elm.Line().up().at(self._340.VCC)
        vcc_5v: float = Point(self.dwg.here).y
        elm.Wire("-|").to(self._2313.VCC)
        return vcc_5v

    def io_header(self):
        """Header connecting the IO pins to the switching and amplifier circuitry"""
        self.dwg.move_from(self._2313.PD3, -1.5, 0.1)
        header = elm.Header(
            rows=6, pinsleft=["B1", "B2", "B3", "D2", "D3", "D4"], pinalignleft="center"
        )
        elm.Line().at(self._2313.PB1).to(header.pin1)
        elm.Line().at(self._2313.PB2).to(header.pin2)
        elm.Line().at(self._2313.PB3).to(header.pin3)
        elm.Line().at(self._2313.PD2).to(header.pin4)
        elm.Line().at(self._2313.PD3).to(header.pin5)
        elm.Line().at(self._2313.PD4).to(header.pin6)

    def usb(self):
        """The USB header and USB/main power selection switch"""
        self.dwg.move_from(self._340.UDP, -6, -1)
        header = elm.Header(
            rows=5,
            pinsleft=["5V", "D-", "D+", "CC", "GND"],
            pinalignleft="center",
        )
        elm.Line().down(0.3).at(header.pin5)
        elm.Vss().label("0V")
        elm.Resistor().right(2).at(header.pin4).label("5K1 ±10%", loc="right")
        elm.Wire("|-").to(header.pin5)
        elm.Wire("|-").at(self._340.UDP).to(header.pin3)
        elm.Wire("|-").at(self._340.UDM).to(header.pin2)
        elm.Switch().at(header.pin1).tox(self._340.UDM)
        elm.Line().toy(self.vcc_5v)

    def leds(self):
        """LED and CL Resistor for PB4 and Serial Data"""
        elm.Resistor().right(2.66).at(self._2313.PB4).label("220Ω", ofst=(0, -0.4))
        elm.LED().down().toy(self._2313.GND).label("Diagnostic", loc="top")
        self.dwg.here = (Point(self.dwg.here).x, self.vcc_5v)
        elm.Resistor().down().label("1K")
        elm.LED().toy(self._340.RXD).label("Data", loc="top")

    def _2313_reset(self):
        """Pull up resistor on 2313 reset pin"""
        elm.Line().down().at((self._2313.reset.x, self.vcc_5v))
        elm.Line().to(self._2313.reset)

    def _2313_decoupling(self):
        """Decoupling cap for the ATTiny 2313"""
        top_left = Point([self.the_left, self.vcc_5v])
        elm.Line().at(top_left).tox(self._2313.VCC).hold()
        elm.Capacitor().down().toy(self._2313.GND).label("100n")

    def vdd_vss_labels(self):
        """The voltage labels"""
        elm.Vdd().at(Point([self.the_left, self.vcc_5v])).label("5V")
        elm.Vss().at(Point([self.the_left, self.vss_0v])).label("0V")

    def draw(self):
        """Digital part of the drawing"""
        self.dwg.move(-10.3, -18)
        self._2313 = self.at_tiny_2313()
        self._2313.right()
        self.dwg.move(17, 1.9)
        self._340 = self.ch_340_g()
        self._340.right()
        self.vcc_5v = self.main_vcc()
        self.the_right = self._340_clock_and_caps()
        self.vss_0v = self.interconnect_and_ground()
        self.not_connected()
        self.usb()
        self.leds()
        self.the_left = self._2313_clock()
        self._2313_reset()
        self._2313_decoupling()
        self.io_header()
        self.vdd_vss_labels()
