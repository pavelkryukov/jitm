﻿# JITM

JITM is an attempt to create J2ME messenger based on [JIMM](http://www.jimm.org/) and [Java Telegram API](https://github.com/rubenlagus/TelegramApi) by Ruben Bermudez.

## PACKAGE CONTENTS

```
  |- README (this file)
  |- CHANGES (change log)
  |- COPYING (license)
  |- COPYING-Telegram-API (license for Java Telegram API)
  |- build.xml (Apache Ant buildfile)
  |- bin
  |   |- DEFAULT
  |   |   '- [List of included modules]
  |   |        '- [List of included languages]
  |   |             |- Jimm.jar (obfuscated Java archive)
  |   |             '- Jimm.jad (archive descriptor)
  |   |- MIDP2
  |   |   '- [List of included modules]
  |   |        '- [List of included languages]
  |   |             |- Jimm.jar (obfusc. Java archive, MIDP2 compatible mobiles only)
  |   |             '- Jimm.jad (archive descriptor, MIDP2 compatible mobiles only)
  |   |- RIM
  |   |   '- [List of included modules]
  |   |        '- [List of included languages]
  |   |             |- Jimm.jar (obfuscated Java archive, RIM mobiles only)
  |   |             '- Jimm.jad (archive descriptor, RIM mobiles only)
  |   '- SIEMENS2
  |   |   '- [List of included modules]
  |   |        '- [List of included languages]
  |   |             |- Jimm.jar (obfuscated Java archive, Siemens MIDP2 mobiles only)
  |   |             '- Jimm.jad (archive descriptor, Siemens MDIP2 mobiles only)
  |   '- MOTOROLA
  |       '- [List of included modules]
  |            '- [List of included languages]
  |                 |- Jimm.jar (obfuscated Java archive, Motorola MIDP2 mobiles only)
  |                 '- Jimm.jad (archive descriptor, Motorola MIDP2 mobiles only)
  |- res
  |   |- MANIFEST.MF
  |   '- [Target]
  |   |    '- [Target-specific resources]
  |   '- [MODULES]
  |        '- [Modules]
  |             '- [Module-specific resources]   
  |- src
  |   '- [Source tree]
  '- util
      |- build-nightly.xml (Apache Ant nightly buildfile)
      |- build-release.xml (Apache Ant release buildfile)
      |- jarsizetask (JarSizeTask)   
      |- jlft (Jimm Lang File Tool)
      |- langs (Jimm langauge files processor)   
      '- sijapp (Simple Java PreProcessor)
```

## HOW TO INSTALL

You can find the .jar file you need to install on your phone in one of the six
sub directories in the bin folder of this archive.
You should pick the version you need depending on the device you have:

  * DEFAULT  - Use this version if non of the other versions fits
  * MIDP2    - Use this version for devices supporting the MIDP 2 profile.
               Look in the technical specs of your device to find out if it
               supports MIDP 2.
  * RIM      - Use this version for RIM Blackberry devices.
  * SIEMENS2 - Use this version for Siemens MIDP 2 devices. (e.g. S65, CX65...)
  * MOTOROLA - Use this version for Motorola MIDP 2 devices. (e.g. V600)
  
Then choose between the different module and language configurations and copy
the Jimm.jar and Jimm.jad file to you mobile phone or use the appropriate 
installation procedure described in your devices manual.

At first start-up, select 'Options' and set your UIN and password. The default
values for the ICQ server host and port should be okay. Option values are
automatically saved and loaded on subsequent start-ups.

You can also download Jimm.jad and Jimm.jar via WAP. Open the following URL with
your WAP device and select the desired version:
<http://www.jimm.org/download/>

### KNOWN ISSUES

 * On Nokia 6230 with FW > 3.15 or other devices which stop connecting at about
   20% of the connection you have to set "Shadow connection" in the "Network" 
   section of the "Options section" to true.
   
 * On later Benq-Siemens devices you have to set the keepalive packet interval
   to some value bellow 30 seconds. (29 seconds is suggested)

 * On Nokia S60-series devices no sound or vibration, if options "Warning tones" or
   "Vibration on" disabled in "Settings/<Profile name>/Personalisation/Tones/"

### HOW TO BUILD

Jimm uses Apache Ant for building. You can download Apache Ant from
<http://ant.apache.org/>. Follow the instructions in the Apache Ant manual to
install Apache Ant.

You also must have a Java 2 Platform Standard Edition (J2SE) SDK. Depending on
the selected target you also need the following:

  * DEFAULT target: J2ME Wireless Toolkit 2.2
      <http://java.sun.com/j2me/>
  * MIDP2 and MOTOROLA target: J2ME Wireless Toolkit 2.2
      <http://java.sun.com/j2me/>
  * RIM target: Latest version of the Blackberry SDK
      <http://www.blackberry.com/developers/>
  * SIEMENS target: Latest version of the Siemens Mobility Toolkit (SMTK)
		
By default, class files are obfuscated using ProGuard to reduce bytecode size.
You can download ProGuard from <http://proguard.sourceforge.net/>. Follow the
instructions in the ProGuard manual to install ProGuard. Jimm requires ProGuard
4.2 or later. To get a reliable version it is suggested to use version 4.2 and
not the latest available version.

If you want to build the Jimm release achrive with the build.xml script provided
in the util/build-release folder you also need the '7z' (http://www.7-zip.org/)
program in the path of the console you run ant in.

Also for release only you need 'svnant' which you can obtain at http://subclipse.tigris.org/svnant.html
follow the installation instructions and make sure the .jar files are on your PATH.

To build binaries for RIM Blackberry devices you need to have Antenna and the
BlackBerry JDE Component Package installed on your system and configured via the
build.xml script. You can download Antenna from http://antenna.sourceforge.net
and the BlackBerry JDE Component Package from http://na.blackberry.com/eng/developers/downloads/.

At first, open the Apache ant buildfile (build.xml) and change the properties in
the configuration/customization section. You can use both the slash and the
backslash for separating path components.

To build Jimm, just type 'ant' or 'ant dist'. To disable class file obfuscation,
add '-Dskip-obfuscate=1' to the before mentioned command. If everything goes
well, look at the newly created directory dist/bin for Jimm.jar and Jimm.jad
files.

To clean the distribution, type 'ant clean'.

### PERFORMANCE

Jimm takes about 20 seconds to load on a Siemens S55 mobile phone. Logging into
ICQ again takes about 20 seconds. These will vary depending on your mobile phone
processor and memory performance, bandwidth and contact list size. Because of
performance limitations of older J2ME devices, it is strongly recommended to
reduce your contact list size to 50 contacts if you own one of these devices.

Delays at message sending/receiving are negligible and can be ignored.

Currently, Jimm requires about 250 KB of RAM, depending on the size of your
contact list, and the activity on the ICQ network. Jimm also requires support
for socket (TCP/IP) connections, which *is not* required by MIDP 1.0 to be
standard-compliant but some vendor implementations include. Check your mobile
phone manual.

### LICENSING

For licensing and copyright information, please see the file COPYING in the
Jimm distribution.

### DISCLAIMER

Jimm is not affiliated with or endorsed by Telegram. All trademarks belong to
their respective owners.

### CREDITS

Core development team:
  *  Denis Artyomov <artyomov at jimm.org>
  *  Manuel Linsmayer <manuel at jimm.org>
  *  Andreas Rossbacher <andreas at jimm.org>
  *  Ivan Mikitevich <mail at rad1st.ru>

Contributors:
  *  Spassky Alexander 
  		(Type-2 Message support)
  *  Perminov Andrey
        (Socks Proxy module)
  *  Sergey Chernov 
  		(Cyrillic character support)
  *  David Ethell 
  		(RIM sound and vibra support) 
  *  Mathias Huelsbusch 
  		(Mathematical art used for splash screen)
  *  Andrey B. Ivlev 
  		(Cyrillic character support)
  *  Sergey Mymrikov 
  		(Contact status and program icons)
  *  Tomas Spal 
  		(Better ContactList title update routine)
  *  Denis Stanishevskiy 
  		(Offline messages timestamp, new vibra option, remove you from users 
  		contact list)
  *  Mattias Stoeneberg
  		Message counter on splash screen, Search for f/m
  *  Dmitry Tunin 
  		(MOTOROLA build maintainer, some bugfixes)
  *  Alexander Shvedow
  		(Smiles pictures)
  *  Andrey Kazakov
  		(Status for temporary and unauthorized contacts, safe login, file
  		transfer improvement, constacts groups management)
  *  Igor Palkin
  		(Configurable hotkeys, history exporting, client info, open URL feature,
  		text templates)
  *  Vladimir Kryukov (Aspro)
		Softkeys recognition, some bugfixes
  *  Mantsurov Ivan
		Cool "Kolobok" emotions set
		

Translators:
  Jimm 0.5.x
	  *  Guy Aroshas (Hebrew translation)
	  *  Martin Bjoerkman (Swedish translation)
	  *  Sergey Chernov (Russian translation)
	  *  Ivaylo Donchev (Bulgarian translation)
	  *  Pawel Glogowski (Polish translation)
	  *  Linas Gricius (Lithuanian translation)
	  *  Andrey B. Ivlev (Russian translation)
	  *  Petr Krčmář (Czech translation)
	  *  Urukalo Milan (Serbian translation)
	  *  Mario B. Napoli (Italian translation)
	  *  Nazar Pelenjo (Ukrainian translation)
	  *  Alexei Solonets (Russian translation)
	  
  Jimm 0.6.x
	  *  Vladimir Dostovalov (French translation)
	  *  Vladimir Sojka (Czech translation)
	  *  Peter Šramo (Slovak translation)
