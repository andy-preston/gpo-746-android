"""Command line to draw the schematic and nicely format the SVG"""

import schemdraw
import xmlformatter
from schematic import schematic

with open("README.html", "r", encoding="UTF-8") as file:
    old_contents = file.read().replace("<!DOCTYPE html>", "")
    html_header = old_contents.split("<svg")[0]
    html_footer = old_contents.split("</svg>")[-1]

with schemdraw.Drawing() as drawing:
    schematic(drawing)
    rawXml: str = html_header + drawing.get_imagedata().decode("UTF-8") + html_footer

formatted: str = (
    xmlformatter.Formatter(
        indent="4", indent_char=" ", encoding_output="UTF-8", preserve=["literal"]
    )
    .format_string(rawXml)
    .decode("UTF-8")
)

with open("README.html", "w", encoding="UTF-8") as file:
    file.write("<!DOCTYPE html>\n")
    file.write(formatted)
