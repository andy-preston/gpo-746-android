"""Command line to draw the schematic and nicely format the SVG"""

from subprocess import Popen, PIPE
from io import TextIOWrapper
from schemdraw import Drawing
from schematic import schematic


def style_clean(svg_xml: str) -> str:
    """strip out the inline styles that Schemdraw uses and replace them with CSS classes"""
    classes = {
        "black_white": "stroke:black;fill:white;stroke-width:2.0;",
        "just_black": "stroke:black;fill:none;stroke-width:2.0;",
        "very_black": "stroke:black;fill:black;",
        "dashed": "stroke-dasharray:2,3.3;",
        "rounded": "stroke-linecap:round;stroke-linejoin:round;",
        "arrow": "stroke-linecap:butt;stroke-linejoin:miter;",
    }
    combinations = [
        ["black_white"],
        ["just_black"],
        ["just_black", "rounded"],
        ["just_black", "dashed", "rounded"],
        ["very_black", "arrow"],
    ]
    for combination in combinations:
        svg_xml = svg_xml.replace(
            'style="'
            + "".join(map(lambda css_class: classes[css_class], combination))
            + '"',
            'class="' + " ".join(combination) + '"',
        )
    return svg_xml


with Drawing() as drawing:
    schematic(drawing)
    drawing_xml: str = drawing.get_imagedata().decode("UTF-8")

with open("schematic.html", "r", encoding="UTF-8") as file:
    old_contents: str = file.read()
header = old_contents.split("<svg")[0]
footer = old_contents.split("</svg>")[-1]

with Popen(["./prettier.ts"], encoding="UTF-8", stdin=PIPE, stdout=PIPE) as prettier:
    if isinstance(prettier.stdin, TextIOWrapper):
        prettier.stdin.write((header + style_clean(drawing_xml) + footer))
        pretty: str = prettier.communicate()[0].replace("!doctype", "!DOCTYPE")
    else:
        raise RuntimeError("prettier not accepting input???")

with open("schematic.html", "w", encoding="UTF-8") as file:
    file.write(pretty)
