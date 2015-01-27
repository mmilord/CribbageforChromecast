# Cribbage for Chromecast

Simple Android app using Google's Cast APIs for a multiplayer Cribbage game using a Cast device as a game/board screen.

- Currently two player focused, expansion to 3 and 4 will follow
- Semi-working implementation of themes
- Currently built and compiled to Lollipop base
- Reciever app source available upon request


### To be able to run this app, the following will be needed:
- At least 2 Android devices running Android 5.0.1+
- A Google Cast supported device (Have not tested Google TV or Android TV, but they should work)
- A Google Cast dev account (https://cast.google.com/publish)
  - The reciever app source uploaded as a new application
  - Replace the app_id attribute in /res/values/strings.xml with the appropriate Application ID from the Cast dev console
  - The namespace attribute in the strings.xml should also match the Cast dev console


## Todo:
- Adjust custom animations for cards to more material feel [(For a more "authentic" motion)](http://www.google.com/design/spec/animation/authentic-motion.html#authentic-motion-mass-weight)
- Finish switch to MVC*
- Implement save state upon lost connection; adjust connection timeout
- Restrict cards aspect ratio to prevent stretched look on tablets*
- Switch layouts to fragments*
- Eventually implement legacy libraries to add Lollipop features to non-Lollipop devices
- Add more card styles (way way far off)

#### Current style:
<a href="http://imgur.com/bkFw3Ci"><img src="http://i.imgur.com/bkFw3Ci.png" title="source: imgur.com" /></a>
