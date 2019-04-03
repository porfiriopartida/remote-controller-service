[![Build Status](https://travis-ci.com/ppartida/remote-controller-service.svg?branch=master)](https://travis-ci.com/ppartida/remote-controller-service)

# Remote Controller Service

A screenshot based remote automation service that allows HTTP requests to trigger
handle remote automated requests. Given a configuration file for loading the service then sending requests as
GET http://localhost/namespace/test_name will execute all the steps.
The steps will be read and executed in order

MyCustomApp:
    test_cases:
        open_application:
            steps:
              closed_application_icon.png
        close_application:
            steps:
              0|minimized_application_icon.png
              application_x.png

GET http://localhost/MyCustomApp/open_application
    Will look up for the "closed_application_icon.png" image in your screen. If found, then the mouse will move and click
    right in the center of the matching image coordinates. If the image is 10x10 px and was found in the position 100, 200,
    then the click would be in the position 5, 205.


## Getting Started

- Download the code or .jar file [#link] TODO: Add link
- Take screenshots step by step of what needs to be clicked on the screen
- Organize them in a directory (doesn't need to be inside the project directory) where permissions are granted to the application

external_resources/in/MyCustomApp/test_cases/open_application/closed_application_icon.png
external_resources/in/MyCustomApp/test_cases/close_application/minimed_application_icon.png
external_resources/in/MyCustomApp/test_cases/close_application/application_x.png

- Create a test application yml file (#sample) TODO: add sample file here

We are trying to get rid of part of the required configurations, but you mainly need to setup this:

```
spring:
  application:
    name: remote-controller
    main:
      web_environment: true
server:
  port: 8080
  context-path: /
  context_parameters:
    defaultHtmlEscape: true

mouse:
  pressDelay: 10
  maxClicks: 3
screen:
  size:
    width: 1920
    height: 1080
  capture:
    centered: true
    small:
      width: 100
      height: 50


automation:
  files:
    external_resources_path: <path_to_your_external_resources_directory> #Existing directory with all our screenshots inside "in"
    strictMode: true # All files must exist
  streaming: # The name of your namespace for this automation you can have many.
    test_cases:
      open_nfx: # The name of your first automation case
        always_click: # A section to add possible things that we may want to click only and always if they show up
          my_random_chrome_popup_close_button.png
        steps: # The steps to be clicked one by one
          google_icon.png
          nfx_favorite_icon.PNG
      still_watching: # The name of your second automation case
        always_click:
          my_random_chrome_popup_close_button.png
        steps:
          0|yes_button.png # The 0 here means that it won't wait until the image is present, 1 (by default) will wait up to 1 minute.

```

You can update above file with as many cases as needed


### Installing

From git, just clone from git: git@github.com:ppartida/remote-controller-service.git
Build the project and get yourself a .jar file

Alternative to downloading the code:
Get the .jar file from: https://github.com/ppartida/remote-controller-service/releases TODO: Cut a release!

## Running

Start the service as any other spring boot application and provide a reference for your configuration file:
java -jar myjar.jar --spring.config.location=<Path_to_your_configuration>

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/ppartida/remote-controller-service/tags).
For version names we are using dog tricks. The first one is the easiest trick, but it is also the most important because it allows us to
stablish communication now we deserve a treat.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Travis CI](https://travis-ci.com/ppartida/remote-controller-service) - Test CI

## Authors

* **[Porfirio Partida](https://github.com/ppartida)**

See also the list of [contributors](https://github.com/ppartida/remote-controller-service/graphs/contributors) who participated in this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* This uses the java.util.Robot class for most of the automation processes (if not for all) it won't work on a headless environment.
* This project started to workaround those punishing games that require you to do actions every now and then, whenever you are out
you cannot just do that, so I made this to help me collect my energies/do my daily logins in time when I'm away
The idea is to allow a mobile application/web based support for now to do the triggers

* This README was built thanks to  [PurpleBooth](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2)'s template
