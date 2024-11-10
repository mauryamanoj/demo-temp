# Saudi Tourism AEM Project

## Modules

The main parts of the template are:

* sta-core: Contains common custom features like Sitemap, Solr...
* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.
* ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, templates, runmode specific configs
* ui.content: contains sample content using the components from the ui.apps

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with

    mvn clean install -PautoInstallPackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish

Or alternatively

    mvn clean install -PautoInstallPackage -Daem.port=4503

## Build Checks

* Checkstyle: automates the process of checking Java coding standards
* Junit Coverage check: you can set the project minimum coverage in %. Default 80%.
* Lombok: reduce boilerplate code for model/data objects e.g., it can generate getters and
  setters for those object automatically by using Lombok annotations. @Getter/@Setter @ToString
  @Builder @Slf4j @AllArgsConstructor

## Jenkin checks

* Sonar
* Pull Request checks and mandatory approval

## Helpful tools

* aemsync: helps in syncing code from IDE to AEM. [Aemsync](https://github.com/gavoja/aemsync)


    aemsync or aemsync -t http://admin:admin@localhost:4502 -w .

* aemCLI: to create, start, stop and much more on aem instance for current project. [repo](https://github.com/jlentink/aem)


    aem start or aem start -h
    



