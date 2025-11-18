INTEGRANTES DEL GRUPO: Borsalino Renzo Demián, Cao Gabino 

Esta aplicación utiliza la API de [WeatherBit]. Nos permite generar un listado de búsquedas de temperaturas en el momento. 
WeatherBit requiere de una Apikey, que nosotros podemos asignar dentro de ajustes. Para obtener una APIKey, tenemos que registrar en su sitio web y ahi nos otorgaran una personal. 

El proyecto consiste en poder acceder esta información, mostrarla en un listado, ver los detalles y poder incluso modificar en qué unidades quiere que nosotros lo mostremos. 
El usuario tiene la capacidad de elegir entre grados Celsius o Farenheit y hace los cálculos necesarios para pasarlos con los valores correspondientes.

Utiliza una base de datos donde se alojarán cada búsqueda lograda. En ella quedara la temperatura, la hora, la ciudad y la descripcíon del clima. Las tablas se encuentran en la
clase ClimaDbHelper, que es aquel que tiene las funcionalidades necesarias. El ClimaSingletonRepository es el repositorio que envia la orden de llevar las distintas funcionalidades 
de la base de datos como insertar o borrar elementos de la tabla. El viewModel tiene la capacidad de recibir la orden para que el repositorio pueda mandar la señal adecuada.

La clase principal es la clase Clima, donde ahi encontraremos los tipos de elementos que manejara nuestra aplicación.

ApiClient y Retrofit client en service son aquellos que van a tener la capacidad de poder hacer las consultas a la página web que nos mande un archivo json para que nuestro 
programa pueda interpretar y mostrarnos la información 

El uso de Context es necesario en el Singleton para que pueda acceder a los datos de la base de datos. Cumple con manejo de excepciones como problemas de internet o si hay errores
dentro de nuestro sistema. 
