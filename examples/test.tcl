# Simple test file
puts "font names=[font names]"
puts "font exists sans12=[font exists sans12]"
puts "font exists nonesuch=[font exists nonesuch]"
puts "font families=[font families]"
puts "font create=[font create mono20 -family monospace -posture italic -size 20]"
puts "font cget mono20=[font cget mono20]"
stencil rect -at 10,10 -size 100,60 -linewidth 2 -background lightyellow
stencil line -to 10,10 -to 110,70
stencil line -to 10,70 -to 110,10
stencil label "Stencil Test" -at 60,80 -pos top_center -font mono20
