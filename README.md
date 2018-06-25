# GistTests

Github gists api tests

How to run: gradlew clean test -Dtoken="GITHUB_TOKEN" && gradlew allureReport

Add -Duser="GITHUB_USERNAME" if you plan to enable Remove all gists afterClass method

Project created in Kotlin, with gradle, rest assured, testNG, allure report and assertJ

After the command allureReport the report will be generated in ./build/reports/allure-report (better to open it with Firefox)

Suites has tests results

![](Suites.bmp)

Timeline shows how it was run in parallel

![](Timeline.bmp)
