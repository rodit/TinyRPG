@echo off
set /p file=File Name: 
move %file% old_%file%
convert "old_%file%" -interpolate nearest -filter point -resize 128x128 %file%
del "old_%file%"
echo Done...
PAUSE