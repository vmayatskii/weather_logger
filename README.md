# Weather Logger

Weather logger is simple Android program which allows you to look for a weather in your current location and save it, to take a look for it after.

## Installation

It is no any exotic ways to install Weather Logger to your device, just drop .apk file into your Android device and click on it to install

## Openweathermap API

Program uses [openweathermap](https://openweathermap.org/) API to recieve weather in current location.
```kotlin
 val key = "e969637b42055b4ad51e22ecc8b7310c" // Api key from my account from openweathermap.org
 val url = URL("http://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&APPID=${key}&units=metric")
```
where *lat* is current lattitude and *long* is longitude.
Key is unique openweathermap key, in this particular case I use my account, but it easily can be changed to yours just by changing *key* value.

## Storage

Weather savings stored in this apps Android folder (data/files) as a Json string.

## Acknowledgments

Perhaps I\`m one of the worst designers in the universe (or even the worst), so don\`t blame this app too hard for being ugly
