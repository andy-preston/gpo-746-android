""""GPO-746 Microcontroller Schematic"""

import schemdraw
import schemdraw.elements as elm
from analogue import analogue
from digital import digital


def schematic(dwg: schemdraw.Drawing):
    """general setup of schematic actual diagrams in digital.py and analogue.py"""
    dwg.config(fontsize=12)
    elm.style(elm.STYLE_IEC)

    anchor_point: elm.Element = analogue(dwg)
    dwg.move_from(anchor_point, 4, -25)

    digital(dwg)
