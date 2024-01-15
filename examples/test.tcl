# Simple test file
stencil clear white

set y 20
foreach sym {
    none arrow_solid arrow_open dot_solid dot_solid_offset dot_open dot_open_offset
} {
    stencil line -from 20,$y -tox 100 -start $sym -end $sym
    incr y 20
}
