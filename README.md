\#\# _Scala Project Template_
[![Build Status](https://travis-ci.org/zeab/scala-template.svg?branch=master)](https://travis-ci.org/zeab/scala-template)
------

This is just a project shell that can be saved as a project template to speed up inital development time on new projects. 

\#\#\#\#\# **_To Save as Intellij Template_**

	1. Open this project in intellij
	2. From top menu -> Tools -> Save Project as Template
	3. Start a new project using the project template you just saved

\#\#\#\#\# **_Publishing Docker_**

Publish just the main scala template project locally<br>
```sbt dpl```<br>
Publish just the main scala template project to docker hub<br>
```sbt dp```<br>
Publish module a<br>
```sbt dpl-a```<br>
Publish module b<br>
```sbt dpl-b```<br>

\#\#\#\#\# **_Running Scala Project Template in Docker_**

```docker run --rm -p 1099:1099 -v \$HOME/logs:/opt/docker/logs zeab/scalaprojecttemplate```
