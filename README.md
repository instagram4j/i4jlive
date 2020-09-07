# i4jlive
Example instagram4j CLI Java app that brings Instagram LIVE to the desktop. 

The purpose is to illustrate a basic program using instagram4j library. 

This simple program
allows the creation of an instagram live post on a desktop meant to be used with a desktop livestream program like OBS. 

# Usage
1. Download release jar
2. Run in command line interface
```
java -jar i4jlive.jar username password
```
3. Type 'live' command in command line
4. Copy rtmp url and broadcast key to livestream settings (e.g. in OBS)
5. Start livestream (e.g. in OBS)
6. When ready, type 'start' command in command line
7. When finished, type 'quit' command in command line
8. Type 'quit' to exit program

## Note:
The program automatically saves logged in user session in two files, igclient.ser and igcookies.ser.
The program can then be run without providing username and password arguments.

If you want force login into a different user, please delete the two .ser files.

# Terms of Use and Information
Software is provided as is with no warranty or support. The source is licensed under MIT.

This program is no way, shape, or form affiliated or endorsed officially by Instagram.
