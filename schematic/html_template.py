"""HTML template to wrap the schematic SVG"""

TITLE = "GPO-746 Telephone Control Electronics With USB Interface"

html_prefix = [
    '<html lang="en-GB"><head>',
    f"<title>{TITLE}</title>",
    "<style>body { font-family: sans-serif; text-align: center; }</style>",
    "</head><body>",
    f"<h1>{TITLE}</h1>",
]

html_suffix = ["</body></html>"]
