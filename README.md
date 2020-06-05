# wavey

**UPDATE 06/05/20: ** I modified this to remove JMSL, which isn't free software. It's faster and easier to use now as well. The old version using JMSL is on the `jmsl` branch if you want to look.

### About

This program uses real time data from [this NOAA Data Buoy](https://www.ndbc.noaa.gov/station_page.php?station=44090&uom=E&tz=STN), stationed in the middle of the Cape Cod Bay, to compose a long form piece of music based on the weather events that have transpired.

I used a novel method to map the cardinal directions of the wind, swells and wind waves to the circle of fifths, allowing the instruments in the piece to remain in a tonal context. This follows the paradigm that data sonification should use the data to control musical structures, instead of generating raw audio.

You can run the jar file yourself to take a listen. If you'd like, you can supply a different URL to the WaveDataFetcher to sonify a different buoy (I haven't tested this, so there may be some errors) or tweak the synth parameters to your liking. The project is set up for Eclipse.

### Usage

From the root project directory, run `java -jar Wavey.jar`.

You'll need an active internet connection, at least until the program is producing sonud. JSyn is packaged in the jarfile, so there are no requirements other than Java 8 or higher.