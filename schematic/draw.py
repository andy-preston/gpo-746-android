"""Command line to draw the schematic and nicely format the SVG"""

import schemdraw
import xmlformatter
from html_template import html_prefix, html_suffix
from schematic import schematic

with schemdraw.Drawing() as drawing:
    schematic(drawing)
    rawXml: str = (
        "\n".join(html_prefix)
        + drawing.get_imagedata().decode("UTF-8")
        + "\n".join(html_suffix)
    )

formatted: str = (
    xmlformatter.Formatter(
        indent="4", indent_char=" ", encoding_output="UTF-8", preserve=["literal"]
    )
    .format_string(rawXml)
    .decode("UTF-8")
)

with open("schematic.html", "w", encoding="UTF-8") as file:
    file.write("<!DOCTYPE html>\n")
    file.write(formatted)
