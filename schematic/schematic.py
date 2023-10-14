""""GPO-746 Microcontroller Schematic"""

import schemdraw
import schemdraw.elements as elm
from analog import AnalogPart
from digital import digital


def schematic(dwg: schemdraw.Drawing):
    """general setup of schematic actual diagrams in digital.py and analog.py"""
    dwg.config(fontsize=12)
    elm.style(elm.STYLE_IEC)
    anchor_point: schemdraw.util.Point = AnalogPart(dwg).draw()
    dwg.move_from(anchor_point, 4, -23)
    digital(dwg)
