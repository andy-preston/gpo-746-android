Drive an old GPO 746 Telephone with ATtiny2313
==============================================

The long term aim is to have the phone provide input to an Android App for
making a receiving mobile calls through the handset.

At the moment, we've just got it to ring in an old fashioned UK cadence and
scan the dial when the receiver is lifted.

PCB details: https://easyeda.com/edgeeffect/phone
(The PCB is unfinished - I'm working on stripboard until it's nearer finished)

Notes
-----

avrnude: https://gitlab.com/-/snippets/2009682

Fuses (L, H, E)
ATtiny2313, 14745600 crystal version:

    avrnude t2313 FF DF FF

For testing the serial/USB link on a Raspberry Pi see ../ch340g-prototype/
