@echo off
move %1 old_%1
convert "old_%1" -interpolate nearest -filter point -resize 128x128 %1
del "old_%1"
echo Done...