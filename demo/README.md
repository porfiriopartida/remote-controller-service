# Get the application

## From a release
Download the latest release from - https://github.com/ppartida/remote-controller-service/releases/latest:
Example: https://github.com/ppartida/remote-controller-service/releases/download/v0.1.0/remote-controller-v0.1.0.jar
remote-controller.jar

## Build your own .jar
Download the code via clone or download and build the code
```
git clone https://github.com/ppartida/remote-controller-service.git

or download from: https://github.com/ppartida/remote-controller-service/archive/master.zip
```
```
mvn package -DskipTests=true
```

#Start the application

## Windows
Run the move.bat (it will copy all the demo files into C:\)
Execute the downloaded jar with an external configuration parameter as:

```
java -jar remote-controller-v0.1.1.jar --spring.config.location=C:\tmp\demo\demo_configuration_win.yml
```


## Mac OS
Run the move.sh (it will copy all the demo files into /tmp
Execute the downloaded jar with an external configuration parameter as:)

```
java -jar remote-controller-v0.1.1.jar --spring.config.location=/tmp/demo/demo_configuration.yml
```

# Without the SH/BAT run
You can skip the step of moving the files to those directories by updating your demo_files/demo_configuration.yml file
```
automation:
  files:
    external_resources_path: /tmp # This must be the path where the files are (this directory)
```

# Next
Once the service is running you can open the numbers.png (in this directory)

You can go to your browser and open
- http://localhost:8080/automation/demo/case_1
- http://localhost:8080/automation/demo/case_2

### Case 1
The mouse will move around to 1 > 3 > 1
### Case 2
The mouse will move to 1 > 2 > 3 > 8