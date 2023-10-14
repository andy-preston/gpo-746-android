"""Custom obsolete symbols that I still use."""

import math
from schemdraw.segments import Segment, SegmentArc, SegmentText
from schemdraw.elements import Element2Term

gap = [math.nan, math.nan]


class OldSchoolNC(Element2Term):
    """Old school "not connected" line"""

    def __init__(self, *d, **kwargs):
        super().__init__(*d, **kwargs)
        self.segments.append(Segment([[0, 0], [0.25, 0], gap, [0.75, 0], [1, 0]]))
        self.segments.append(SegmentArc([0.5, 0], 0.5, 0.5, 0, 180))
        self.params["theta"] = 90


class ElectrolyticCapacitor(Element2Term):
    """Obsolete version of the electrolytic capacitor symbol"""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.segments.append(
            Segment(
                [
                    [0, 0],
                    gap,
                    [0, 0.25],
                    [0, -0.25],
                    [0.2, -0.25],
                    [0.2, 0.25],
                    [0, 0.25],
                    gap,
                    [0.3, 0.25],
                    [0.3, -0.25],
                    gap,
                    [0.3, 0],
                ]
            )
        )
        self.segments.append(SegmentText([-0.18, 0.25], "+"))
