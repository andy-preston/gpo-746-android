""""GPO-746 Microcontroller Schematic"""

from schemdraw import Drawing
from schemdraw.util import Point
import schemdraw.elements as elm
from analog import AnalogPart
from digital import digital


def schematic(dwg: Drawing):
    """general setup of schematic actual diagrams in digital.py and analog.py"""
    dwg.config(fontsize=12)
    elm.style(elm.STYLE_IEC)
    anchor_point: Point = AnalogPart(dwg).draw()
    dwg.move_from(anchor_point, 4.8, -22)
    digital(dwg)
