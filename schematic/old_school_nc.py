"""A custom Schemdraw element to do an old school "not connected" line"""

import math
from schemdraw.segments import Segment, SegmentArc
from schemdraw.elements import Element2Term


class OldSchoolNC(Element2Term):
    """Old school "not connected" line"""

    def __init__(self, *d, **kwargs):
        super().__init__(*d, **kwargs)
        self.segments.append(
            Segment([[0, 0], [0.25, 0], [math.nan, math.nan], [0.75, 0], [1, 0]])
        )
        self.segments.append(SegmentArc([0.5, 0], 0.5, 0.5, 0, 180))
        self.params["theta"] = 90
