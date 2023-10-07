"""Command line to draw the schematic and nicely format the SVG"""

import schemdraw
import xmlformatter
from schematic import schematic

with schemdraw.Drawing() as d:
    schematic(d)
    rawXml: str = d.get_imagedata().decode("UTF-8")

formatted: str = (
    xmlformatter.Formatter(
        indent="4", indent_char=" ", encoding_output="UTF-8", preserve=["literal"]
    )
    .format_string(rawXml)
    .decode("UTF-8")
)

with open("schematic.svg", "w", encoding="UTF-8") as file:
    file.write(formatted)
