DataGator
=========

Spring Integration tool for common task automation including:
- download Youtube videos
- download Youtube audio tracks
- encode videos using HandbrakeCLI
- rotate videos 90 deg clockwise using ffmpeg
- encode mp3s using lame to lower filesize with lower variable bitrate
- download jpg, gif, png, and webm by scraping an html document

Dependencies for full functionality.  These should be available on `PATH`.
- [youtube-dl](http://rg3.github.io/youtube-dl/)
- [HandbrakeCLI](https://handbrake.fr/)
- [ffmpeg](https://www.ffmpeg.org/)
- [lame](http://lame.sourceforge.net/)

Building
- `maven install`

Running
- `$ java -jar ./target/DataGator-0.1.0.jar`


