package dam.tfg.pokeplace.utils;

public class StringFormatter {
    public static String formatName(String name){
        name=name.replace("-"," "); //Cambiamos los guiones por espacios
        name=name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase(); //Ponemos la primera en mayuscula
        name=name.replace("\f"," "); //Esto quita caracteres extraños que puedan salir como simbolos o cuadrados en el texto
        return name;
    }
    public static String removeLineBreaks(String s){
        return s.replace("\n"," ");
    }
}
