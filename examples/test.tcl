# Simple test file
stencil clear white
stencil rect -at 10,10 -size 100,60 -background lightyellow
stencil line -to 10,10 -to 110,70
stencil line -to 10,70 -to 110,10
stencil label "Stencil Test" -at 60,80 -tack north

stencil rect -at 60,150 -size 60,40 -tack south
stencil rect -at 60,150 -size 12,8  -tack south
