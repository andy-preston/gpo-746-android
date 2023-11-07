"""Command line to draw the schematic and nicely format the SVG"""

from subprocess import Popen, PIPE
import schemdraw
from schematic import schematic

with schemdraw.Drawing() as drawing:
    schematic(drawing)
    drawing_xml: str = drawing.get_imagedata().decode("UTF-8")

with open("schematic.html", "r", encoding="UTF-8") as file:
    old_contents: str = file.read()
header = old_contents.split("<svg")[0]
footer = old_contents.split("</svg>")[-1]

with Popen(["./prettier.ts"], encoding="UTF-8", stdin=PIPE, stdout=PIPE) as prettier:
    prettier.stdin.write((header + drawing_xml + footer))
    pretty: str = prettier.communicate()[0].replace("!doctype", "!DOCTYPE")

with open("schematic.html", "w", encoding="UTF-8") as file:
    file.write(pretty)
