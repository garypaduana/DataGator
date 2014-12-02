#DataGator

DataGator is a Spring Integration tool for common task automation including:
- download Youtube videos
- download Youtube audio tracks
- encode videos using HandbrakeCLI
- rotate videos 90 deg clockwise using ffmpeg
- encode mp3s using lame to lower the filesize with a lower, variable bitrate
- download jpg, gif, png, and webm by scraping an html document
- select a subset of pages from a pdf document
- rotate each page in a pdf document by 180 degrees

Many tasks can be performed by creating a Chrome `.url` bookmark file in the appropriate directory.  For instance, to download a video from Youtube, drag a link to the desired page into the youtube download folder.  DataGator will look for the following format when reading the file:

>[InternetShortcut]  
>URL=https://www.youtube.com/watch?v=ClPShKs9Kr0

##Dependencies
Top level dependencies should be available on `PATH`.
- [youtube-dl](http://rg3.github.io/youtube-dl/)
- [HandbrakeCLI](https://handbrake.fr/)
- [ffmpeg](https://www.ffmpeg.org/)
- [lame](http://lame.sourceforge.net/)
- [Python 2.7.8](https://www.python.org/downloads/)
  - [PyPDF2](https://pypi.python.org/pypi/PyPDF2)

##Building
- `mvn install`

##Running
- `$ java -jar ./target/DataGator-0.1.0.jar`
