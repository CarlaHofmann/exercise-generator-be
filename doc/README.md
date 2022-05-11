# Exercise Generator Backend - Documentation

- [Generating UML class diagrams](#generating-uml-class-diagrams)
  - [Generating a digram image](#generating-a-digram-image)


## Generating UML class diagrams

This project includes the [plantuml-generator-maven-plugin](https://github.com/devlauer/plantuml-generator) to automatically generate a [PlantUML](https://plantuml.com/) diagram from the source code. The plugin will automatically be executed during the `test` phase of the maven lifecycle. The generated digram is located under [doc/diagram.puml](diagram.puml).

### Generating a digram image

To generate an image from the PlantUML digram you need to the PlantUML executable, which can be done as a JAR file from the official github [release page](https://plantuml.com/de/download) or by using a package manager of your choice (i.E.: [Chocolatey (Windows)](https://community.chocolatey.org/packages/plantuml), [Homebrew (MacOS)](https://formulae.brew.sh/formula/plantuml), [Using APT (Ubuntu)](https://packages.ubuntu.com/jammy/plantuml)). 

After doing so you can execute the following command to generate an image:

```sh
# If using the JAR file
java -jar plantuml.jar -tpng diagram.puml
```

```sh
# If installed via package manager
plantuml -tpng diagram.puml
```

This will create a [diagram.png](diagram.png) file from the [diagram.puml](diagram.puml) file.

For further information or different file formats, please refer to the [official command line documentation](https://plantuml.com/de/command-line).