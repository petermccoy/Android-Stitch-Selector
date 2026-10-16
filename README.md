# Stitch Selector

An Android app for a yarn stitching project. Each tap of **New Stitch** picks:

- a **color** at random from all colors,
- a **number of stitches** to make, from 4 to 10, and
- a **stitch** at random from the stitches not yet chosen this round. Once every stitch has
  been chosen, they all go back into the available pool.

## Pages

| Page | What it does |
| --- | --- |
| **Project** | Editable project name (default `Project1`), the current pick, scrollable lists of colors, available stitches and chosen stitches, and the **New Stitch** / **New Project** buttons. New Project puts all stitches back, clears the log, and lets you name the new project. |
| **Colors** | Add, edit or remove colors. |
| **Stitches** | Add, edit or remove stitches. |
| **Log** | Every stitch and color picked for the current project, newest first. |

Everything is saved on the phone automatically.

## Getting the app onto a phone

**Easiest:** open the repository's **Actions** tab on GitHub, open the latest *Build APK* run,
and download the `stitch-selector-apk` artifact. Unzip it, copy `app-debug.apk` to the phone,
and open it (Android will ask you to allow installing apps from that source).

**From Android Studio:** open this folder, plug in the phone with USB debugging enabled, and
press Run.

**From the command line** (needs the Android SDK): `./gradlew assembleDebug`, then
`adb install app/build/outputs/apk/debug/app-debug.apk`.
