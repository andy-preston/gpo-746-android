"""Command line to draw the schematic and nicely format the SVG"""

import schemdraw
import xmlformatter
from schematic import schematic

with open("schematic.html", "r", encoding="UTF-8") as file:
    old_contents: str = file.read().replace("<!DOCTYPE html>", "")

with schemdraw.Drawing() as drawing:
    schematic(drawing)
    drawing_xml: str = drawing.get_imagedata().decode("UTF-8")

rawXml: str = (
    old_contents.split("<svg")[0] + drawing_xml + old_contents.split("</svg>")[-1]
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
