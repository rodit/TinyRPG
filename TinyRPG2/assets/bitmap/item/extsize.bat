@echo off
set /P x=X: 
set /P y=Y: 
set /P out=Output File: 
set /A xp=x*32
set /A yp=y*32
convert -define png:size=32x32 item_sheet.png[32x32+%xp%+%yp%] %out%
resizeargs %out%
echo Done...
PAUSE