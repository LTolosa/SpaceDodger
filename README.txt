Luis Tolosa
let2120
COMS4160
Programming Assignment 1

SpaceDodger

SpaceDodger is a game where you control a ship as you move through a field of
asteroids and dodge them so your ship doesn't get damaged. If you happen to collide
into an asteroid, the ship will signify damage by turning pink. You control the ship by
using the WASD keys to move the ship on a plane. The plane tilts in the direction it will
go. The controls are as follows:
	W: Move the Ship up.
	A: Move the Ship left.
	S: Move the Ship down.
	D: Move the Ship right.

The asteroids are randomly generated on a plane further down. 20 of them are generated at a time
and they have varying speeds, so some may be traveling faster at you. The ship and asteroids 
also have colliders in place. This is how the program knows to turn the ship pink when
it is hit by an asteroid. When the asteroid goes behind the player they are destroyed.

This program uses the Slick utils library to be able to apply textures. I obtained the model
for the ship from turbosquid.com. I built my own .obj file loader drawing inspiration from this
youtube video https://www.youtube.com/watch?v=izKAvSV3qk0&sns=em and did some manipulations to 
fit my code. 

The submission folder will contain the bin folder, which has pre compiled class files. The lib folder
contains all the libraries used. the src folder contains all the .java file classes I have written. In the
main folder I put all the resources such as textures and obj files that I used in the project. It will also
this readme and a 10 second video clip of my program running.

To run my program, link up all the libraries in the lib folder to the project and run the Main.class file which
hold most of the code and the main function that will run the code. 