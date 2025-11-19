Proyecto Angular

Este proyecto fue generado utilizando Angular CLI versión 21.0.0.
Aquí encontrarás las instrucciones necesarias para instalar, ejecutar, desarrollar y construir el frontend.

🚀 Servidor de desarrollo

Para iniciar el servidor de desarrollo, ejecutá:

ng serve


Una vez en funcionamiento, abrí tu navegador y accedé a:

http://localhost:4200/


La aplicación se recargará automáticamente cada vez que realices cambios en los archivos del proyecto.

🧱 Generación de componentes (Code Scaffolding)

Angular CLI cuenta con herramientas para generar de forma rápida distintos elementos del proyecto.

Para crear un nuevo componente, ejecutá:

ng generate component nombre-del-componente


Para ver todas las opciones disponibles (componentes, directivas, pipes, servicios, módulos, etc.):

ng generate --help

🏗️ Construcción (Build)

Para compilar el proyecto y generar la versión lista para producción:

ng build


Los archivos resultantes se almacenarán en la carpeta dist/.
El build de producción aplica optimizaciones para mejorar rendimiento y velocidad de carga.

🧪 Pruebas unitarias

Para ejecutar las pruebas unitarias utilizando Karma, usá:

ng test

🔍 Pruebas end-to-end (E2E)

Para ejecutar pruebas end-to-end:

ng e2e


Angular CLI no incluye un framework E2E por defecto, por lo que podés elegir el que mejor se adapte a tu proyecto (Cypress, Playwright, Protractor alternativo, etc.).