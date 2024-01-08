"""Command line to draw the schematic and nicely format the SVG"""

from re import sub
from subprocess import Popen, PIPE
from io import TextIOWrapper
from xml.etree import ElementTree
from schemdraw import Drawing
import schemdraw.elements as elm
from amplifier import AmplifierBoard
from controller import MicrocontrollerBoard
from switching import SwitchingBoard


def find_attribute(root, attribute: str, value: str):
    search_value = "" if value == "" else f"='{value}'"
    search_string = f".//*[@{attribute}{search_value}]"
    return root.findall(search_string)


def style_clean(svg_xml: str) -> str:
    """Clean up Schemdraw's default formatting markup"""
    root = ElementTree.fromstring(
        svg_xml.replace(' xmlns="http://www.w3.org/2000/svg" xml:lang="en"', "")
    )
    attributes_to_remove = [
        ["fill", "black"],
        ["font-family", "sans"],
        ["font-size", ""],
        ["transform", ""],
    ]
    for remove in attributes_to_remove:
        for child in find_attribute(root, remove[0], remove[1]):
            del child.attrib[remove[0]]
    class_substitutes = {
        "hollow": "stroke:black;fill:white;stroke-width:2.0;",
        "lines": "stroke:black;fill:none;stroke-width:2.0;",
        "dashed": "stroke-dasharray:2,3.3;",
        "rounded": "stroke-linecap:round;stroke-linejoin:round;",
        "arrow": "stroke:black;fill:black;stroke-linecap:butt;stroke-linejoin:miter;",
    }
    class_combinations = [
        ["hollow"],
        ["lines"],
        ["lines", "rounded"],
        ["lines", "dashed", "rounded"],
        ["arrow"],
    ]
    for combination in class_combinations:
        bad_style = "".join(
            map(lambda css_class: class_substitutes[css_class], combination)
        )
        for child in find_attribute(root, "style", bad_style):
            del child.attrib["style"]
            child.attrib["class"] = " ".join(combination)
    for child in root.iter():
        for attribute, value in child.attrib.items():
            child.attrib[attribute] = sub(r"\.\d+", "", value)
    return ElementTree.tostring(root, encoding="unicode")


print("\n\nRedrawing the drawing!\n========= === ========\n")
with Drawing() as drawing:
    elm.style(elm.STYLE_IEC)
    SwitchingBoard(drawing).draw()
    AmplifierBoard(drawing).draw()
    MicrocontrollerBoard(drawing).draw()
    drawing_xml: str = drawing.get_imagedata().decode("UTF-8")

with open("schematic.html", "r", encoding="UTF-8") as file:
    old_contents: str = file.read()
header = old_contents.split("<svg")[0]
footer = old_contents.split("</svg>")[-1]

with Popen(["./prettier.ts"], encoding="UTF-8", stdin=PIPE, stdout=PIPE) as prettier:
    if isinstance(prettier.stdin, TextIOWrapper):
        prettier.stdin.write(header + style_clean(drawing_xml) + footer)
        pretty: str = prettier.communicate()[0].replace("!doctype", "!DOCTYPE")
    else:
        raise RuntimeError("prettier not accepting input???")

with open("schematic.html", "w", encoding="UTF-8") as file:
    file.write(pretty)
